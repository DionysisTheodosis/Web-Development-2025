<script setup>
  import Fieldset from 'primevue/fieldset'
  import Password from 'primevue/password'
  import { computed, ref } from 'vue'
  import apiClient from '@/plugins/api-client.js'
  import { useI18n } from 'vue-i18n'
  import { Form } from '@primevue/forms'
  import Card from 'primevue/card'
  import 'primeicons/primeicons.css'
  import { useRouter } from 'vue-router'
  import { z } from 'zod'
  import { zodResolver } from '@primevue/forms/resolvers/zod'
  import Select from 'primevue/select'
  import { useSpinner } from '@/components/spinner/useSpinner.js'
  import BaseFormField from '@/components/BaseFormField.vue'
  import Button from 'primevue/button'
  import { useDarkModeStore } from '@/stores/theme.js'
  import {
    useMutation,
    useQueryClient,
  } from '@tanstack/vue-query'
  import { useAppConfirm } from '@/composables/useAppConfirmDialog.js'

  const router = useRouter()
  const { t, locale, availableLocales } = useI18n()
  const { loading, showSpinner, hideSpinner } = useSpinner()

  const invalidated = ref(false)
  const queryClient = useQueryClient()
  const globalAuthQueryKey = ['user', 'me']


  const languageOptions = computed(() => {
    return availableLocales.map((code) => ({
      label: t(`locale.${code}.full`),
      value: code,
    }))
  })
  const selectedLanguage = computed({
    get: () => locale.value,
    set: (value) => {
      locale.value = value
    },
  })

  const themeStore = useDarkModeStore()
  const themeOptions = computed(() => [
    {
      label: t('themeMode.system'),
      icon: 'pi pi-desktop',
      value: 'system',
    },
    {
      label: t('themeMode.light'),
      icon: 'pi pi-sun',
      value: 'light',
    },
    {
      label: t('themeMode.my-app-dark'),
      icon: 'pi pi-moon',
      value: 'my-app-dark',
    },
  ])

  const selectedThemeMode = computed({
    get: () => themeStore.themePreference,
    set: (mode) => themeStore.setDarkMode(mode),
  })

  const initialValues = ref({
    oldPassword: '',
    newPassword: '',
    confirmPassword: '',
  })

  const formResolver = computed(() =>
    zodResolver(
      z
        .object({
          oldPassword: z.string().nonempty({
            message: t('registerPage.validation.required'),
          }),
          newPassword: z
            .string()
            .min(8, {
              message: t(
                'registerPage.validation.lengthAtLeast8'
              ),
            })
            .refine((value) => /[a-z]/.test(value), {
              message: t(
                'registerPage.validation.passLowercase'
              ),
            })
            .refine((value) => /[A-Z]/.test(value), {
              message: t(
                'registerPage.validation.passUppercase'
              ),
            })
            .refine((value) => /\d/.test(value), {
              message: t(
                'registerPage.validation.passNumber'
              ),
            })
            .refine(
              (value) => /[^a-zA-Z0-9\s]/.test(value),
              {
                message: t(
                  'registerPage.validation.passSpecialChar'
                ),
              }
            ),
          confirmPassword: z.string().nonempty({
            message: t('registerPage.validation.required'),
          }),
        })
        .superRefine((data, ctx) => {
          if (
            data.newPassword &&
            data.confirmPassword &&
            data.newPassword !== data.confirmPassword
          ) {
            ctx.addIssue({
              code: 'custom',
              path: ['confirmPassword'],
              message: t(
                'registerPage.validation.passwordsMismatch'
              ),
            })
          }
        })
    )
  )
  const changePasswordApi = async (payload) => {
    const { data } = await apiClient.patch(
      '/users/me/password',
      payload
    )
    return data
  }

  const passwordMutation = useMutation({
    mutationFn: changePasswordApi,
    onMutate: () => {
      invalidated.value = false
      showSpinner()
    },
    onSuccess: async () => {
      queryClient.setQueryData(globalAuthQueryKey, null)
      queryClient.removeQueries({
        queryKey: globalAuthQueryKey,
      })
      await router.replace('/login')
    },
    onError: (error) => {
      console.error('Change password failed:', error)
      invalidated.value = true
    },
    onSettled: () => {
      hideSpinner()
    },
  })

  const onFormSubmit = ({ valid, values }) => {
    if (!valid || passwordMutation.isPending.value) return
    invalidated.value = false
    const payload = {
      oldPassword: values.oldPassword.trim(),
      newPassword: values.newPassword.trim(),
    }
    passwordMutation.mutate(payload)
  }

  // --- Confirmation Dialog ---
  const { requireSave } = useAppConfirm()
  let pendingFormData = null
  const confirmChangePassword = (formEvent) => {
    pendingFormData = formEvent
    requireSave({
      header: t('settingsPage.dialogs.changePasswordTitle'),
      message: t(
        'settingsPage.dialogs.changePasswordMessage'
      ),
      accept: () => {
        if (pendingFormData) {
          onFormSubmit(pendingFormData)
          pendingFormData = null
        }
      },
      reject: () => {
        pendingFormData = null
      },
    })
  }
