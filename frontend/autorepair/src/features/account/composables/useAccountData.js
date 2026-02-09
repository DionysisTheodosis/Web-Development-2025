// src/features/account/composables/useAccountData.js

import {
  useQuery,
  useMutation,
  useQueryClient,
} from '@tanstack/vue-query'
import {
  fetchCurrentUser,
  updateCurrentUser,
  deleteUserAccount,
} from '../services/accountService'

import router from '@/router/index.js'

const queryKey = ['currentUser']
const userQueryKey = ['user', 'me']

export function useAccountData(
  getInitialValues,
  spinnerUtils,
  toggleEdit
) {
  const queryClient = useQueryClient()

  const { showSpinner, hideSpinner } = spinnerUtils

  const {
    data: user,
    isLoading,
    isError,
  } = useQuery({
    queryKey,
    queryFn: fetchCurrentUser,
    staleTime: 5 * 60 * 1000,
  })

  const areChangesDetected = (currentValues) => {
    if (!user.value) return false

    const initial = getInitialValues()
    const userRole = user.value.role
    let detected = false

    const allFields = [
      'username',
      'email',
      'firstname',
      'lastname',
      'personalID',
      ...(userRole === 'CUSTOMER'
        ? ['address', 'taxNumber']
        : []),
      ...(userRole === 'MECHANIC' ? ['specialty'] : []),
    ]

    for (const key of allFields) {
      const newValue = (currentValues[key] ?? '').trim()
      const oldValue = String(initial[key] ?? '').trim()

      if (newValue !== oldValue) {
        detected = true
        break
      }
    }
    return detected
  }

  const updateUserMutation = useMutation({
    mutationFn: updateCurrentUser,

    onMutate: async (newPayload) => {
      await queryClient.cancelQueries({ queryKey })
      const previousUser =
        queryClient.getQueryData(queryKey)

      queryClient.setQueryData(queryKey, (oldData) => {
        if (!oldData) return oldData

        const mappedPayload = {
          ...newPayload,
          firstName:
            newPayload.firstname ?? oldData.firstName,
          lastName: newPayload.lastname ?? oldData.lastName,
          identityNumber:
            newPayload.identityNumber ??
            oldData.identityNumber,
        }
        return {
          ...oldData,
          ...mappedPayload,
          username: newPayload.username ?? oldData.username,
          email: newPayload.email ?? oldData.email,
          role: oldData.role,
        }
      })
      return { previousUser }
    },

    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey })
      await queryClient.invalidateQueries({
        queryKey: userQueryKey,
      })
      toggleEdit()
    },

    onError: (error, newPayload, context) => {
      console.error('Update failed:', error)
      if (context.previousUser) {
        queryClient.setQueryData(
          queryKey,
          context.previousUser
        )
      }
    },

    onSettled: () => {
      hideSpinner()
    },
  })

  const deleteAccount = useMutation({
    mutationFn: deleteUserAccount,
    onSuccess: async () => {
      queryClient.setQueryData(userQueryKey, null)

      await queryClient.removeQueries({
        queryKey: userQueryKey,
      })
      await queryClient.removeQueries({
        queryKey: queryKey,
      })

      await router.push('/')
    },
  })

  const onFormSubmit = ({ valid, values }) => {
    if (!valid || updateUserMutation.isPending.value) return

    if (!areChangesDetected(values)) {
      toggleEdit()
      return
    }

    const userRole = user.value.role
    const payload = {}
    const initial = getInitialValues()

    for (const key in values) {
      if (key === 'role') continue

      const newValue = (values[key] ?? '').trim()
      const oldValue = String(initial[key] ?? '').trim()

      if (newValue !== oldValue) {
        const apiKey =
          key === 'personalID' ? 'identityNumber' : key
        if (newValue !== '') {
          payload[apiKey] = newValue
        }
      }
    }

    payload.role = userRole.toLowerCase().trim()

    showSpinner()
    updateUserMutation.mutate(payload)
  }

  return {
    user,
    isLoading,
    isError,
    updateUserMutation,
    deleteAccount,
    onFormSubmit,
    queryKey,
    areChangesDetected,
  }
}
