<template>
  <router-link :to="homePagePath" class="cursor-pointer">
    <SvgLogo :color="color" />
  </router-link>
</template>

<script setup>
  import { computed } from 'vue'
  import SvgLogo from '@/components/SvgLogo.vue'
import {useAuth} from '@/features/auth/useAuth.js'
const { isAuthenticated } = useAuth()
  defineProps({
    color: {
      type: String,
      default: '#000000',
    },
  })

  const homePagePath = computed(() => {
    if (isAuthenticated.value) {
      return '/dashboard';
    }
    return '/';
  });
</script>

<style scoped>
 .cursor-pointer {
    text-decoration: none;
    color: inherit;
  }
</style>