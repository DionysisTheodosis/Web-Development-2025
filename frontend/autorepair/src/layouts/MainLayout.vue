<script setup>
  import { storeToRefs } from 'pinia'
  import Breadcrumb from 'primevue/breadcrumb'
  import { useRoute } from 'vue-router'
  import { useI18n } from 'vue-i18n'
  const route = useRoute()
  const { t } = useI18n()
  const AppSideBar = defineAsyncComponent(
    () => import('@/components/sidebar/AppSideBar.vue')
  )
  const Drawer = defineAsyncComponent(
    () => import('primevue/drawer')
  )
  import AppNavBar from '@/components/navbar/AppNavBar.vue'
  const FooterSection = defineAsyncComponent(
    () => import('@/components/footer/FooterSection.vue')
  )
  import {
    computed,
    defineAsyncComponent,
    onMounted,
    provide,
    ref,
  } from 'vue'

  import { useAuth } from '@/features/auth/useAuth.js'
  const { isAuthenticated } = useAuth()
  import AppBreadcrumb from '@/components/AppBreadcrumb.vue'
  const drawerVisible = ref(false)
  const mainLoaded = ref(false)
  provide('mainLoaded', mainLoaded)
  provide('drawerVisible', drawerVisible)
  provide('toggleDrawer', () => {
    drawerVisible.value = !drawerVisible.value
  })
  onMounted(() => {
    mainLoaded.value = true
  })

</script>

<template>
  <Drawer
    id="drawer"
    v-model:visible="drawerVisible"
    position="full"
    class="backdrop-blur-[20px]"
  >
    <template #container="{ closeCallback }">
      <AppSideBar
        v-focustrap
        @close-drawer="closeCallback"
      />
    </template>
  </Drawer>
  <div
    :class="{
      flex:  isAuthenticated,
      'flex flex-col': !isAuthenticated,
    }"
  >
    <AppSideBar
      v-if=" isAuthenticated"
      class="sticky top-0 hidden h-screen w-64 flex-col backdrop-blur-[20px] lg:flex"
      :class="[
         isAuthenticated
          ? 'bg-black/80 dark:bg-black/60'
          : 'bg-black/60',
      ]"
    />
    <main class="flex min-h-screen flex-1 flex-col min-w-0">
      <AppNavBar />
      <div class="p-2">
     <AppBreadcrumb/>
      </div>
      <div class="flex-grow " >
      <router-view
        v-slot="{ Component, route }"
      >
        <component :is="Component" :key="route.path" />
      </router-view>
      </div>
      <FooterSection v-if="mainLoaded" />
    </main>
  </div>
</template>
