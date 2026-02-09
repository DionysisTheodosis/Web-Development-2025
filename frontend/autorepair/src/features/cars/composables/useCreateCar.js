import {
  useMutation,
  useQueryClient,
} from '@tanstack/vue-query'
import { useToast } from 'primevue/usetoast'
import { createCar } from '@/features/cars/services/carService.js'
import { useI18n } from 'vue-i18n'

export function useCreateCar(onSuccessCallback) {
  const queryClient = useQueryClient()
  const toast = useToast()
  const { t } = useI18n()

  const mutation = useMutation({
    mutationFn: (payload) => createCar(payload, true),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cars'] })

      toast.add({
        severity: 'success',
        summary: t('newCarDialog.toast.success.title'),
        detail: t('newCarDialog.toast.success.summary'),
        life: 3000,
      })

      if (onSuccessCallback) onSuccessCallback()
    },
  })

  const handleFormSubmit = ({ valid, values }) => {
    if (!valid) return


    const payload = {
      serialNumber: values.serialNumber?.trim(),
      brand: values.brand?.trim(),
      model: values.model?.trim(),
      carType: values.carType,
      fuelType: values.fuelType,
      doorCount: values.doorCount,
      wheelCount: values.wheelCount,
      productionDate: values.productionDate,
      acquisitionYear: values.acquisitionYear,
      ownerId: values.ownerId,
    }

    mutation.mutate(payload)
  }

  return {
    isPending: mutation.isPending,
    handleFormSubmit,
  }
}
