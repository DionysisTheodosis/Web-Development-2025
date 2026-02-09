<script setup>
import Button from 'primevue/button'
import useHeroSection from './useHeroSection.js'
import { onMounted } from 'vue'
const { title, subtitle, btnText, highResImage, onHighResLoaded, isHighResLoaded, lowResImage ,tempRef} =
  useHeroSection()
onMounted(() => {
  tempRef.value = "des me";
  onHighResLoaded()
})
</script>

<template>
  <section id="hero" class="h-svh">
    <div
      :class="[
      'absolute inset-0 z-0',
      'bg-cover bg-center  bg-no-repeat',
      'transition-[filter] duration-[6000ms] ease-in-out z-99',
      isHighResLoaded ? 'filter-none' : 'blur-[20px]',
    ]"
      :style="{
      backgroundImage: `url(${isHighResLoaded ? highResImage : lowResImage})`
    }"
    ></div>
    <div
      v-if="highResImage"
      class="absolute  inset-0 z-10 bg-gradient-to-b from-black/20 via-black/60 to-black/30"
    ></div>

    <div class="relative flex flex-col text-center justify-center items-center p-10 h-[calc(100%-200px)] z-20">
      <h1 class="text-surface-50 font-bold mb-4 text-title-1">
        {{ title }}
      </h1>
      <p class="text-surface-200  text-title-5 font-light italic mb-20 ">
        {{ subtitle }}
      </p>
      <Button
        as="router-link"
        to="/login"
        :label="btnText"
        :style="{ color: 'var(--p-surface-0)' }"
        icon="pi pi-calendar"
        size="large"
        raised
      />
    </div>
  </section>
</template>
