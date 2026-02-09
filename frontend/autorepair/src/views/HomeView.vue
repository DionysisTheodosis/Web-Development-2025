<script setup>
  import {
    inject,
    onMounted,
    defineAsyncComponent,
    ref,
  } from 'vue'
  import { useI18n } from 'vue-i18n'
  const text = ref('')
  import HeroSection from '@/features/anonymous/hero/HeroSection.vue'
  const AboutSection = defineAsyncComponent(
    () =>
      import('@/features/anonymous/about/AboutSection.vue')
  )
  const ServicesSection = defineAsyncComponent(
    () =>
      import(
        '@/features/anonymous/services/ServicesSection.vue'
      )
  )
  const WhyUsSection = defineAsyncComponent(
    () =>
      import('@/features/anonymous/whyus/WhyUsSection.vue')
  )

  const Button = defineAsyncComponent(
    () => import('primevue/button')
  )
  const { t } = useI18n()
  const mainLoaded = inject('mainLoaded')
  onMounted(() => {
    mainLoaded.value = true
  })
</script>

<template>
    <HeroSection />
    <AboutSection
      :title="t('sections.about.title')"
      :description="t('sections.about.description')"
      class="py-20 md:py-24 xl:py-32"
    />
    <ServicesSection class="py-20 md:py-24 xl:py-32" />
    <WhyUsSection
      :title="$t('sections.whyUs.title')"
      :benefits="[
        $t('sections.whyUs.benefit1'),
        $t('sections.whyUs.benefit2'),
        $t('sections.whyUs.benefit3'),
        $t('sections.whyUs.benefit4'),
      ]"
    />
</template>
