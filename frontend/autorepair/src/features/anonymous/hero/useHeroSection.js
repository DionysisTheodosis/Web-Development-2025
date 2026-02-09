import { computed, ref } from 'vue'
import { useI18n } from 'vue-i18n'


export default function useHeroSection() {
  const { t } = useI18n({ useScope: 'global' })
  const title = computed(() => t('sections.hero.title'))
  const subtitle = computed(() => t('sections.hero.subtitle'))
  const btnText = computed(() => t('sections.hero.button'))

  const isHighResLoaded = ref(false)
  const lowResImage = '/images/hero-small.jpg'
  const highResImage = '/images/hero-large.jpg'
  const tempRef = ref(null)
  const onHighResLoaded = () => {
    const img = new Image();
    img.src = highResImage;
    img.onload = () => {
      isHighResLoaded.value = true;
    };
  };

  return {
    title,
    subtitle,
    btnText,
    onHighResLoaded,
    highResImage,
    isHighResLoaded,
    lowResImage,
    tempRef
  }
}
