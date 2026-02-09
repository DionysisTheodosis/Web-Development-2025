// /features/auth/useAuth.js

import {
  useQuery,
  useQueryClient,
} from '@tanstack/vue-query'
import { computed } from 'vue'
import apiClient from '@/plugins/api-client.js'

export function useAuth() {
  const queryClient = useQueryClient()

  const query = useQuery({
    queryKey: ['user', 'me'],
    queryFn: async () => {
      const { data } = await apiClient.get('/auth/me', {
        withCredentials: true,
      })
      return data
    },
    retry: false,
    staleTime: 5 * 60 * 1000,
    refetchOnWindowFocus: true,
  })

  const isAuthenticated = computed(() => !!query.data.value)


  const login = async (credentials) => {
    await apiClient.post('/auth/login', credentials, {
      withCredentials: true,
    })

    await queryClient.invalidateQueries({
      queryKey: ['user', 'me'],
    })
  }

  const logout = async () => {
    try {
      await apiClient.post('/auth/logout')
    } catch (e) {
    }

    queryClient.setQueryData(['user', 'me'], null)

    queryClient.removeQueries({ queryKey: ['user', 'me'] })
    queryClient.clear()
  }

  return {
    ...query,
    isAuthenticated,
    login,
    logout,
  }
}
