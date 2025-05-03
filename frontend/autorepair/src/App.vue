<template>
  <div class="flex h-screen overflow-hidden">
    <!-- Navbar: fixed at top -->
    <header class="fixed top-0 left-0 right-0 h-16 z-30 ">
      <AppNavBar @toggle-drawer="drawerVisible = !drawerVisible" />
    </header>

    <!-- Sidebar: visible on md+ screens -->
    <aside
      class="hidden md:block fixed top-16 bottom-0 left-0 w-64 z-20  border-r dark:border-surface-700  "
    >
      <AppSideBar />
    </aside>

    <!-- Drawer: small screens only -->
    <Drawer
      v-model:visible="drawerVisible"
      position="full"
      class="md:hidden"

    >
      <template #container="{ closeCallback }">
        <AppSideBar @close-drawer="closeCallback" />
      </template>
    </Drawer>

    <!-- Main content area -->
    <main
      class="flex-1 overflow-auto justify-items-center pt-16 px-4 md:px-8"
      :class="{'md:pl-64': true}"
    >
      <!-- Centered container -->

        <router-view />

    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import AppNavBar from '@/components/AppNavBar.vue'
import AppSideBar from '@/components/AppSideBar.vue'
import Drawer from 'primevue/drawer'

const drawerVisible = ref(false)
</script>

