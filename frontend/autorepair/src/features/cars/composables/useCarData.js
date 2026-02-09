import {
  useQuery,
  useMutation,
  useQueryClient,
} from '@tanstack/vue-query'
import { useRouter } from 'vue-router'
import { useToast } from 'primevue/usetoast'
import { useI18n } from 'vue-i18n'
import {
  fetchCarById,
  updateCar,
  deleteCar,
  fetchCars,
} from '@/features/cars/services/carService'
import { fetchUserById } from '@/features/users/services/userService'

const areCarChangesDetected = (
  currentValues,
  initialValues
) => {
  // Safety check: if either is undefined/null, return false
  if (!currentValues || !initialValues) return false

  const fields = [
    'serialNumber',
    'brand',
    'model',
    'carType',
    'fuelType',
    'doorCount',
    'wheelCount',
    'productionDate',
    'acquisitionYear',
  ]

  for (const key of fields) {
    let newValue = currentValues[key]
    let oldValue = initialValues[key]

    // Handle string trimming
    if (typeof newValue === 'string')
      newValue = newValue.trim()
    if (typeof oldValue === 'string')
      oldValue = oldValue.trim()

    // Standard comparison (handles numbers, strings, and nulls)
    if (String(newValue ?? '') !== String(oldValue ?? ''))
      return true
  }
  return false
}

export function useCarData(
  carId,
  getInitialValues,
  spinnerUtils,
  toggleEdit
) {
  const queryClient = useQueryClient()
  const router = useRouter()
  const toast = useToast()
  const { t } = useI18n()

  const { showSpinner, hideSpinner } = spinnerUtils || {
    showSpinner: () => {},
    hideSpinner: () => {},
  }
  const queryKey = ['car', carId]

  const {
    data: car,
    isLoading,
    isError,
  } = useQuery({
    queryKey,
    queryFn: async () => {
      // 1. Fetch single car
      let data = await fetchCarById(carId)

      if (!data.ownerInfo && data.serialNumber) {
        try {
          const searchParams = {
            filters: { serialNumber: data.serialNumber },
            withOwnerInfo: true,
            page: 0,
            size: 10,
          }
          const searchResult = await fetchCars(searchParams)

          const foundCar =
            searchResult.content?.find(
              (c) => c.id === Number(carId)
            ) ||
            searchResult.content?.find(
              (c) => c.serialNumber === data.serialNumber
            )

          if (foundCar && foundCar.ownerInfo) {
            data.ownerInfo = foundCar.ownerInfo

            if (data.ownerInfo.id) {
              try {
                const fullUser = await fetchUserById(
                  data.ownerInfo.id
                )

                if (fullUser) {
                  data.ownerInfo = {
                    ...data.ownerInfo,
                    taxNumber: fullUser.taxNumber,
                    email: fullUser.email,
                    phone: fullUser.phoneNumber,
                    address: fullUser.address,
                  }
                }
              } catch (userErr) {
                console.error(
                  'Failed to fetch full user details:',
                  userErr
                )
              }
            }
          }
        } catch (err) {
          console.error(
            'Failed to fetch owner info via fallback:',
            err
          )
        }
      }

      return data
    },
    enabled: !!carId,
    staleTime: 5 * 60 * 1000,
  })

  const updateCarMutation = useMutation({
    mutationFn: ({ id, payload }) =>
      updateCar(id, payload, true),
    onMutate: () => showSpinner(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey })
      queryClient.invalidateQueries({ queryKey: ['cars'] })
      if (toggleEdit) toggleEdit()
      toast.add({
        severity: 'success',
        summary: t('common.updated'),
        detail: t('cars.toasts.updateSuccess'),
        life: 3000,
      })
    },
    onError: (err) => {
      console.error('Update failed', err)
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: t('cars.toasts.updateError'),
      })
    },
    onSettled: () => hideSpinner(),
  })

  const deleteCarMutation = useMutation({
    mutationFn: (id) => deleteCar(id || carId, true),
    onMutate: () => showSpinner(),
    onSuccess: () => {
      queryClient.removeQueries({ queryKey })
      queryClient.invalidateQueries({ queryKey: ['cars'] })
      router.push('/cars')
      toast.add({
        severity: 'success',
        summary: 'Deleted',
        detail: t('cars.toasts.deleteSuccess'),
        life: 3000,
      })
    },
    onSettled: () => hideSpinner(),
  })

  const onFormSubmit = ({ valid, values }) => {
    if (!valid || updateCarMutation.isPending.value) return

    const initial = getInitialValues()

    if (!initial) {
      console.error('Initial values are undefined')
      return
    }

    if (!areCarChangesDetected(values, initial)) {
      if (toggleEdit) toggleEdit()
      return
    }

    const payload = {}
    const fields = [
      'serialNumber',
      'brand',
      'model',
      'carType',
      'fuelType',
      'doorCount',
      'wheelCount',
      'productionDate',
      'acquisitionYear',
    ]

    for (const key of fields) {
      if (values[key] === undefined) continue

      let newVal = values[key]
      let oldVal = initial[key]

      if (typeof newVal === 'string') newVal = newVal.trim()
      if (typeof oldVal === 'string') oldVal = oldVal.trim()

      if (String(newVal ?? '') !== String(oldVal ?? '')) {
        if (
          key === 'productionDate' &&
          newVal instanceof Date
        ) {
          const year = newVal.getFullYear()
          const month = String(
            newVal.getMonth() + 1
          ).padStart(2, '0')
          const day = String(newVal.getDate()).padStart(
            2,
            '0'
          )
          payload[key] = `${year}-${month}-${day}`
        } else if (
          key === 'acquisitionYear' &&
          newVal instanceof Date
        ) {
          payload[key] = newVal.getFullYear()
        } else if (
          ['doorCount', 'wheelCount'].includes(key)
        ) {
          payload[key] = parseInt(newVal, 10)
        } else {
          payload[key] = newVal
        }
      }
    }

    if (Object.keys(payload).length === 0) {
      if (toggleEdit) toggleEdit()
      return
    }

    updateCarMutation.mutate({ id: carId, payload })
  }

  return {
    car,
    isLoading,
    isError,
    updateCarMutation,
    deleteCarMutation,
    onFormSubmit,
    areCarChangesDetected: (values) =>
      areCarChangesDetected(values, getInitialValues()),
    queryKey,
  }
}
