<script setup>
  import PanelMenu from 'primevue/panelmenu'
  import Button from 'primevue/button'
  import 'primeicons/primeicons.css'
  import useNavLinks from '@/components/navbar/useNavLinks.js'
  import useSideBarLinks from '@/components/sidebar/useAppSideBar.js'
  import { inject, ref } from 'vue'
  import LogoLinkButton from '@/components/LogoLinkButton.vue'
  import { useAuth } from '@/features/auth/useAuth.js'
  const { data: user , isAuthenticated} = useAuth()

  const { navLinks } = useNavLinks()
  const { sideBarItems } = useSideBarLinks()
  const emit = defineEmits(['close-drawer'])
  const drawerVisible = ref(false)
  drawerVisible.value = inject("drawerVisible")

  const handleNavigate = async (navigateFn) => {
    navigateFn()
    emit('close-drawer')
  }
</script>


<template>
  <div v-focustrap class="flex flex-col ">
    <div class="flex px-3 pt-4 justify-end" :class="{'hidden':!drawerVisible.value}">
      <Button icon="pi pi-times" @click="$emit('close-drawer')" rounded outlined />
    </div>
    <div class="max-lg:flex px-3 pt-0">
      <div class="flex-1 flex justify-center  mb-4">
        <LogoLinkButton class="h-20 w-auto" color="var(--p-primary-color)" />
      </div>
    </div>
    <div class="flex-1 overflow-y-auto p-3">
      <PanelMenu
        :model="isAuthenticated ? sideBarItems : navLinks"
        class="border-none"
        :style="{
          '--p-panelmenu-panel-border-color': 'transparent',
          '--p-panelmenu-panel-background': 'transparent',
        }"
      >

        <template #item="{ item }">
          <router-link v-if="item.to" v-slot="{ navigate, isActive }" :to="item.to" custom>
            <a
              @click="() => handleNavigate(navigate)"
              v-ripple
              :class="[
                'flex items-center px-4 py-2 rounded-md transition-colors cursor-pointer text-surface-0 hover:text-primary',
                isAuthenticated
                  ? isActive
                    ? ' bg-primary  hover:text-white '
                    : ' hover:bg-primary-color'
                  : item.active
                    ? 'text-primary justify-center'
                    : 'text-surface-50 justify-center',
              ]"
            >
              <span :class="item.icon" />
              <span class="shrink-0 ml-2">{{ item.label }}</span>
            </a>
          </router-link>

          <a v-else v-ripple class="flex items-center px-4 py-2 cursor-pointer text-surface-0 hover:text-primary">
            <span :class="item.icon" />
            <span class="ml-2">{{ item.label }}</span>
            <span v-if="item.items" class="pi pi-angle-down" />
          </a>
        </template>
      </PanelMenu>
    </div>
  </div>
</template>
