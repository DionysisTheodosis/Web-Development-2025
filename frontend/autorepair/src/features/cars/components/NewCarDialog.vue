<template>
  <Dialog
    :visible="visible"
    @update:visible="emit('update:visible', $event)"
    :header="t('newCarDialog.title')"
    :modal="true"
    class="p-fluid w-full max-w-2xl"
  >
    <Form
      v-slot="$form"
      :initialValues="initialValues"
      :resolver="formResolver"
      :validateOnValueUpdate="true"
      @submit="handleFormSubmit"
      class="flex flex-col gap-4"
    >
      <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
        <BaseFormField
          name="serialNumber"
          :label="t('newCarDialog.field.serialNumber')"
          icon="pi pi-barcode"
          :component="InputText"
        />
        <BaseFormField
          name="brand"
          :label="t('newCarDialog.field.brand')"
          icon="pi pi-tag"
          :component="InputText"
        />
        <BaseFormField
          name="model"
          :label="t('newCarDialog.field.model')"
          icon="pi pi-car"
          :component="InputText"
        />
        <BaseFormField
          name="carType"
          :label="t('newCarDialog.field.type')"
          :component="Select"
          :inputProps="{
            options: typeOptions,
            optionLabel: 'label',
            optionValue: 'value',
          }"
        />
        <BaseFormField
          name="fuelType"
          :label="t('newCarDialog.field.fuelType')"
          :component="Select"
          :inputProps="{
            options: fuelOptions,
            optionLabel: 'label',
            optionValue: 'value',
          }"
        />
        <BaseFormField
          name="doorCount"
          :label="t('newCarDialog.field.doors')"
          :component="InputNumber"
          :inputProps="{
            showButtons: true,
            min: 2,
            max: 8,
          }"
        />
        <BaseFormField
          name="wheelCount"
          :label="t('newCarDialog.field.wheels')"
          :component="InputNumber"
          :inputProps="{
            showButtons: true,
            min: 3,
            max: 10,
          }"
        />
        <BaseFormField
          name="productionDate"
          :label="t('newCarDialog.field.productionDate')"
          :component="DatePicker"
          :inputProps="{
            showIcon: true,
            dateFormat: 'dd-mm-yy',
            maxDate: new Date(),
            minDate: new Date(1900, 0, 1),
          }"
        />
        <BaseFormField
          name="acquisitionYear"
          :label="t('newCarDialog.field.yearOfAcquisition')"
          :component="InputNumber"
          :inputProps="{
            useGrouping: false,
            min: 1900,
            max: 2026,
            showButtons: true,
            buttonLayout: 'horizontal',
            incrementButtonIcon: 'pi pi-plus',
            decrementButtonIcon: 'pi pi-minus',
          }"
        />

        <BaseFormField
          v-show="!props.defaultOwnerId"
          name="ownerId"
          :label="t('newCarDialog.field.owner')"
          :component="Select"
          :inputProps="{
            options: customers,
            optionLabel: 'username', // or fullname
            optionValue: 'id',
            filter: true,
            loading: isLoadingCustomers,
          }"
        />
      </div>

      <div
        class="mt-4 flex justify-end gap-2 border-t pt-4"
      >
        <Button
          :label="t('newCarDialog.cancelButtonLabel')"
          icon="pi pi-times"
          text
          severity="secondary"
          @click="emit('update:visible', false)"
        />

        <Button
          type="submit"
          :label="t('newCarDialog.submitButtonLabel')"
          icon="pi pi-check"
          :loading="isPending.value"
        />
      </div>
    </Form>
  </Dialog>
</template>

<script setup>
  import { computed, ref, watch, onMounted } from 'vue'
  import { useI18n } from 'vue-i18n'
  import { zodResolver } from '@primevue/forms/resolvers/zod'
  import { useQuery } from '@tanstack/vue-query'

  // UI Components
  import Dialog from 'primevue/dialog'
  import Button from 'primevue/button'
  import InputText from 'primevue/inputtext'
  import InputNumber from 'primevue/inputnumber'
  import Select from 'primevue/select'
  import DatePicker from 'primevue/datepicker'
  import { Form } from '@primevue/forms'
  import BaseFormField from '@/components/BaseFormField.vue'

  // Logic
  import { createCarSchema } from '@/composables/zodCarValidation.js'
  import { useCreateCar } from '@/features/cars/composables/useCreateCar.js'
  import { fetchUsers } from '@/features/users/services/userService.js'

  const props = defineProps({
    visible: Boolean,
    defaultOwnerId: {
      type: [String, Number],
      default: null,
    },
  })

  const emit = defineEmits(['update:visible', 'success'])
  const { t } = useI18n()

  const { isPending, handleFormSubmit } = useCreateCar(
    () => {
      emit('update:visible', false)
      emit('success')
    }
  )

  // Options
  const typeOptions = computed(() => [
    { label: t('carTypes.passenger'), value: 'PASSENGER' },
    { label: t('carTypes.truck'), value: 'TRUCK' },
    { label: t('carTypes.bus'), value: 'BUS' },
  ])

  const fuelOptions = computed(() => [
    { label: t('fuelTypes.electric'), value: 'ELECTRIC' },
    { label: t('fuelTypes.diesel'), value: 'DIESEL' },
    { label: t('fuelTypes.hybrid'), value: 'HYBRID' },
    { label: t('fuelTypes.lpg'), value: 'LPG' },
  ])

  const {
    data: customersData,
    isLoading: isLoadingCustomers,
  } = useQuery({
    queryKey: ['users', 'customers', 'simple'],
    queryFn: () =>
      fetchUsers({ role: 'CUSTOMER', size: 1000 }),
    enabled: computed(
      () => props.visible && !props.defaultOwnerId
    ),
    staleTime: 1000 * 60 * 5,
  })

  const customers = computed(
    () => customersData.value?.content || []
  )

  const formResolver = computed(() =>
    zodResolver(createCarSchema(t))
  )

  const initialValues = ref({
    serialNumber: '',
    brand: '',
    model: '',
    carType: null,
    fuelType: null,
    doorCount: 4,
    wheelCount: 4,
    productionDate: null,
    acquisitionYear: new Date().getFullYear(),
    ownerId: null,
  })

  watch(
    () => props.visible,
    (isOpen) => {
      if (isOpen) {
        initialValues.value = {
          serialNumber: '',
          brand: '',
          model: '',
          carType: null,
          fuelType: null,
          doorCount: 4,
          wheelCount: 4,
          productionDate: null,
          acquisitionYear: new Date().getFullYear(),
          ownerId: props.defaultOwnerId
            ? Number(props.defaultOwnerId)
            : null,
        }
      }
    }
  )
</script>
