import { ref, computed, watch } from 'vue'
import {
  useQuery,
  useMutation,
  useQueryClient,
} from '@tanstack/vue-query'
import { useToast } from 'primevue/usetoast'
import { useI18n } from 'vue-i18n'
import { FilterMatchMode } from '@primevue/core/api'
import {
  fetchUsers,
  deleteUser,
  deleteMultipleUsers,
  setUserActiveStatus,
} from '@/features/users/services/userService'
import { watchDebounced } from '@vueuse/core'

export function useUsersList(
  requireDelete,
  presetRole = null
) {
  const queryClient = useQueryClient()
  const toast = useToast()
  const { t } = useI18n()

  const USER_QUERY_KEY = ['users', 'list']

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
    username: {
      value: null,
      matchMode: FilterMatchMode.CONTAINS,
    },
    lastName: {
      value: null,
      matchMode: FilterMatchMode.CONTAINS,
    },
    taxNumber: {
      value: null,
      matchMode: FilterMatchMode.CONTAINS,
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
    username: filters.value.username?.value,
    lastName: filters.value.lastName?.value,
    taxNumber: filters.value.taxNumber?.value,
  })

  const usersQuery = useQuery({
    queryKey: computed(() => [
      ...USER_QUERY_KEY,
      {
        page: currentPage.value,
        size: rowsPerPage.value,
        sortField: sortField.value,
        sortOrder: sortOrder.value,
        filters: getFilterParams(),
        role: presetRole,
      },
    ]),
    queryFn: ({ queryKey }) => {
      const params = queryKey[queryKey.length - 1]
      return fetchUsers(params)
    },
    placeholderData: (previousData) => previousData,
  })


  const items = computed(
    () => usersQuery.data.value?.content || []
  )
  const totalRecords = computed(
    () => usersQuery.data.value?.totalElements || 0
  )
  const isLoading = computed(
    () =>
      usersQuery.isLoading.value ||
      usersQuery.isFetching.value
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
      queryKey: USER_QUERY_KEY,
    })
  }

  const deleteItemMutation = useMutation({
    mutationFn: (id) => deleteUser(id, true),
    onSuccess: () => {
      refreshTable()
      toast.add({
        severity: 'success',
        summary: t('allUsers.userList.toasts.success'),
        detail: t('allUsers.userList.toasts.deleted'),
        life: 3000,
      })
    },
  })

  const confirmDeleteItem = (user) => {
    requireDelete({
      header: t('allUsers.userList.dialogs.deleteTitle'),
      message: t(
        'allUsers.userList.dialogs.deleteMessage',
        { username: user.username }
      ),
      accept: () => deleteItemMutation.mutate(user.id),
    })
  }


  const deleteSelectedItemsMutation = useMutation({
    mutationFn: (ids) => deleteMultipleUsers(ids, true),
    onSuccess: () => {
      refreshTable()
      toast.add({
        severity: 'success',
        summary: t('allUsers.userList.toasts.success'),
        detail: t('allUsers.userList.toasts.usersDeleted'),
        life: 3000,
      })
      selectedItems.value = []
    },
  })

  const confirmDeleteSelected = () => {
    requireDelete({
      header: t(
        'allUsers.userList.dialogs.deleteSelectedTitle'
      ),
      message: t(
        'allUsers.userList.dialogs.deleteSelectedMessage'
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


  const togglingIds = ref(new Set())
  const isToggling = (id) => togglingIds.value.has(id)

  const toggleStatusMutation = useMutation({
    mutationFn: ({ id, newStatus }) =>
      setUserActiveStatus(id, newStatus),
    onMutate: (variables) =>
      togglingIds.value.add(variables.id),
    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({ queryKey: ['users'] })

      const detailMsg = variables.newStatus
        ? t('allUsers.userList.toasts.activated')
        : t('allUsers.userList.toasts.locked')

      toast.add({
        severity: 'success',
        summary: t('allUsers.userList.toasts.success'),
        detail: detailMsg,
        life: 3000,
      })
    },
    onSettled: (data, error, variables) =>
      togglingIds.value.delete(variables.id),
  })

  const onToggleStatus = (user) => {
    toggleStatusMutation.mutate({
      id: user.id,
      newStatus: !user.active,
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
    togglingIds,
    onPage,
    onSort,
    refreshTable,
    confirmDeleteItem,
    confirmDeleteSelected,
    onToggleStatus,
    isToggling,
  }
}
