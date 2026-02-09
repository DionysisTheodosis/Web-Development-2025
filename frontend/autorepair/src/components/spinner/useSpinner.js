import { ref } from 'vue';

export function useSpinner() {
  const loading = ref(false);

  const showSpinner = () => {
    loading.value = true;
  };

  const hideSpinner = () => {
    loading.value = false;
  };

  return { loading, showSpinner, hideSpinner };
}