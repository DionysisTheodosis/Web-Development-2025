// src/features/account/services/accountService.js
import apiClient from '@/plugins/api-client.js'

export const fetchCurrentUser = async () => {
  const { data } = await apiClient.get('/users/me')
  return data
}

export const updateCurrentUser = async (payload) => {
  const { data } = await apiClient.patch(
    '/users/me',
    payload
  )
  return data
}

export const deleteUserAccount = async () => {
  return apiClient.delete(`/users/me`)
}
