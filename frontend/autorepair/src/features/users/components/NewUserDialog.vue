<template>
  <Dialog
    :visible="visible"
    @update:visible="emit('update:visible', $event)"
    :header="dialogTitle"
    :modal="true"
    class="p-fluid"
  >
    <Form
      v-slot="$form"
      :initialValues="initialValues"
      :resolver="formResolver"
      :validateOnValueUpdate="true"
      @submit="handleFormSubmit"
      class="flex flex-col gap-4"
    >
      <BaseFormField
        name="username"
        :label="t('newUserDialog.field.username')"
        icon="pi pi-user"
        :component="InputText"
      />
      <BaseFormField
        name="email"
        :label="t('newUserDialog.field.email')"
        icon="pi pi-envelope"
        :component="InputText"
      />
      <BaseFormField
        name="password"
        :label="t('newUserDialog.field.password')"
        icon="pi pi-lock"
        :component="Password"
        :inputProps="{
          toggleMask: true,
          feedback: false,
          autocomplete: 'new-password',
        }"
      />

      <div class="flex gap-4">
        <div class="w-1/2">
          <BaseFormField
            name="firstname"
            :label="t('newUserDialog.field.firstname')"
            icon="pi pi-user"
            :component="InputText"
          />
        </div>
        <div class="w-1/2">
          <BaseFormField
            name="lastname"
            :label="t('newUserDialog.field.lastname')"
            icon="pi pi-user"
            :component="InputText"
          />
        </div>
      </div>

      <BaseFormField
        name="personalID"
        :label="t('newUserDialog.field.personalID')"
        icon="pi pi-id-card"
        :component="InputText"
      />

      <BaseFormField
        name="role"
        :label="t('role')"
        :component="Select"
        :inputProps="{
          options: roleOptions,
          optionLabel: 'label',
          optionValue: 'value',
          placeholder: t('newUserDialog.field.roleSelectPlaceholder'),
          disabled: !!props.defaultRole
        }"
      />

      <div
        v-if="$form.role?.value === 'customer'"
        class="flex flex-col gap-4 rounded border-l-4 border-blue-500 p-2 pl-4"
      >
        <BaseFormField
          name="address"
          :label="t('newUserDialog.field.address')"
          icon="pi pi-home"
          :component="InputText"
        />
        <BaseFormField
          name="taxNumber"
          :label="t('newUserDialog.field.taxNumber')"
          icon="pi pi-briefcase"
          :component="InputText"
        />
      </div>

      <div
        v-if="$form.role?.value === 'mechanic'"
        class="flex flex-col gap-4 rounded border-l-4 border-orange-500 p-2 pl-4"
      >
        <BaseFormField
          name="specialty"
          :label="t('newUserDialog.field.specialty')"
          icon="pi pi-cog"
          :component="InputText"
        />
      </div>

      <div class="mt-4 flex justify-end gap-2 border-t pt-4">
        <Button
          :label='t("newUserDialog.cancelButtonLabel")'
          icon="pi pi-times"
          text
          severity="secondary"
          @click="emit('update:visible', false)"
        />

        <Button
          type="submit"
          :label='t("newUserDialog.submitButtonLabel")'
          icon="pi pi-check"
          :loading="isPending.value"
        />
      </div>
    </Form>
  </Dialog>
</template>

<script setup>
  import { computed, ref, watch } from 'vue';
  import { useI18n } from 'vue-i18n';
  import { zodResolver } from '@primevue/forms/resolvers/zod';

  import Dialog from 'primevue/dialog';
  import Button from 'primevue/button';
  import InputText from 'primevue/inputtext';
  import Password from 'primevue/password';
  import Select from 'primevue/select';
  import { Form } from '@primevue/forms';
  import BaseFormField from '@/components/BaseFormField.vue';


  import { createUserSchema } from '@/composables/zodRegisterValidation.js';
  import { useCreateUser } from '@/features/users/composables/useCreateUser.js';


  const props = defineProps({
    visible: Boolean,
    defaultRole: {
      type: String,
      default: null
    }
  });

  const emit = defineEmits(['update:visible', 'success']);
  const { t } = useI18n();


  const { isPending, handleFormSubmit } = useCreateUser(() => {
    emit('update:visible', false);
    emit('success');
  });


  const roleKeys = ['customer', 'mechanic'];

  const roleOptions = computed(() => {
    return roleKeys.map((role) => ({
      label: t(`roles.${role}`),
      value: role,
    }));
  });


  const formResolver = computed(() => zodResolver(createUserSchema(t, 'edit_password')));


  const initialValues = ref({
    username: '',
    email: '',
    password: '',
    firstname: '',
    lastname: '',
    personalID: '',
    role: 'customer',
    address: '',
    taxNumber: '',
    specialty: '',
  });


  watch(() => props.visible, (isOpen) => {
    if (isOpen) {
      const startRole = props.defaultRole ? props.defaultRole.toLowerCase() : 'customer';

      initialValues.value = {
        username: '',
        email: '',
        password: '',
        firstname: '',
        lastname: '',
        personalID: '',
        role: startRole,
        address: '',
        taxNumber: '',
        specialty: '',
      };
    }
  });


  const dialogTitle = computed(() => {
    if (props.defaultRole) {
      return t('newUserDialog.title') + ' - ' + t(`roles.${props.defaultRole.toLowerCase()}`);
    }
    return t('newUserDialog.title');
  });

</script>