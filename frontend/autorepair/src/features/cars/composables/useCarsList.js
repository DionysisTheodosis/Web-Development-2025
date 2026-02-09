import { ref, computed, watch, toValue } from 'vue'
import {
  useQuery,
  useMutation,
  useQueryClient,
} from '@tanstack/vue-query'
import { useToast } from 'primevue/usetoast'
import { useI18n } from 'vue-i18n'
import { FilterMatchMode } from '@primevue/core/api'
import {
  fetchCars,
  deleteCar,
  deleteMultipleCars,
} from '@/features/cars/services/carService.js'
import { watchDebounced } from '@vueuse/core'

export function useCarsList(
  requireDelete,
  presetOwnerId = null,
  withOwnerInfo = null
) {
  const queryClient = useQueryClient()
  const toast = useToast()
  const { t } = useI18n()


  const CAR_QUERY_KEY = ['cars', 'list']


  const selectedItems = ref([])
  const currentPage = ref(0)
  const rowsPerPage = ref(10)
  const sortField = ref(null)
  const sortOrder = ref(null)
  const firstRecordIndex = computed(
    () => currentPage.value * rowsPerPage.value
  )

  const filters = ref({
    global: {
      value: null,
      matchMode: FilterMatchMode.CONTAINS,
    },
    serialNumber: {
      value: null,
      matchMode: FilterMatchMode.CONTAINS,
    },
    brand: {
      value: null,
      matchMode: FilterMatchMode.CONTAINS,
    },
    model: {
      value: null,
      matchMode: FilterMatchMode.CONTAINS,
    },
    carType: {
      value: null,
      matchMode: FilterMatchMode.EQUALS,
    },
    fuelType: {
      value: null,
      matchMode: FilterMatchMode.EQUALS,
    },
  })

  watchDebounced(
    () => filters.value,
    () => {
      currentPage.value = 0
    },
    {
      deep: true,
      debounce: 500,
      maxWait: 1000,
    }
  )

  const getFilterParams = () => ({
    global: filters.value.global?.value,
    serialNumber: filters.value.serialNumber?.value,
    brand: filters.value.brand?.value,
    model: filters.value.model?.value,
    carType: filters.value.carType?.value,
    fuelType: filters.value.fuelType?.value,
  })

  // --- QUERY: Fetch Cars ---
  const carsQuery = useQuery({
    queryKey: computed(() => [
      ...CAR_QUERY_KEY,
      {
        page: currentPage.value,
        size: rowsPerPage.value,
        sortField: sortField.value,
        sortOrder: sortOrder.value,
        filters: getFilterParams(),
        ownerId: toValue(presetOwnerId),
        withOwnerInfo: toValue(withOwnerInfo),
      },
    ]),
    queryFn: ({ queryKey }) => {
      const params = queryKey[queryKey.length - 1]
      return fetchCars(params)
    },
    placeholderData: (previousData) => previousData,
  })

  // --- COMPUTED DATA ---
  const items = computed(
    () => carsQuery.data.value?.content || []
  )
  const totalRecords = computed(
    () => carsQuery.data.value?.totalElements || 0
  )
  const isLoading = computed(
    () =>
      carsQuery.isLoading.value ||
      carsQuery.isFetching.value
  )

  const onPage = (event) => {
    currentPage.value = event.page
    rowsPerPage.value = event.rows
  }

  const onSort = (event) => {
    sortField.value = event.sortField
    sortOrder.value = event.sortOrder
    currentPage.value = 0
  }

  const refreshTable = () => {
    queryClient.invalidateQueries({
      queryKey: CAR_QUERY_KEY,
    })
  }



  const deleteItemMutation = useMutation({
    mutationFn: (id) => deleteCar(id, true), // skipToast=true, we show our own
    onSuccess: () => {
      refreshTable()
      toast.add({
        severity: 'success',
        summary: t('allCars.carList.toasts.success'),
        detail: t('allCars.carList.toasts.deleted'),
        life: 3000,
      })
    },
  })

  const confirmDeleteItem = (car) => {
    requireDelete({
      header: t('allCars.carList.dialogs.deleteTitle'),
      message: t('allCars.carList.dialogs.deleteMessage', {
        serial: car.serialNumber,
      }),
      accept: () => deleteItemMutation.mutate(car.id),
    })
  }


  const deleteSelectedItemsMutation = useMutation({
    mutationFn: (ids) => deleteMultipleCars(ids, true), // skipToast=true, we show our own
    onSuccess: () => {
      refreshTable()
      toast.add({
        severity: 'success',
        summary: t('allCars.carList.toasts.success'),
        detail: t('allCars.carList.toasts.carsDeleted'),
        life: 3000,
      })
      selectedItems.value = []
    },
  })

  const confirmDeleteSelected = () => {
    requireDelete({
      header: t(
        'allCars.carList.dialogs.deleteSelectedTitle'
      ),
      message: t(
        'allCars.carList.dialogs.deleteSelectedMessage'
      ),
      accept: () => {
        if (selectedItems.value?.length > 0) {
          const idsToDelete = selectedItems.value.map(
            (i) => i.id
          )
          deleteSelectedItemsMutation.mutate(idsToDelete)
        }
      },
    })
  }

  return {
    items,
    totalRecords,
    isLoading,
    filters,
    selectedItems,
    rowsPerPage,
    firstRecordIndex,
    sortField,
    sortOrder,
    onPage,
    onSort,
    refreshTable,
    confirmDeleteItem,
    confirmDeleteSelected,
  }
}
