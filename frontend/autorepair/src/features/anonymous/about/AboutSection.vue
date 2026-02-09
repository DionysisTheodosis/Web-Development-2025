<script setup>
  import { onMounted, ref } from 'vue'
  import useHeroSection from '@/features/anonymous/hero/useHeroSection.js'
  const { tempRef } = useHeroSection()
  const props = defineProps({
    title: String,
    description: String,
  })
</script>
<template>
  <section id="about" class="px-4 lg:px-24">
    <h2 class="mb-4 text-title-4 font-semibold">
      {{ props.title }}
    </h2>
    <p
      class="text-body-big text-surface-800 dark:text-surface-300"
    >
      {{ props.description }}
    </p>
  </section>
</template>
