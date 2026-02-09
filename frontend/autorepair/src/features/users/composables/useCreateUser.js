// src/features/users/composables/useCreateUser.js
import { useMutation, useQueryClient } from '@tanstack/vue-query';
import { useToast } from 'primevue/usetoast';
import { createUser } from '@/features/users/services/userService';
import { useI18n } from 'vue-i18n'

export function useCreateUser(onSuccessCallback) {
  const queryClient = useQueryClient();
  const toast = useToast();
  const { t } = useI18n();
  const mutation = useMutation({
    mutationFn: (payload) => createUser(payload,true),
    onSuccess: () => {

      queryClient.invalidateQueries({ queryKey: ['users'] });

      toast.add({
        severity: 'success',
        summary: t('newUserDialog.toast.success.title'),
        detail: t('newUserDialog.toast.success.summary'),
        life: 3000
      });

      if (onSuccessCallback) onSuccessCallback();
    },
  });

  const handleFormSubmit = ({ valid, values }) => {
    if (!valid) return;

    let payload = {
      username: values.username?.trim(),
      email: values.email?.trim(),
      password: values.password,
      firstName: values.firstname?.trim(),
      lastName: values.lastname?.trim(),
      identityNumber: values.personalID?.trim(),
      role: values.role?.toLowerCase()
    };

    if (values.role === 'customer') {
      payload.address = values.address?.trim();
      payload.taxNumber = values.taxNumber?.trim();
    } else if (values.role === 'mechanic') {
      payload.specialty = values.specialty?.trim();
    }

    mutation.mutate(payload);
  };

  return {
    isPending: mutation.isPending,
    handleFormSubmit
  };
}