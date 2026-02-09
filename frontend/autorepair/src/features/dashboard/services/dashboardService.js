import apiClient from '@/plugins/api-client.js'

export const fetchDashboardStats = async () => {
  try {
    const { data } = await apiClient.get('/dashboard/stats')
    return data
  } catch (error) {
    throw error
  }
}
