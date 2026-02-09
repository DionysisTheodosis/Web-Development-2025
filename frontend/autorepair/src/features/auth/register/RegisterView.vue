<script setup>
  import { computed, ref } from 'vue'
  import router from '@/router/index.js'

  import apiClient from '@/plugins/api-client.js'
  import { useI18n } from 'vue-i18n'

  import { Form } from '@primevue/forms'
  import InputText from 'primevue/inputtext'
  import Button from 'primevue/button'
  import Password from 'primevue/password'
  import Card from 'primevue/card'
  import Select from 'primevue/select'
  import Stepper from 'primevue/stepper'
  import StepList from 'primevue/steplist'
  import StepPanels from 'primevue/steppanels'
  import Step from 'primevue/step'
  import StepPanel from 'primevue/steppanel'
  import { InputMask } from 'primevue'
  import 'primeicons/primeicons.css'

  import { zodResolver } from '@primevue/forms/resolvers/zod'

  import { useSpinner } from '@/components/spinner/useSpinner.js'
  import Spinner from '@/components/spinner/Spinner.vue'
  import BaseFormField from '@/components/BaseFormField.vue'
  import LogoLinkButton from '@/components/LogoLinkButton.vue'
  import LocaleMenu from '@/components/locale/LocaleMenu.vue'
  import { createUserSchema } from '@/composables/zodRegisterValidation.js'

  const { t } = useI18n()

  const { loading, showSpinner, hideSpinner } = useSpinner()
  const selectedRole = ref()
  const invalidated = ref(false)

  const initialValues = ref({
    username: '',
    password: '',
    email: '',
    confirmPassword: '',
    role: '',
    firstname: '',
    lastname: '',
    personalID: '',
    address: '',
    taxNumber: '',
    specialty: '',
  })
  const baseRoles = [
    { name: 'customer' },
    { name: 'mechanic' },
  ]

  const translatedRoles = computed(() => {
    return baseRoles.map((role) => ({
      name: role.name,
      label: t(`roles.${role.name}`),
    }))
  })
  const formResolver = computed(() =>
    zodResolver(createUserSchema(t, 'register'))
  )

  const onFormSubmit = async ({ valid, values }) => {
    if (!valid) return
    try {
      showSpinner()
      let payload = {
        username: values.username.trim(),
        password: values.password.trim(),
        email: values.email.trim(),
        firstName: values.firstname.trim(),
        lastName: values.lastname.trim(),
        identityNumber: values.personalID.trim(),
        role: selectedRole.value.name.toLowerCase().trim(),
      }
      if (selectedRole.value.name === 'customer') {
        values.address = values.address.trim()
        values.taxNumber = values.taxNumber.trim()
        payload = {
          ...payload,
          address: values.address,
          taxNumber: values.taxNumber,
        }
      }
      if (selectedRole.value.name === 'mechanic') {
        values.specialty = values.specialty.trim()
        payload = {
          ...payload,
          specialty: values.specialty,
        }
      }
      await apiClient.post('/auth/register', payload)
      await router.push('/')
    } catch (error) {
      invalidated.value = true
      console.error('Register failed:', error)
    } finally {
      hideSpinner()
    }
  }
