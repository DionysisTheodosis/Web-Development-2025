import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

export default async function useAboutSection() {
  const { t } = useI18n({ useScope: 'global' })
  const title = computed(() => t('sections.about.title'))
  const subtitle = computed(() => t('sections.about.description'))
  return { title, subtitle }
}
