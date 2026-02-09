import { ref,useTemplateRef} from 'vue';

export default function useAuthPopover() {
  const popoverRef = useTemplateRef('popoverRef');
  const isVisible = ref(false);

  const togglePopover = () => {
    if (popoverRef.value) {
      isVisible.value = !isVisible.value;
      popoverRef.value.toggle();
    }
  };
  return {
    popoverRef,
    togglePopover,
    isVisible
  };
}