</script>
<template>
  <Card
    class="aa relative rounded-3xl bg-surface-100 p-5 px-0 shadow-lg dark:bg-surface-900"
  >
    <template #header>
      <LocaleMenu class="absolute right-4 top-4 z-10" />
    </template>
    <template #title>
      <div
        class="mb-0 mt-0 flex items-center justify-center pt-0"
      >
        <LogoLinkButton
          class="h-40 w-auto"
          color="var(--p-primary-color)"
        />
      </div>
      <h1 class="mb-2 text-center text-title-4 font-bold">
        {{ t('registerPage.title') }}
      </h1>
      <p class="mb-4 text-center text-subheading">
        {{ t('registerPage.subtitle') }}
      </p>
    </template>
    <template #content>
      <Form
        v-slot="$form"
        :initialValues="initialValues.value"
        :resolver="formResolver"
        @submit="onFormSubmit"
      >
        <Stepper
          value="1"
          linear
          :pt:StepList="{ class: 'flex flex-col' }"
        >
          <StepList
            class="flex flex-col items-start justify-center md:flex-row"
          >
            <Step value="1">
              {{ t('registerPage.step1') }}
            </Step>
            <Step value="2">
              {{ t('registerPage.step2') }}
            </Step>
            <Step value="3">
              {{ t('registerPage.step3') }}
            </Step>
            <Step value="4">
              {{ t('registerPage.step4') }}
            </Step>
          </StepList>
          <StepPanels>
            <StepPanel
              v-slot="{ activateCallback }"
              value="1"
              class="bg-surface-100 dark:bg-surface-900"
            >
              <div
                class="mx-10 mb-3 flex flex-col space-y-4"
              >
                <BaseFormField
                  :label="t('registerPage.field.username')"
                  name="username"
                  icon="pi pi-user"
                  :component="InputText"
                  :inputProps="{
                    type: 'text',
                    autocomplete: 'username',
                  }"
                  :invalid="invalidated"
                />

                <BaseFormField
                  :label="t('registerPage.field.email')"
                  name="email"
                  icon="pi pi-envelope"
                  :component="InputText"
                  :inputProps="{
                    type: 'text',
                    autocomplete: 'email',
                  }"
                  :invalid="invalidated"
                />

                <BaseFormField
                  :label="t('registerPage.field.password')"
                  name="password"
                  icon="pi pi-key"
                  :component="Password"
                  :inputProps="{
                    toggleMask: true,
                    feedback: false,
                    autocomplete: 'new-password',
                  }"
                  :invalid="invalidated"
                />

                <BaseFormField
                  :label="
                    t('registerPage.field.confirmPassword')
                  "
                  name="confirmPassword"
                  icon="pi pi-key"
                  :component="Password"
                  :inputProps="{
                    toggleMask: true,
                    feedback: false,
                    autocomplete: 'confirm-password',
                    disabled:
                      !$form.password?.valid ||
                      !$form.password?.value,
                    id: 'confirmPassword',
                  }"
                  :invalid="invalidated"
                />
              </div>
              <div class="mx-10 flex justify-end pt-6">
                <Button
                  :label="t('registerPage.nextButton')"
                  icon="pi pi-arrow-right"
                  iconPos="right"
                  @click="activateCallback('2')"
                  :disabled="
                    !$form.valid ||
                    !$form.username?.value ||
                    !$form.password?.value ||
                    !$form.email?.value
                  "
                />
              </div>
            </StepPanel>
            <StepPanel
              v-slot="{ activateCallback }"
              value="2"
              class="bg-surface-100 dark:bg-surface-900"
            >
              <div
                class="mx-10 mb-3 flex flex-col space-y-4"
              >
                <BaseFormField
                  :label="t('registerPage.field.firstname')"
                  name="firstname"
                  icon="pi pi-user"
                  :component="InputText"
                  :inputProps="{
                    type: 'text',
                    autocomplete: 'given-name',
                  }"
                  :invalid="invalidated"
                />
                <BaseFormField
                  :label="t('registerPage.field.lastname')"
                  name="lastname"
                  icon="pi pi-user"
                  :component="InputText"
                  :inputProps="{
                    type: 'text',
                    autocomplete: 'family-name',
                  }"
                  :invalid="invalidated"
                />
                <BaseFormField
                  :label="
                    t('registerPage.field.personalID')
                  "
                  name="personalID"
                  icon="pi pi-id-card"
                  :component="InputMask"
                  :inputProps="{
                    mask: 'aa999999',
                    type: 'text',
                    autocomplete: 'cc-number',
                  }"
                  :invalid="invalidated"
                />
              </div>
              <div class="mx-10 flex justify-between pt-6">
                <Button
                  :label="t('registerPage.backButton')"
                  severity="secondary"
                  icon="pi pi-arrow-left"
                  @click="activateCallback('1')"
                />

                <Button
                  :label="t('registerPage.nextButton')"
                  icon="pi pi-arrow-right"
                  iconPos="right"
                  @click="activateCallback('3')"
                  :disabled="
                    !$form.valid ||
                    !$form.username?.value ||
                    !$form.password?.value ||
                    !$form.email?.value ||
                    !$form.firstname?.value ||
                    !$form.lastname?.value ||
                    !$form.personalID?.value
                  "
                />
              </div>
            </StepPanel>
            <StepPanel
              v-slot="{ activateCallback }"
              value="3"
              class="bg-surface-100 dark:bg-surface-900"
            >
              <div
                class="mx-10 mb-3 flex flex-col space-y-4"
              >
                <Select
                  required
                  v-model="selectedRole"
                  name="roles.name"
                  :options="translatedRoles"
                  optionLabel="label"
                  :placeholder="
                    t(
                      'registerPage.field.roleSelectPlaceholder'
                    )
                  "
                  fluid
                />

                <BaseFormField
                  v-if="selectedRole?.name === 'customer'"
                  :label="t('registerPage.field.taxNumber')"
                  name="taxNumber"
                  icon="pi pi-id-card"
                  :component="InputMask"
                  :inputProps="{
                    mask: '999999999',
                    type: 'text',
                    autocomplete: 'cc-number',
                  }"
                  :invalid="invalidated"
                />

                <BaseFormField
                  v-if="selectedRole?.name === 'customer'"
                  :label="t('registerPage.field.address')"
                  name="address"
                  icon="pi pi-map-marker"
                  :component="InputText"
                  :inputProps="{
                    type: 'text',
                    autocomplete: 'address-line1',
                  }"
                  :invalid="invalidated"
                />

                <BaseFormField
                  v-if="selectedRole?.name === 'mechanic'"
                  :label="t('registerPage.field.specialty')"
                  name="specialty"
                  icon="pi pi-wrench"
                  :component="InputText"
                  :inputProps="{
                    toggleMask: true,
                    feedback: false,
                  }"
                  :invalid="invalidated"
                />
              </div>
              <div class="mx-10 flex justify-between pt-6">
                <Button
                  :label="t('registerPage.backButton')"
                  severity="secondary"
                  icon="pi pi-arrow-left"
                  @click="activateCallback('2')"
                />
                <Button
                  icon="pi pi-arrow-right"
                  :label="t('registerPage.nextButton')"
                  iconPos="right"
                  @click="activateCallback('4')"
                  :disabled="
                    !$form.valid ||
                    !$form.username?.value ||
                    !$form.password?.value ||
                    !$form.email?.value ||
                    !$form.firstname?.value ||
                    !$form.lastname?.value ||
                    !$form.personalID?.value ||
                    !selectedRole ||
                    (selectedRole?.name === 'customer' &&
                      (!$form.address?.value ||
                        !$form.taxNumber?.value)) ||
                    (selectedRole?.name === 'mechanic' &&
                      !$form.specialty?.value)
                  "
                />
              </div>
            </StepPanel>
            <StepPanel
              v-slot="{ activateCallback }"
              value="4"
              class="bg-surface-100 dark:bg-surface-900"
            >
              <div
                class="mx-10 mb-3 flex flex-col space-y-4"
              >
                <p
                  class="text-sm text-surface-600 dark:text-surface-400"
                >
                  {{ t('registerPage.reviewTitle') }}
                </p>

                <div
                  class="my-4 rounded-lg border-2 border-primary-500 bg-primary-50 p-4 dark:bg-primary-950/50"
                >
                  <div class="flex items-start">
                    <i
                      class="pi pi-info-circle mr-3 mt-1 text-lg text-primary-600 dark:text-primary-400"
                    ></i>
                    <div>
                      <p
                        class="font-bold text-primary-600 dark:text-primary-400"
                      >
                        {{
                          t(
                            'registerPage.pendingApprovalTitle'
                          )
                        }}
                      </p>
                      <p class="mt-1 text-sm">
                        <i18n-t
                          keypath="registerPage.pendingApprovalMessage"
                          tag="span"
                        >
                          <span class="font-semibold">
                            {{
                              t(
                                'registerPage.pendingApprovalEmphasis'
                              )
                            }}
                          </span>
                        </i18n-t>
                      </p>
                    </div>
                  </div>
                </div>
              </div>
              <div class="mx-10 flex justify-between pt-6">
                <Button
                  :label="t('registerPage.backButton')"
                  severity="secondary"
                  icon="pi pi-arrow-left"
                  @click="activateCallback('3')"
                />

                <div
                  v-if="loading"
                  class="flex justify-center"
                >
                  <Spinner />
                </div>
                <Button
                  v-else
                  type="submit"
                  severity="primary"
                  icon="pi pi-check"
                  :label="t('registerPage.registerButton')"
                  :disabled="
                    !$form.valid ||
                    !$form.username?.value ||
                    !$form.password?.value ||
                    !$form.email?.value ||
                    !$form.firstname?.value ||
                    !$form.lastname?.value ||
                    !$form.personalID?.value ||
                    !selectedRole ||
                    (selectedRole?.name === 'customer' &&
                      (!$form.address?.value ||
                        !$form.taxNumber?.value)) ||
                    (selectedRole?.name === 'mechanic' &&
                      !$form.specialty?.value)
                  "
                />
              </div>
            </StepPanel>
          </StepPanels>
        </Stepper>
      </Form>
    </template>
    <template #footer>
      <div
        class="flex items-center justify-center space-x-2"
      >
        <p>{{ t('registerPage.loginTitle') }}</p>
        <RouterLink
          to="/login"
          class="text-secondary font-medium hover:underline"
        >
          {{ t('registerPage.loginButton') }}
        </RouterLink>
      </div>
    </template>
  </Card>
</template>
