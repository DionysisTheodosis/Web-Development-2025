<template>

  <div class="flex h-screen overflow-hidden">
    <!-- Navbar: fixed at top, shifts right on md+ -->
    <header class="fixed top-0 left-0 right-0 h-16 z-30 md:left-64">
      <AppNavBar @toggle-drawer="drawerVisible = !drawerVisible" />
    </header>

    <!-- Sidebar: starts at very top, shows on md+ -->
    <aside
      class="hidden md:block fixed top-0 bottom-0 left-0 w-64 z-20 backdrop-blur-lg bg-black/20 shadow-xl"
      style="background-color: rgba(255, 255, 255, 0.001); border-color: transparent;"
    >

      <AppSideBar />
    </aside>

    <!-- Drawer: small screens only -->
    <Drawer
      v-model:visible="drawerVisible"
      position="full"
      class="md:hidden backdrop-blur-lg bg-black/20 text-white shadow-xl"
      style="--p-drawer-background: rgba(0, 0, 0, 0.001); --p-drawer-border-color: transparent;"
    >
      <template #container="{ closeCallback }">
        <AppSideBar @close-drawer="closeCallback" />
      </template>
    </Drawer>
    <ScrollPanel class="w-full justify-center">
    <!-- Main content area: already padded on md+ -->
    <main
      class="flex-1 overflow-auto pt-16 px-4 md:px-8 md:pl-64"
    >
      <router-view />

    </main>
      <ScrollTop target="parent" :threshold="100" icon="pi pi-arrow-up" :buttonProps="{ severity: 'primary-contrast', raised: true, rounded: true }" />
    </ScrollPanel>
  </div>

</template>


<script setup>
import { ref } from 'vue'
import AppNavBar from '@/components/AppNavBar.vue'
import AppSideBar from '@/components/AppSideBar.vue'
import Drawer from 'primevue/drawer'
import '@/assets/main.css'
import ScrollTop from 'primevue/scrolltop';
import ScrollPanel from 'primevue/scrollpanel';

const drawerVisible = ref(false)

</script>

