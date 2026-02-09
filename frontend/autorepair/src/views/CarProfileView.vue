<script setup>
  import { computed, ref, onMounted, watch } from 'vue'
  import { useRouter, useRoute } from 'vue-router'
  import { useI18n } from 'vue-i18n'


  import { useCarData } from '@/features/cars/composables/useCarData.js'
  import { useCarDetailsForm } from '@/composables/useCarDetailsForm.js'
  import { useSpinner } from '@/components/spinner/useSpinner.js'
  import { createCarSchema } from '@/composables/zodCarValidation.js'
  import { useAuth } from '@/features/auth/useAuth.js'

  import { zodResolver } from '@primevue/forms/resolvers/zod'
  import { Form } from '@primevue/forms'
  import Button from 'primevue/button'
  import Avatar from 'primevue/avatar'
  import Tag from 'primevue/tag'
  import Chip from 'primevue/chip'
  import InputText from 'primevue/inputtext'
  import InputNumber from 'primevue/inputnumber'
  import Select from 'primevue/select'
  import DatePicker from 'primevue/datepicker'
  import Message from 'primevue/message'
  import Tabs from 'primevue/tabs'
  import TabList from 'primevue/tablist'
  import Tab from 'primevue/tab'
  import TabPanels from 'primevue/tabpanels'
  import TabPanel from 'primevue/tabpanel'
  import Toast from 'primevue/toast'
  import { useToast } from 'primevue/usetoast'
  import Spinner from '@/components/spinner/Spinner.vue'
  import { onKeyStroke } from '@vueuse/core'
  import { useAppConfirm } from '@/composables/useAppConfirmDialog.js'

  const router = useRouter()
  const route = useRoute()
  const { t } = useI18n()
  const spinnerUtils = useSpinner()
  const { requireDelete, requireSave } = useAppConfirm()
  const toast = useToast()

  const carId = computed(() => route.params.id)
  const enablingEdit = ref(false)
  const formVersion = ref(0)
  const invalidated = ref(false)
  const tabViewKey = ref(0)

  const { data: currentUser } = useAuth()
  const canViewOwner = computed(
    () =>
      currentUser.value?.role === 'SECRETARY' ||
      currentUser.value?.role === 'MECHANIC'
  )
  const toggleEdit = () => {
    const newState = !enablingEdit.value
    enablingEdit.value = newState

    const queryUpdate = {
      edit: newState ? 'true' : undefined,
    }
    if (newState) queryUpdate.tab = 'details'
    updateUrl(queryUpdate)
  }


  const {
    car,
    isLoading,
    isError,
    updateCarMutation,
    deleteCarMutation,
    onFormSubmit,
  } = useCarData(
    carId.value,
    () => initialValues.value,
    spinnerUtils,
    toggleEdit
  )

  const { initialValues, infoItems } =
    useCarDetailsForm(car)
  const resolver = zodResolver(createCarSchema(t, 'edit'))

  const isSaveDisabled = computed(
    () => updateCarMutation.isPending.value
  )

  const carTypeOptions = ref([
    { label: t('carTypes.passenger'), value: 'PASSENGER' },
    { label: t('carTypes.truck'), value: 'TRUCK' },
    { label: t('carTypes.bus'), value: 'BUS' },
  ])

  const fuelTypeOptions = ref([
    { label: t('fuelType.electric'), value: 'ELECTRIC' },
    { label: t('fuelType.diesel'), value: 'DIESEL' },
    { label: t('fuelType.lpg'), value: 'LPG' },
    { label: t('fuelType.hybrid'), value: 'HYBRID' },
  ])

  const handleToggle = () => {
    if (enablingEdit.value) {
      formVersion.value++
    }
    toggleEdit()
  }

  // --- Tab Management ---
  const tabMap = ['details', 'owner']

  const getInitialIndex = () => {
    const tabName = route.query.tab
    const index = tabMap.indexOf(tabName)
    return index !== -1 ? index : 0
  }

  const activeTabIndex = ref(getInitialIndex())

  const updateUrl = (newParams) => {
    router.replace({
      query: { ...route.query, ...newParams },
    })
  }

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

  watch(
    car,
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

  onKeyStroke('Escape', (e) => {
    e.preventDefault()
    if (enablingEdit.value) {
      handleToggle()
    }
  })

  const confirmDeleteCar = (car) => {
    requireDelete({
      header: t('allCars.carList.dialogs.deleteTitle'),
      message: t('allCars.carList.dialogs.deleteMessage', {
        model: car.brand + ' ' + car.model,
      }),
      accept: () => {
        deleteCarMutation.mutate(car.id)
      },
    })
  }

  let pendingFormData = null
  const confirmSaveCar = (formEvent) => {
    pendingFormData = formEvent
    requireSave({
      header: t('carProfile.dialogs.saveTitle'),
      message: t('carProfile.dialogs.saveMessage'),
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

    <template v-else-if="car">
      <div
        class="card flex shrink-0 flex-col items-center gap-6 p-6 md:flex-row md:items-start"
      >
        <div class="relative mr-4">
          <Avatar
            icon="pi pi-car"
            shape="circle"
            size="xlarge"
            class="h-24 w-24 bg-primary-700 text-title-3 text-white shadow-lg"
          />
          <Tag
            :value="car.serialNumber"
            severity="success"
            class="absolute -bottom-2 left-1/2 -translate-x-1/2 px-3 font-mono uppercase shadow-sm"
          />
        </div>

        <div class="flex-1 text-center md:text-left">
          <h2
            class="text-title-5 font-bold text-surface-900 dark:text-surface-0"
          >
            {{ car.brand }} {{ car.model }}
          </h2>
          <p
            class="text-body text-surface-500 dark:text-surface-400"
          >
            {{
              t('carTypes.' + car.carType?.toLowerCase())
            }}
            •
            {{
              t('fuelType.' + car.fuelType?.toLowerCase())
            }}
          </p>
          <div
            class="mt-2 flex justify-center gap-2 md:justify-start"
          >
            <Chip
              :label="car.acquisitionYear?.toString()"
              icon="pi pi-calendar"
              class="bg-blue-100 text-body-sm text-blue-700 dark:bg-blue-900/30 dark:text-blue-400"
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
          <Toast />

          <Button
            :label="t('carProfile.actions.delete')"
            icon="pi pi-trash"
            severity="danger"
            variant="outlined"
            @click="confirmDeleteCar(car)"
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
              {{ t('carProfile.tabs.details') }}
            </Tab>
            <Tab
              v-if="canViewOwner"
              :value="1"
              :disabled="enablingEdit"
            >
              {{ t('carProfile.tabs.owner') }}
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
                  @submit="confirmSaveCar"
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
                      <Select
                        v-if="item.name === 'carType'"
                        :id="'field-' + item.name"
                        :name="item.name"
                        :options="carTypeOptions"
                        optionLabel="label"
                        optionValue="value"
                        :disabled="!enablingEdit"
                        :invalid="$form[item.name]?.invalid"
                        fluid
                      />

                      <Select
                        v-else-if="item.name === 'fuelType'"
                        :id="'field-' + item.name"
                        :name="item.name"
                        :options="fuelTypeOptions"
                        optionLabel="label"
                        optionValue="value"
                        :disabled="!enablingEdit"
                        :invalid="$form[item.name]?.invalid"
                        fluid
                      />

                      <InputNumber
                        v-else-if="
                          [
                            'doorCount',
                            'wheelCount',
                          ].includes(item.name)
                        "
                        :id="'field-' + item.name"
                        :name="item.name"
                        showButtons
                        :min="
                          item.name === 'doorCount' ? 2 : 3
                        "
                        :max="
                          item.name === 'doorCount' ? 8 : 10
                        "
                        :readonly="!enablingEdit"
                        :invalid="$form[item.name]?.invalid"
                        fluid
                      />

                      <DatePicker
                        v-else-if="
                          item.name === 'productionDate' &&
                          enablingEdit
                        "
                        :model-value="
                          initialValues.productionDate
                            ? new Date(
                                initialValues.productionDate
                              )
                            : ''
                        "
                        :id="'field-' + item.name"
                        :name="item.name"
                        dateFormat="yy-mm-dd"
                        :invalid="$form[item.name]?.invalid"
                        showIcon
                        fluid
                      />

                      <InputText
                        v-else-if="
                          item.name === 'productionDate'
                        "
                        :id="'field-' + item.name"
                        :model-value="
                          initialValues.productionDate
                            ? new Date(
                                initialValues.productionDate
                              ).toLocaleDateString()
                            : ''
                        "
                        readonly
                        class="text-body"
                      />

                      <InputNumber
                        v-else-if="
                          item.name === 'acquisitionYear'
                        "
                        :id="'field-' + item.name"
                        :name="item.name"
                        :useGrouping="false"
                        showButtons
                        :min="1900"
                        :max="2026"
                        buttonLayout="horizontal"
                        incrementButtonIcon="pi pi-plus"
                        decrementButtonIcon="pi pi-minus"
                        :readonly="!enablingEdit"
                        :invalid="$form[item.name]?.invalid"
                        fluid
                      />

                      <InputText
                        v-else
                        :id="'field-' + item.name"
                        :name="item.name"
                        :model-value="item.value"
                        :readonly="!enablingEdit"
                        :invalid="$form[item.name]?.invalid"
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

            <TabPanel v-if="canViewOwner" :value="1">
              <div
                v-if="car.ownerInfo"
                class="flex flex-col items-center gap-6 p-12"
              >
                <Avatar
                  icon="pi pi-user"
                  size="xlarge"
                  shape="circle"
                  class="h-24 w-24 bg-surface-200 text-surface-600"
                />
                <div class="text-center">
                  <h3
                    class="text-title-6 font-bold text-surface-900 dark:text-surface-0"
                  >
                    {{ car.ownerInfo.fullName }}
                  </h3>
                  <p
                    class="flex items-center justify-center gap-2 text-body text-surface-500"
                  >
                    <i class="pi pi-id-card"></i>
                    AFM: {{ car.ownerInfo.taxNumber }}
                  </p>
                </div>
                <Button
                  :label="t('carProfile.actions.viewOwner')"
                  icon="pi pi-external-link"
                  outlined
                  @click="
                    router.push(
                      `/users/${car.ownerInfo.id}`
                    )
                  "
                />
              </div>
              <div
                v-else
                class="p-12 text-center text-body text-surface-500"
              >
                {{ t('carProfile.owner.empty') }}
              </div>
            </TabPanel>
          </TabPanels>
        </Tabs>
      </div>
    </template>
  </div>
</template>
