
import { createI18n } from 'vue-i18n'
import el from '@/langs/el.json'
import en from '@/langs/en.json'

const savedLocale = localStorage.getItem('locale') || 'en'

const i18nInstance = createI18n({
  legacy: false,
  locale: savedLocale,
  fallbackLocale: 'en',
  messages: { en, el }
})

const setLocale = (newLocale) => {
  i18nInstance.global.locale.value = newLocale
  localStorage.setItem('locale', newLocale)
}

export default i18nInstance
export { setLocale }
