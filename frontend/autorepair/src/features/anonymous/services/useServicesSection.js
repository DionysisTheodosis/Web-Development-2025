import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

export default function useServicesSection() {
  const { t, tm } = useI18n({ useScope: 'global' })

  const animations = [
    'fade-in-10 zoom-in-50',
    'fade-in-10 zoom-in-75',
    'fade-in-10 zoom-in-50'
  ]
  const imagesLow = [
    '/images/cards/oil-service-small.jpg',
    '/images/cards/brake-service-small.jpg',
    '/images/cards/engine-service-small.jpg'
  ]

  const imagesHigh = [
    '/images/cards/oil-service-large.jpg',
    '/images/cards/brake-service-large.jpg',
    '/images/cards/engine-service-large.jpg'

  ]
  const title = computed(() => t('sections.services.title'))
  const services = computed(() => {
    const items = tm('sections.services.items')
    return items.map((item, index) => ({
      ...item,
      animation: animations[index] || 'fade-in-10 zoom-in-50',
      image: imagesLow[index],
      imageHigh: imagesHigh[index],
    }))
  })

  return { title, services }
}
