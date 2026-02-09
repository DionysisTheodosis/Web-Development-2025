import { ref, computed } from 'vue'
import { useQuery } from '@tanstack/vue-query'
import { fetchDashboardStats } from '../services/dashboardService'
import { fetchUsers } from '@/features/users/services/userService'

export function useDashboard() {
  const { data: stats, isLoading: statsLoading } = useQuery(
    {
      queryKey: ['dashboard', 'stats'],
      queryFn: async () => {
        return await fetchDashboardStats()
      },
      staleTime: 0,
      refetchOnWindowFocus: false,
    }
  )

  const {
    data: recentUsersData,
    isLoading: recentLoading,
  } = useQuery({
    queryKey: ['users', 'recent'],
    queryFn: () =>
      fetchUsers({
        page: 0,
        size: 5,
        sortField: 'id',
        sortOrder: -1,
      }),
  })

  const chartData = computed(() => {
    if (!stats.value) return null

    const documentStyle = getComputedStyle(
      document.documentElement
    )

    const customerCount = stats.value.activeCustomers || 0
    const mechanicCount = stats.value.activeMechanics || 0
    const secretaryCount =
      stats.value.activeSecretaries || 0

    return {
      labels: ['Customers', 'Mechanics', 'Secretaries'],
      datasets: [
        {
          data: [
            customerCount,
            mechanicCount,
            secretaryCount,
          ],
          backgroundColor: [
            documentStyle.getPropertyValue(
              '--p-primary-500'
            ),
            documentStyle.getPropertyValue('--p-cyan-500'),
            documentStyle.getPropertyValue(
              '--p-orange-500'
            ),
          ],
          hoverBackgroundColor: [
            documentStyle.getPropertyValue(
              '--p-primary-400'
            ),
            documentStyle.getPropertyValue('--p-cyan-400'),
            documentStyle.getPropertyValue(
              '--p-orange-400'
            ),
          ],
        },
      ],
    }
  })

  const chartOptions = computed(() => {
    const documentStyle = getComputedStyle(
      document.documentElement
    )
    const textColor =
      documentStyle.getPropertyValue('--text-color')

    return {
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'bottom',
          labels: {
            usePointStyle: true,
            color: textColor,
            padding: 20,
          },
        },
      },
      cutout: '60%',
    }
  })

  return {
    stats,
    recentUsers: recentUsersData,
    statsLoading,
    recentLoading,

    chartData,
    chartOptions,
  }
}
