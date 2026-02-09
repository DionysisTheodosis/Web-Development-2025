import { computed, ref } from 'vue'
import {
  useInfiniteQuery,
  useMutation,
  useQueryClient,
} from '@tanstack/vue-query'
import apiClient from '@/plugins/api-client.js'

export function usePendingUsersInfinite() {
  const queryClient = useQueryClient()
  const QUERY_KEY = ['users', 'pending-infinite']

  // State
  const size = ref(5)
  const sortOrder = ref('desc')
  const sortBy = ref('id')

  // --- INFINITE QUERY ---
  const usersQuery = useInfiniteQuery({
    queryKey: computed(() => [
      ...QUERY_KEY,
      size.value,
      sortOrder.value,
    ]),
    queryFn: async ({ pageParam = 0 }) => {
      const response = await apiClient.get(
        '/users/request-activation',
        {
          params: {
            page: pageParam,
            size: size.value,
            sortBy: sortBy.value,
            sortOrder: sortOrder.value,
          },
        }
      )

      // HATEOAS Parsing
      const data = response.data
      const content = data._embedded?.users || []
      const pageMeta = data.page || {}

      return {
        content,
        ...pageMeta,
        totalPages: pageMeta.totalPages ?? data.totalPages,
        totalElements:
          pageMeta.totalElements ?? data.totalElements,
      }
    },
    getNextPageParam: (lastPage, allPages) => {
      const currentPagesFetched = allPages.length
      const totalPages = lastPage.totalPages
      return currentPagesFetched < totalPages
        ? currentPagesFetched
        : undefined
    },
    staleTime: 1000 * 60, // 1 minute
  })

  const allUsers = computed(
    () =>
      usersQuery.data.value?.pages?.flatMap(
        (page) => page.content
      ) || []
  )

  const totalElements = computed(
    () =>
      usersQuery.data.value?.pages?.[0]?.totalElements ?? 0
  )
  const pendingCount = totalElements

  const handleOptimisticRemoval = (userId) => {
    queryClient.setQueryData(
      [...QUERY_KEY, size.value, sortOrder.value],
      (oldData) => {
        if (!oldData) return oldData
        return {
          ...oldData,
          pages: oldData.pages.map((page) => ({
            ...page,
            content: page.content.filter(
              (u) => u.id !== userId
            ),

            totalElements: Math.max(
              0,
              page.totalElements - 1
            ),
          })),
        }
      }
    )
    queryClient.invalidateQueries({
      queryKey: ['users', 'list'],
    })
  }

  // --- MUTATIONS ---
  const activateUserMutation = useMutation({
    mutationFn: (userId) =>
      apiClient.patch(
        `/users/${userId}/active-account`,
        null,
        { params: { active: true } }
      ),
    onSuccess: (_, userId) =>
      handleOptimisticRemoval(userId),
  })

  const denyUserMutation = useMutation({
    mutationFn: (userId) =>
      apiClient.delete(`/users/${userId}`),
    onSuccess: (_, userId) =>
      handleOptimisticRemoval(userId),
  })

  const toggleSort = () => {
    sortOrder.value =
      sortOrder.value === 'asc' ? 'desc' : 'asc'
  }

  return {
    usersQuery,
    allUsers,
    totalElements,
    pendingCount,
    sortOrder,
    toggleSort,
    activateUserMutation,
    denyUserMutation,
  }
}
