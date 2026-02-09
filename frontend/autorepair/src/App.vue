<script setup>
  import {
    computed,
    defineAsyncComponent,
    onMounted,
    ref,
    watch,
  } from 'vue'
  import { useRoute } from 'vue-router'
  import Toast from 'primevue/toast'
  import { useToast } from 'primevue/usetoast'
  const toast = useToast()
  import { useToastBus } from '@/composables/useToastBus.js'
  import AppConfirmDialog from '@/components/AppConfirmDialog.vue'
  const AuthLayout = defineAsyncComponent(
    () => import('@/layouts/AuthLayout.vue')
  )
  const MainLayout = defineAsyncComponent(
    () => import('@/layouts/MainLayout.vue')
  )
  const ScrollTop = defineAsyncComponent(
    () => import('primevue/scrolltop')
  )

  const route = useRoute()
  const loading = ref(true)
  const toastPosition = ref('top-center')
  onMounted(() => {
    const updatePosition = () => {
      toastPosition.value =
        window.innerWidth >= 640
          ? 'top-right'
          : 'top-center'
    }
    updatePosition()
    window.addEventListener('resize', updatePosition)
    loading.value = false
  })
  const layoutComponent = computed(() => {
    return route.meta.layout === 'auth'
      ? AuthLayout
      : MainLayout
  })
  watch(
    () => useToastBus.messages,
    (messages) => {
      messages.forEach((msg) => toast.add(msg))
      useToastBus.messages.length = 0
    },
    { deep: true }
  )
</script>

<template>
  <Toast :position="toastPosition" />
  <component :is="layoutComponent" :key="layoutComponent">
    <router-view />
  </component>

  <ScrollTop
    target="window"
    :threshold="50"
    icon="pi pi-arrow-up"
    :buttonProps="{
      severity: 'contrast',
      raised: true,
      rounded: true,
    }"
  />
  <AppConfirmDialog />
</template>
