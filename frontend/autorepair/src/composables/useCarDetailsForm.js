import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

export function useCarDetailsForm(car) {
  const { t } = useI18n()


  const initialValues = computed(() => {
    if (!car.value) return {}
    return {
      serialNumber: car.value.serialNumber || '',
      brand: car.value.brand || '',
      model: car.value.model || '',
      carType: car.value.carType || null,
      fuelType: car.value.fuelType || null,
      doorCount:
        car.value.doorCount != null
          ? Number(car.value.doorCount)
          : 4,
      wheelCount:
        car.value.wheelCount != null
          ? Number(car.value.wheelCount)
          : 4,
      // Μετατροπή σε Date object για το DatePicker component
      productionDate: car.value.productionDate
        ? new Date(car.value.productionDate)
        : null,
      acquisitionYear:
        car.value.acquisitionYear != null
          ? Number(car.value.acquisitionYear)
          : null,
    }
  })

  const infoItems = computed(() => {
    if (!car.value) return []
    return [
      {
        name: 'serialNumber',
        label: t('cars.fields.serialNumber'),
        icon: 'pi pi-barcode',
        value: initialValues.value.serialNumber,
      },
      {
        name: 'brand',
        label: t('cars.fields.brand'),
        icon: 'pi pi-tag',
        value: initialValues.value.brand,
      },
      {
        name: 'model',
        label: t('cars.fields.model'),
        icon: 'pi pi-car',
        value: initialValues.value.model,
      },
      {
        name: 'carType',
        label: t('cars.fields.type'),
        icon: 'pi pi-cog',
        value: initialValues.value.carType,
      },
      {
        name: 'fuelType',
        label: t('cars.fields.fuel'),
        icon: 'pi pi-bolt',
        value: initialValues.value.fuelType,
      },
      {
        name: 'doorCount',
        label: t('cars.fields.doors'),
        icon: 'pi pi-minus-circle',
        value: initialValues.value.doorCount,
      },
      {
        name: 'wheelCount',
        label: t('cars.fields.wheels'),
        icon: 'pi pi-circle',
        value: initialValues.value.wheelCount,
      },
      {
        name: 'productionDate',
        label: t('cars.fields.productionDate'),
        icon: 'pi pi-calendar',
        value: initialValues.value.productionDate,
      },
      {
        name: 'acquisitionYear',
        label: t('cars.fields.acquisitionYear'),
        icon: 'pi pi-calendar-plus',
        value: initialValues.value.acquisitionYear,
      },
    ]
  })

  return { initialValues, infoItems }
}
