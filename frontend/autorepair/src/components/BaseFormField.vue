<script setup>
  import { FormField } from '@primevue/forms'
  import InputText from 'primevue/inputtext'
  import Message from 'primevue/message'
  import IconField from 'primevue/iconfield'
  import InputIcon from 'primevue/inputicon'
  import FloatLabel from 'primevue/floatlabel'
  import Password from 'primevue/password'
  import { InputMask } from 'primevue'
  import { computed, useId } from 'vue'

  const props = defineProps({
    name: { type: String, required: true },
    icon: { type: String, default: null },
    component: {
      type: [String, Object],
      default: 'InputText',
    },
    invalid: { type: Boolean, default: false },
    inputProps: { type: Object, default: () => ({}) },
    label: { type: String, default: null },
  })
  const uniquePrefix = useId()
  const fullId = `${uniquePrefix}-${props.name}`
  const labelId = `${uniquePrefix}-label-${props.name}`
  const mergedInputProps = computed(() => {
    const baseProps = { ...props.inputProps };

    if (props.component === Password) {
      return {
        ...baseProps,
        inputId: fullId,
      };
    } else {
      return {
        ...baseProps,
        id: fullId,
      };
    }
  });

</script>
<template>
  <FormField v-slot="$field" :name="props.name">
    <IconField>
      <FloatLabel variant="on">
        <InputIcon v-if="props.icon" :class="props.icon" />
        <component
          :is="props.component"
          v-model="$field.value"
          v-bind="mergedInputProps"
          :aria-labelledby="labelId"
          :invalid="$field?.invalid || props.invalid"
          fluid
        />
        <label :id="labelId" :for="fullId" >
          {{ label || name }}
        </label>
      </FloatLabel>
    </IconField>

    <Message
      v-if="$field?.invalid"
      severity="error"
      size="small"
      variant="simple"
    >
      <ul class="my-0 flex flex-col gap-1 px-4">
        <li
          v-for="(err, index) in $field.errors || []"
          :key="index"
        >
          {{ err.message }}
        </li>
      </ul>
    </Message>
  </FormField>
</template>
