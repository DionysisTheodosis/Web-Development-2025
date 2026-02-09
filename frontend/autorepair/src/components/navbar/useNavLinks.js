import { computed, useTemplateRef } from 'vue'
import { useI18n } from "vue-i18n";
import {useHandleSectionActive} from '@/composables/useHandleSectionActive.js'

export default function useNavLinks() {
  const { t } = useI18n();
  const navRef = useTemplateRef('navRef')
  const { elementAtPosition } = useHandleSectionActive(navRef)
  const navLinks = computed(() => {
    const activeElement = '#' + (elementAtPosition.value || '');
    return [
      { label: t("home"), to: "#hero", active: activeElement === '' || activeElement=== '#hero' },
      { label: t("about"), to: "#about", active: activeElement === '#about' },
      { label: t("services"), to: "#services", active: activeElement === '#services' },
      { label: t("whyUs"), to: "#whyUs", active: activeElement === '#whyUs' },
    ]
  })

  return { navLinks, navRef };
}