</script>
<template>
  <Card class="relative m-2 p-2">
    <template #title>
      <h1 class="mb-8 text-center text-title-2">
        {{ t('settingsPage.title') }}
      </h1>
    </template>

    <template #content>
      <div
        class="flex flex-col gap-8 lg:flex-row lg:items-start lg:justify-center lg:gap-16"
      >
        <Fieldset
          :pt="{
            contentContainer: {
              class:
                'flex flex-col justify-center p-2 items-center ',
            },
          }"
        >
          <template #legend>
            <span
              class="flex items-center px-2 text-title-4 text-primary"
            >
              <i class="pi pi-lock mr-2 text-title-4"></i>
              {{ t('settingsPage.changePassword.title') }}
            </span>
          </template>

          <Form
            formRef="formState"
            v-slot="$form"
            :initialValues="initialValues.value"
            :resolver="formResolver"
            @submit="confirmChangePassword"
            class="mt-2 flex flex-col space-y-5"
          >
            <BaseFormField
              :label="
                t('settingsPage.changePassword.oldPassword')
              "
              name="oldPassword"
              icon="pi pi-key"
              :component="Password"
              :inputProps="{
                toggleMask: true,
                feedback: false,
                autocomplete: 'old-password',
              }"
            />

            <BaseFormField
              :label="
                t('settingsPage.changePassword.newPassword')
              "
              name="newPassword"
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
                t(
                  'settingsPage.changePassword.confirmPassword'
                )
              "
              name="confirmPassword"
              icon="pi pi-key"
              :component="Password"
              :inputProps="{
                toggleMask: true,
                feedback: false,
                autocomplete: 'confirm-password',
                disabled:
                  !$form.newPassword?.valid ||
                  !$form.newPassword?.value,
              }"
              :invalid="invalidated"
            />

            <div class="flex justify-center pt-4">
              <Button
                :loading="loading"
                type="submit"
                severity="primary"
                :label="
                  t('settingsPage.changePassword.button')
                "
                class="w-full"
                :disabled="
                  !$form.valid ||
                  !$form.newPassword?.value ||
                  !$form.oldPassword?.value ||
                  !$form.confirmPassword?.value
                "
              />
            </div>
          </Form>
        </Fieldset>

        <!-- System Settings Section -->
        <Fieldset
          :pt="{
            contentContainer: {
              class:
                'flex flex-1 justify-center p-4 items-center ',
            },
          }"
        >
          <template #legend>
            <span
              class="flex items-center px-2 text-title-4 text-primary"
            >
              <i
                class="pi pi-desktop mr-2 text-title-4"
              ></i>
              {{ t('settingsPage.systemSettings.title') }}
            </span>
          </template>

          <div
            class="mt-2 grid grid-cols-1 gap-6 sm:w-full sm:max-w-lg sm:grid-cols-2"
          >
            <div class="contents">
              <label
                for="language-select"
                class="flex shrink-0 items-center text-body-big sm:justify-end sm:text-start"
              >
                <i class="pi pi-language mr-2"></i>
                {{
                  t(
                    'settingsPage.systemSettings.languageLabel'
                  )
                }}
              </label>

              <Select
                id="language-select"
                v-model="selectedLanguage"
                :options="languageOptions"
                optionLabel="label"
                optionValue="value"
                placeholder="Select Language"
                class="w-full"
              />
            </div>

            <div class="contents">
              <label
                for="theme-select"
                class="flex shrink-0 items-center text-body-big sm:justify-end sm:text-start"
              >
                <i class="pi pi-sun mr-2"></i>
                {{
                  t(
                    'settingsPage.systemSettings.themeModeLabel'
                  )
                }}
              </label>

              <Select
                id="theme-select"
                v-model="selectedThemeMode"
                :options="themeOptions"
                optionLabel="label"
                optionValue="value"
                placeholder="Select Theme"
                class="w-full"
              >
                <template #option="slotProps">
                  <div class="flex items-center space-x-2">
                    <i :class="slotProps.option.icon"></i>
                    <span>
                      {{ slotProps.option.label }}
                    </span>
                  </div>
                </template>

                <template #value="slotProps">
                  <div
                    v-if="slotProps.value"
                    class="flex items-center space-x-2"
                  >
                    <i
                      :class="
                        themeOptions.find(
                          (opt) =>
                            opt.value === slotProps.value
                        )?.icon
                      "
                    ></i>
                    <span>
                      {{
                        themeOptions.find(
                          (opt) =>
                            opt.value === slotProps.value
                        )?.label
                      }}
                    </span>
                  </div>
                  <span v-else>
                    {{ slotProps.placeholder }}
                  </span>
                </template>
              </Select>
            </div>
          </div>
        </Fieldset>
      </div>
    </template>
  </Card>
</template>
