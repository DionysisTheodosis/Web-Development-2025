import {
  useQuery,
  useMutation,
  useQueryClient,
} from '@tanstack/vue-query'
import { useRouter } from 'vue-router' // 👈 FIX 1: Use Hook
import { useToast } from 'primevue/usetoast'
import {
  fetchUserById,
  updateUser,
  deleteUser,
  setUserActiveStatus,
} from '@/features/users/services/userService' // 👈 FIX 2: Correct Path

const areChangesDetected = (
  currentUser,
  currentValues,
  initialValues
) => {
  if (!currentUser) return false
  const allFields = [
    'username',
    'email',
    'firstname',
    'lastname',
    'personalID',
    'role',
    'address',
    'taxNumber',
    'specialty',
  ]

  for (const key of allFields) {
    if (currentValues[key] === undefined) continue
    const newValue = String(currentValues[key] ?? '').trim()
    const oldValue = String(initialValues[key] ?? '').trim()
    if (newValue !== oldValue) return true
  }
  return false
}

export function useUserData(
  userId,
  getInitialValues,
  spinnerUtils,
  toggleEdit
) {
  const queryClient = useQueryClient()
  const router = useRouter()
  const toast = useToast()

  const { showSpinner, hideSpinner } = spinnerUtils || {
    showSpinner: () => {},
    hideSpinner: () => {},
  }
  const queryKey = ['user', userId]

  // --- Query ---
  const {
    data: user,
    isLoading,
    isError,
  } = useQuery({
    queryKey,
    queryFn: () => fetchUserById(userId),
    enabled: !!userId,
    staleTime: 5 * 60 * 1000,
  })

  const updateUserMutation = useMutation({
    mutationFn: (payload) =>
      updateUser(userId, payload, true),
    onMutate: () => showSpinner(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey })
      queryClient.invalidateQueries({ queryKey: ['users'] })
      if (toggleEdit) toggleEdit()
      toast.add({
        severity: 'success',
        summary: 'Updated',
        detail: 'User details updated',
        life: 3000,
      })
    },
    onError: (err) => {
      console.error('Update failed', err)
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Failed to update user',
      })
    },
    onSettled: () => hideSpinner(),
  })

  const deleteUserMutation = useMutation({
    mutationFn: (id) => deleteUser(id || userId, true),
    onMutate: () => showSpinner(),
    onSuccess: () => {
      queryClient.removeQueries({ queryKey })
      queryClient.invalidateQueries({ queryKey: ['users'] })

      router.push('/users')

      toast.add({
        severity: 'success',
        summary: 'Deleted',
        detail: 'User deleted successfully',
        life: 3000,
      })
    },
    onSettled: () => hideSpinner(),
  })

  const toggleActiveMutation = useMutation({
    mutationFn: ({ newStatus }) =>
      setUserActiveStatus(userId, newStatus),
    onMutate: () => showSpinner(),
    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({ queryKey })
      queryClient.invalidateQueries({ queryKey: ['users'] })

      const action = variables.newStatus
        ? 'Activated'
        : 'Locked'
      toast.add({
        severity: variables.newStatus ? 'success' : 'warn',
        summary: 'Success',
        detail: `User account has been ${action}`,
        life: 3000,
      })
    },
    onError: (err) => {
      console.error(err)
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Failed to change status',
      })
    },
    onSettled: () => hideSpinner(),
  })

  const onFormSubmit = ({ valid, values }) => {
    if (!valid || updateUserMutation.isPending.value) return

    const initial = getInitialValues()

    if (!areChangesDetected(user.value, values, initial)) {
      if (toggleEdit) toggleEdit()
      return
    }

    const payload = {}

    for (const key in values) {
      const newValue = String(values[key] ?? '').trim()
      const oldValue = String(initial[key] ?? '').trim()

      if (newValue !== oldValue) {
        const apiKey =
          key === 'personalID' ? 'identityNumber' : key

        if (key === 'role') {
          payload[apiKey] = newValue.toLowerCase()
        } else if (newValue !== '') {
          payload[apiKey] = newValue
        }
      }
    }

    if (!payload.role) {
      payload.role = String(user.value.role)
        .toLowerCase()
        .trim()
    }

    updateUserMutation.mutate(payload)
  }

  return {
    user,
    isLoading,
    isError,
    updateUserMutation,
    deleteUserMutation,
    toggleActiveMutation,
    onFormSubmit,
    areChangesDetected: (values) =>
      areChangesDetected(
        user.value,
        values,
        getInitialValues()
      ),
    queryKey,
  }
}
