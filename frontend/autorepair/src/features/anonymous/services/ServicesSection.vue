<script setup>
import Card from 'primevue/card'
import useServicesSection from './useServicesSection.js'
import Skeleton from 'primevue/skeleton'
import SceletonImage from '@/features/SceletonImage.vue'
import { onMounted, ref } from 'vue'
const { title, services } = useServicesSection()
const isLoading = ref(true)

onMounted(() => {
  isLoading.value = false;
})

</script>

<template>
  <section id="services" class="px-4 lg:px-24 ">
    <h2 class="text-title-4 font-semibold mb-6 text-center">{{ title }}</h2>
    <!-- Loading Skeleton -->
    <div v-if="isLoading" class="flex flex-wrap flex-1 justify-evenly">
      <div v-for="index in 3" :key="index" class="m-4 w-64 h-80">
        <Skeleton height="50%" class="mb-4" /> <!-- Skeleton for Image -->
        <Skeleton width="75%" height="2rem" class="mb-2" /> <!-- Skeleton for Title -->
        <Skeleton height="4rem" /> <!-- Skeleton for Description -->
      </div>
    </div>

    <div v-else class="flex flex-wrap flex-1 justify-evenly">
      <Card
        v-for="(service, index) in services"
        :key="index"
        class="transform-gpu m-4"
        style="width: 25rem; overflow: hidden"
        v-animateonscroll.once="{
          enterClass: `animate-enter ${service.animation} animate-duration-1000`,
        }"
      >
        <template #header>
          <SceletonImage  :high-res-image="service.imageHigh" :low-res-image="service.image"/>
        </template>
        <template #title>
          {{ service.title }}
        </template>
        <template #content>
          <p class="text-surface-700 dark:text-surface-300">{{ service.description }}</p>
        </template>
      </Card>
    </div>
  </section>
</template>
