<script setup>
import { defineProps } from 'vue'
const props = defineProps({
  title: {
    type: String,
    default: 'Why Choose Us'
  },
  benefits: {
    type: Array,
    default: () => [
      'Certified Technicians',
      'Customer Satisfaction Guarantee',
      'Affordable Prices',
      'Fast Turnaround'
    ]
  }
})
</script>

<template>
  <section
    id="whyUs"
    class="mb-16 flex flex-col items-center justify-center bg-surface-800 py-12 md:py-24 xl:py-32 dark:bg-surface-900"
  >
    <h2 class="mb-6 text-center text-3xl font-semibold text-surface-100">
      {{props.title }}
    </h2>
    <ul class="grid grid-cols-1 gap-4 md:grid-cols-2">
      <li
        v-for="benefit in props.benefits"
        :key="benefit"
        v-animateonscroll.once="{
          enterClass: 'animate-fadeinleft animate-duration-1000'
        }"
        class="flex transform-gpu items-center justify-start gap-2"
      >
        <i class="pi pi-check-circle text-primary"></i>
        <span class="text-center text-surface-200">{{ benefit }}</span>
      </li>
    </ul>
  </section>
</template>