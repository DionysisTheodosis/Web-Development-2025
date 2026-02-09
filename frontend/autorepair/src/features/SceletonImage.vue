<script setup>
import { ref } from 'vue'

const props = defineProps({
  lowResImage: String,
  highResImage: String,
})

const isHighResLoaded = ref(false)

function onHighResLoaded() {
  isHighResLoaded.value = true
}
</script>

<template>
  <div class="relative h-64 w-full overflow-hidden">
    <img
      :src="lowResImage"
      class="absolute inset-0 h-full w-full object-cover blur-[20px] transition-opacity duration-500"
      :class="{ 'opacity-0': isHighResLoaded }"
     alt="low resolution image"/>
    <img
      :src="highResImage"
      @load="onHighResLoaded"
      class="absolute inset-0 h-full w-full object-cover transition-opacity duration-500"
      :class="{ 'opacity-0': !isHighResLoaded }"
     alt="high resolution image"/>
  </div>
</template>

