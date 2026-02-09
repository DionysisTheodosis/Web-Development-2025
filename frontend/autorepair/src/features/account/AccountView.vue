<script setup>
  import { computed, ref } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { useI18n } from 'vue-i18n'

  import { useAccountData } from './composables/useAccountData.js'
  import {
    useUserDetailsForm,
    getRoleIcon,
  } from '@/composables/useUserDetailsForm.js'
  import { useSpinner } from '@/components/spinner/useSpinner.js'
  import { useAppConfirm } from '@/composables/useAppConfirmDialog.js'


  import Button from 'primevue/button'
  import Avatar from 'primevue/avatar'
  import Chip from 'primevue/chip'
  import InputText from 'primevue/inputtext'
  import Card from 'primevue/card'
  import Fieldset from 'primevue/fieldset'
  import { Form } from '@primevue/forms'
  import Message from 'primevue/message'
  import ProgressSpinner from 'primevue/progressspinner'
  import 'primeicons/primeicons.css'
  import Spinner from '@/components/spinner/Spinner.vue'
  import { onKeyStroke } from '@vueuse/core'

  const route = useRoute()
  const router = useRouter()
  const { t } = useI18n()
  const spinnerUtils = useSpinner()


  const invalidated = ref(false)
  const formVersion = ref(0)

  const enablingEdit = ref(route.query.edit === 'true')


  const updateUrl = (queryParams) => {
    router.push({
      query: {
        ...route.query,
        ...queryParams,
      },
    })
  }
  onKeyStroke('Escape', (e) => {
    e.preventDefault()
    if (enablingEdit.value) {
      handleToggle()
    }
  })
  const toggleEdit = () => {
    const newState = !enablingEdit.value
    enablingEdit.value = newState
    if (newState) {
      updateUrl({ edit: 'true' })
    } else {
      const newQuery = { ...route.query }
      delete newQuery.edit
      router.push({ query: newQuery })
    }
  }

  const handleToggle = () => {
    if (enablingEdit.value) {
      formVersion.value++
    }
    toggleEdit()
  }

  const goToSettings = () => {
    router.push('/settings')
  }

  const {
    user,
    isLoading,
    isError,
    updateUserMutation,
    deleteAccount,
    onFormSubmit,
    areChangesDetected,
  } = useAccountData(
    () => initialValues.value,
    spinnerUtils,
    toggleEdit
  )

  const { initialValues, infoItems, formResolver } =
    useUserDetailsForm(user)

  const isSaveDisabled = computed(() => {
    return updateUserMutation.isPending.value
  })

  const { requireDelete, requireSave } = useAppConfirm()

  const confirmDeleteAccount = () => {
    requireDelete({
      header: t('accountPage.dialogs.deleteAccountTitle'),
      message: t(
        'accountPage.dialogs.deleteAccountMessage'
      ),
      accept: () => deleteAccount.mutate(),
    })
  }


  let pendingFormData = null
  const confirmSaveAccount = (formEvent) => {
    pendingFormData = formEvent
    requireSave({
      header: t('accountPage.dialogs.saveTitle'),
      message: t('accountPage.dialogs.saveMessage'),
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
  <Card class="relative m-3 mt-0">
    <template #title>
      <h1 class="mb-3 text-center text-title-2">
        {{ t('accountPage.title') }}
      </h1>
    </template>

    <template #content>
      <div
        v-if="isLoading"
        class="flex h-96 items-center justify-center"
      >
        <Spinner
          style="width: 50px; height: 50px"
          strokeWidth="8"
          fill="var(--surface-ground)"
          animationDuration=".5s"
        />
      </div>

      <Message
        v-else-if="isError"
        severity="error"
        class="m-4"
      >
        {{ t('accountPage.fetchError') }}
      </Message>

      <div
        v-else-if="user"
        class="flex flex-col justify-center lg:flex-row"
      >
        <Card
          class="m-2 h-full w-full border-primary-300 shadow-lg lg:w-3/5 xl:w-2/5 dark:border-primary-700"
        >
          <template #title>
            <div
              class="flex flex-col items-center rounded-t-lg bg-surface-100 p-4 dark:bg-surface-800"
            >
              <Avatar
                :label="
                  user.firstName?.[0] + user.lastName?.[0]
                "
                shape="circle"
                size="xlarge"
                class="mb-4 bg-primary-700 text-title-3 text-white shadow-lg"
              />
              <h2
                class="text-center text-title-6 font-extrabold text-color"
              >
                {{ user.firstName }} {{ user.lastName }}
              </h2>
              <p
                class="text-color-secondary mt-1 text-body-sm"
              >
                {{ user.email }}
              </p>
              <Chip
                :label="
                  t(
                    'roles.' +
                      user.role.toLowerCase().trim()
                  )
                "
                :icon="getRoleIcon(user.role)"
                class="mt-4 bg-primary-100 text-body-sm text-primary-900 dark:bg-primary-950 dark:text-primary-100"
              />
            </div>
          </template>

          <template #content>
            <div class="mt-6 flex flex-col gap-3">
              <Button
                :label="
                  enablingEdit
                    ? t('accountPage.cancelButton')
                    : t('accountPage.editProfile')
                "
                :icon="
                  enablingEdit
                    ? 'pi pi-times'
                    : 'pi pi-pencil'
                "
                :severity="
                  enablingEdit ? 'secondary' : 'primary'
                "
                @click="handleToggle"
              />
              <Button
                :label="
                  t('settingsPage.changePassword.button')
                "
                icon="pi pi-lock"
                severity="secondary"
                outlined
                @click="goToSettings"
              />
              <Button
                v-if="user.role !== 'SECRETARY'"
                :label="
                  t('accountPage.actions.deleteAccount')
                "
                icon="pi pi-trash"
                severity="danger"
                outlined
                @click="confirmDeleteAccount"
              />
            </div>
          </template>
        </Card>

        <div class="w-full px-4 pb-4">
          <Fieldset
            :pt="{
              root: {
                class:
                  'border-primary-300 shadow-lg dark:border-primary-700',
              },
              legend: {
                class:
                  'text-title-4 text-primary font-bold',
              },
            }"
          >
            <template #legend>
              <i class="pi pi-user mr-2 text-title-5"></i>
              {{ t('accountPage.userDetails.title') }}
              <ProgressSpinner
                v-if="isSaveDisabled"
                style="width: 20px; height: 20px"
                strokeWidth="8"
                animationDuration=".5s"
                class="ml-3"
              />
            </template>

            <Message
              v-if="invalidated"
              severity="error"
              class="mb-4"
              @close="invalidated = false"
            >
              {{ t('accountPage.updateFailed') }}
            </Message>

            <Form
              :key="formVersion"
              v-slot="$form"
              :initialValues="initialValues"
              :resolver="formResolver"
              :validateOnValueUpdate="true"
              @submit="confirmSaveAccount"
            >
              <div
                v-for="item in infoItems"
                :key="item.name"
                class="flex flex-col justify-between space-x-4 border-b border-surface-200 py-2 md:flex-row md:items-center dark:border-surface-700"
              >
                <span
                  class="text-color-secondary text-title-7 font-medium"
                >
                  <i :class="item.icon + ' mr-2'"></i>
                  {{ item.label }}:
                </span>

                <div
                  class="flex flex-col items-center justify-center md:w-auto md:items-end"
                >
                  <InputText
                    :id="'field-' + item.name"
                    :name="item.name"
                    :default-value="item.value"
                    :readonly="
                      !enablingEdit || item.name === 'role'
                    "
                    :invalid="$form[item.name]?.invalid"
                    :autocomplete="item.autocomplete"
                    fluid
                  />
                  <Message
                    v-if="$form[item.name]?.invalid"
                    severity="error"
                    size="small"
                    variant="simple"
                    class="mt-1"
                  >
                    <ul
                      class="my-0 flex flex-row gap-1 px-4"
                    >
                      <li
                        v-for="(err, index) in $form[
                          item.name
                        ]?.errors || []"
                        :key="index"
                      >
                        {{ err.message }}
                      </li>
                    </ul>
                  </Message>
                </div>
              </div>

              <div
                v-if="enablingEdit"
                class="mt-4 flex justify-end space-x-2 text-right"
              >
                <Button
                  :label="t('accountPage.cancelButton')"
                  icon="pi pi-times"
                  @click="handleToggle"
                  variant="text"
                  severity="danger"
                  :disabled="isSaveDisabled"
                />
                <Button
                  type="submit"
                  severity="primary"
                  icon="pi pi-check"
                  :label="t('accountPage.saveButton')"
                  :disabled="!$form.valid || isSaveDisabled"
                />
              </div>
            </Form>
          </Fieldset>
        </div>
      </div>
    </template>
  </Card>
</template>
