import { computed, toRef } from 'vue'
import { useI18n } from 'vue-i18n'

export default function useFooterSection() {
  const { t } = useI18n()
  const copyright = computed(() => t('footer.copyright'))
  const address = computed(() => t('footer.address'))
  const phone = computed(() => t('footer.phone'))
  const email = computed(() => t('footer.email'))
  const developer = computed(() => t('footer.developer'))
  const developerName = 'Dionysis Theodosis'
  return {
    copyright,
    address,
    phone,
    email,
    developer,
    developerName,
  }
}
