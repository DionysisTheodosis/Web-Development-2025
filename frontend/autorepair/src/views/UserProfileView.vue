<script setup>
  import { computed, ref, onMounted, watch } from 'vue'
  import { useRouter, useRoute } from 'vue-router'
  import { useI18n } from 'vue-i18n'

  import { useUserData } from '@/features/users/composables/useUserData.js'

  import { exportCustomerProfile } from '@/features/users/services/userService.js'

  import { useUserDetailsForm } from '@/composables/useUserDetailsForm.js'
  import { useSpinner } from '@/components/spinner/useSpinner.js'

  import { createUserSchema } from '@/composables/zodRegisterValidation.js'

  import { zodResolver } from '@primevue/forms/resolvers/zod'
  import Button from 'primevue/button'
  import Avatar from 'primevue/avatar'
  import Chip from 'primevue/chip'
  import InputText from 'primevue/inputtext'
  import { Form } from '@primevue/forms'
  import Message from 'primevue/message'

  import Tabs from 'primevue/tabs'
  import TabList from 'primevue/tablist'
  import Tab from 'primevue/tab'
  import TabPanels from 'primevue/tabpanels'
  import TabPanel from 'primevue/tabpanel'
  import PanelMenu from 'primevue/panelmenu'
  import DataView from 'primevue/dataview'
  import DataTable from 'primevue/datatable'
  import Column from 'primevue/column'
  import Tag from 'primevue/tag'
  import Toast from 'primevue/toast'
  import { useToast } from 'primevue/usetoast'
  import Spinner from '@/components/spinner/Spinner.vue'
  import 'primeicons/primeicons.css'
  import { onKeyStroke } from '@vueuse/core'
  import { useAppConfirm } from '@/composables/useAppConfirmDialog.js'
  import CarsView from '@/views/CarsView.vue'

  const router = useRouter()
  const route = useRoute()
  const { t } = useI18n()
  const spinnerUtils = useSpinner()
  const toast = useToast()

  // --- Local UI State ---
  const formVersion = ref(0)
  const invalidated = ref(false)
  const enablingEdit = ref(false)
  const tabViewKey = ref(0)
  const isExporting = ref(false)
  const { requireDelete, requireSave } = useAppConfirm()

  const userId = computed(() => route.params.id)

  const toggleEdit = () => {
    const newState = !enablingEdit.value
    enablingEdit.value = newState

    const queryUpdate = {
      edit: newState ? 'true' : undefined,
    }
    if (newState) queryUpdate.tab = 'info'
    updateUrl(queryUpdate)
  }

  const {
    user,
    isLoading,
    isError,
    updateUserMutation,
    deleteUserMutation,
    onFormSubmit,
    toggleActiveMutation,
  } = useUserData(
    userId.value,
    () => initialValues.value,
    spinnerUtils,
    toggleEdit
  )

  const { initialValues, infoItems } =
    useUserDetailsForm(user)

  const resolver = zodResolver(
    createUserSchema(t, 'edit_profile')
  )

  const isSaveDisabled = computed(
    () => updateUserMutation.isPending.value
  )

  const tabMap = ['info', 'cars']

  const getInitialIndex = () => {
    const tabName = route.query.tab
    const index = tabMap.indexOf(tabName)
    return index !== -1 ? index : 0
  }

  const activeTabIndex = ref(getInitialIndex())

  onKeyStroke('Escape', (e) => {
    e.preventDefault()
    if (enablingEdit.value) {
      handleToggle()
    }
  })

  watch(activeTabIndex, (newIndex) => {
    const tabName = tabMap[newIndex]
    if (route.query.tab !== tabName) {
      updateUrl({ tab: tabName })
    }
  })

  watch(
    () => route.query.tab,
    (newTabName) => {
      const index = tabMap.indexOf(newTabName)
      if (index !== -1 && index !== activeTabIndex.value) {
        activeTabIndex.value = index
      }
    }
  )

  onMounted(() => {
    enablingEdit.value = route.query.edit === 'true'
  })

  watch(
    () => route.query.edit,
    (newVal) => {
      enablingEdit.value = newVal === 'true'
    }
  )

  const handleToggle = () => {
    if (enablingEdit.value) {
      formVersion.value++
    }
    toggleEdit()
  }

  const updateUrl = (newParams) => {
    router.replace({
      query: { ...route.query, ...newParams },
    })
  }

  watch(
    user,
    (val) => {
      if (val) {
        activeTabIndex.value = getInitialIndex()
        setTimeout(() => {
          tabViewKey.value++
        }, 200)
      }
    },
    { immediate: true }
  )

  const handleExport = async (format) => {
    if (!user.value || !user.value.id) return

    if (isExporting.value) return

    isExporting.value = true
    toast.add({
      severity: 'info',
      summary: t('userProfile.toasts.exporting'),
      detail: t('userProfile.toasts.processing'),
      life: 3000,
    })

    try {
      await exportCustomerProfile(user.value.id, format)
      toast.add({
        severity: 'success',
        summary: t('userProfile.toasts.success'),
        detail:
          format === 'pdf'
            ? t('userProfile.toasts.exportPdfDetail')
            : t('userProfile.toasts.exportExcelDetail'),
        life: 3000,
      })
    } catch (error) {
      toast.add({
        severity: 'error',
        summary: t('userProfile.toasts.error'),
        detail: t('userProfile.toasts.exportFailed'),
        life: 3000,
      })
    } finally {
      isExporting.value = false
    }
  }

  const exportItems = computed(() => [
    {
      label: t('userProfile.actions.exportHistory'),
      icon: 'pi pi-file-export',
      items: [
        {
          label: t('userProfile.actions.exportPdf'),
          icon: isExporting.value
            ? 'pi pi-spin pi-spinner'
            : 'pi pi-file-pdf',
          disabled: isExporting.value,
          command: () => handleExport('pdf'),
        },
        {
          label: t('userProfile.actions.exportExcel'),
          icon: isExporting.value
            ? 'pi pi-spin pi-spinner'
            : 'pi pi-file-excel',
          disabled: isExporting.value,
          command: () => handleExport('excel'),
        },
      ],
    },
  ])

  const confirmDeleteUser = (user) => {
    requireDelete({
      header: t('userProfile.dialogs.deleteTitle'),
      message: t('userProfile.dialogs.deleteMessage', {
        username: user.username,
      }),
      accept: () => {
        deleteUserMutation.mutate(user.id)
      },
    })
  }

  const confirmToggleActive = () => {
    if (!user.value) return
    const isLocking = user.value.active
    requireDelete({
      header: isLocking
        ? t('userProfile.dialogs.lockTitle')
        : t('userProfile.dialogs.activateTitle'),
      message: isLocking
        ? t('userProfile.dialogs.lockMessage', {
            username: user.value.username,
          })
        : t('userProfile.dialogs.activateMessage', {
            username: user.value.username,
          }),
      accept: () => {
        toggleActiveMutation.mutate({
          newStatus: !user.value.active,
        })
      },
    })
  }

  let pendingFormData = null
  const confirmSaveUser = (formEvent) => {
    pendingFormData = formEvent
    requireSave({
      header: t('userProfile.dialogs.saveTitle'),
      message: t('userProfile.dialogs.saveMessage'),
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
  <div
    class="flex h-full w-full flex-col space-y-4 overflow-hidden p-3"
  >
    <div
      v-if="isLoading"
      class="flex h-full items-center justify-center"
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
      class="m-4 text-body"
    >
      {{ t('accountPage.fetchError') }}
    </Message>

    <template v-else-if="user">
      <div
        class="card flex shrink-0 flex-col items-center gap-6 p-6 md:flex-row md:items-start"
      >
        <div class="relative">
          <Avatar
            :label="
              user.firstName?.[0] + user.lastName?.[0]
            "
            shape="circle"
            size="xlarge"
            class="h-24 w-24 bg-primary-700 text-title-3 text-white shadow-lg"
          />
          <Tag
            :value="t('roles.' + user.role.toLowerCase())"
            :severity="
              user.role === 'SECRETARY' ? 'danger' : 'info'
            "
            class="absolute -bottom-2 left-1/2 -translate-x-1/2 px-3 uppercase shadow-sm"
          />
        </div>

        <div class="flex-1 text-center md:text-left">
          <h2
            class="text-title-5 font-bold text-surface-900 dark:text-surface-0"
          >
            {{ user.firstName }} {{ user.lastName }}
          </h2>
          <p
            class="text-body text-surface-500 dark:text-surface-400"
          >
            {{ user.email }}
          </p>
          <div
            class="mt-2 flex justify-center gap-2 md:justify-start"
          >
            <Chip
              v-if="!enablingEdit"
              :label="
                user.active
                  ? t('userProfile.status.active')
                  : t('userProfile.status.pending')
              "
              :icon="
                user.active
                  ? 'pi pi-check-circle'
                  : 'pi pi-clock'
              "
              :class="[
                'text-body-sm',
                user.active
                  ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400'
                  : 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400',
              ]"
            />
            <Button
              v-else
              :label="
                user.active
                  ? t('userProfile.actions.lock')
                  : t('userProfile.actions.activate')
              "
              :icon="
                user.active ? 'pi pi-lock' : 'pi pi-check'
              "
              :severity="user.active ? 'danger' : 'success'"
              :loading="
                toggleActiveMutation.isPending.value
              "
              @click="confirmToggleActive"
              outlined
              class="w-full sm:w-auto"
            />
          </div>
        </div>

        <div class="flex w-full flex-col gap-2 md:w-auto">
          <Button
            :label="
              enablingEdit
                ? t('accountPage.cancelButton')
                : t('accountPage.editProfile')
            "
            :icon="
              enablingEdit ? 'pi pi-times' : 'pi pi-pencil'
            "
            :severity="
              enablingEdit ? 'secondary' : 'primary'
            "
            @click="handleToggle"
          />

          <PanelMenu
            :model="exportItems"
            class="w-full md:w-80"
          />
          <Toast />

          <Button
            v-if="user.role !== 'SECRETARY'"
            :label="t('userProfile.actions.delete')"
            icon="pi pi-trash"
            severity="danger"
            variant="outlined"
            @click="confirmDeleteUser(user)"
          />
        </div>
      </div>

      <div
        class="card flex min-h-0 flex-1 flex-col overflow-hidden !p-0"
      >
        <Tabs
          :key="tabViewKey"
          v-model:value="activeTabIndex"
          class="flex h-full flex-col"
        >
          <TabList>
            <Tab :value="0">
              {{ t('userProfile.tabs.details') }}
            </Tab>
            <Tab :value="1" :disabled="enablingEdit">
              {{ t('userProfile.tabs.cars') }}
            </Tab>
          </TabList>

          <TabPanels
            class="flex flex-1 flex-col overflow-hidden p-0"
          >
            <TabPanel :value="0">
              <div
                class="h-full overflow-y-auto p-4 md:p-6"
              >
                <Message
                  v-if="invalidated"
                  severity="error"
                  class="mb-4 text-body"
                  @close="invalidated = false"
                >
                  {{ t('accountPage.updateFailed') }}
                </Message>

                <Form
                  :key="formVersion"
                  v-slot="$form"
                  :initialValues="initialValues"
                  :resolver="resolver"
                  :validateOnValueUpdate="true"
                  @submit="confirmSaveUser"
                  class="mx-auto max-w-4xl"
                >
                  <div
                    v-for="item in infoItems"
                    :key="item.name"
                    class="flex flex-col gap-2 border-b border-surface-200 py-4 md:flex-row md:items-center md:justify-between dark:border-surface-700"
                  >
                    <label
                      :for="'field-' + item.name"
                      class="w-1/3 text-title-7 font-medium text-surface-500 dark:text-surface-400"
                    >
                      <i :class="item.icon + ' mr-2'"></i>
                      {{ item.label }}
                    </label>

                    <div
                      class="flex w-full flex-col md:w-2/3"
                    >
                      <InputText
                        :id="'field-' + item.name"
                        :name="item.name"
                        :model-value="item.value"
                        :readonly="
                          !enablingEdit ||
                          item.name === 'role'
                        "
                        :invalid="$form[item.name]?.invalid"
                        :autocomplete="item.autocomplete"
                        class="text-body"
                      />
                      <Message
                        v-if="$form[item.name]?.invalid"
                        severity="error"
                        size="small"
                        variant="simple"
                        class="mt-1"
                      >
                        <ul
                          class="my-0 flex flex-row gap-1 px-4 text-body-sm"
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
                    class="sticky bottom-0 mt-6 flex justify-end gap-3 border-t border-surface-200 bg-surface-0 py-4 dark:border-surface-700 dark:bg-surface-900"
                  >
                    <Button
                      :label="t('accountPage.cancelButton')"
                      icon="pi pi-times"
                      severity="secondary"
                      text
                      @click="handleToggle"
                      :disabled="isSaveDisabled"
                    />
                    <Button
                      type="submit"
                      :label="t('accountPage.saveButton')"
                      icon="pi pi-check"
                      :loading="isSaveDisabled"
                      :disabled="!$form.valid"
                    />
                  </div>
                </Form>
              </div>
            </TabPanel>

            <TabPanel :value="1" class="h-full">
              <CarsView :presetOwnerId="userId" />
            </TabPanel>
          </TabPanels>
        </Tabs>
      </div>
    </template>
  </div>
</template>
