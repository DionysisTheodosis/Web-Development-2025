<script setup>
  import { inject, computed } from 'vue'
  import InputText from 'primevue/inputtext'
  import Menubar from 'primevue/menubar'
  import Button from 'primevue/button'
  import Notification from '@/components/Notification.vue'
  import LocaleMenu from '@/components/locale/LocaleMenu.vue'
  import '@/components/auth/index.js'
  import useNavLinks from '@/components/navbar/useNavLinks.js'
  import { AuthPopoverButton } from '@/components/auth/index.js'
  import { useI18n } from 'vue-i18n'
  import ButtonTheme from '@/components/ButtonTheme.vue'
  import LogoLinkButton from '@/components/LogoLinkButton.vue'
  import { useAuth } from '@/features/auth/useAuth.js'
  const { t } = useI18n()
  defineEmits(['toggle-drawer'])
  const toggleDrawer = inject('toggleDrawer')
  const { navLinks } = useNavLinks()
  const { data: user ,isAuthenticated} = useAuth()

  const role = computed(() => user.value?.role || null)
</script>

<template>
  <nav
    class="sticky top-0 left-0 right-0 z-50 backdrop-blur-[20px]"
    :class="[isAuthenticated ? 'bg-black/80 dark:bg-black/60' : 'bg-black/60']"
  >
    <Menubar
      :model="navLinks"
      :pt="{
        root: { class: 'text-surface-600 hover:text-primary' },
        button: { class: 'hidden' },
        itemContent: { class: 'hidden lg:flex group' },
        itemLabel: { class: 'text-surface-900 group-hover:text-primary transition-colors' }
      }"
    >
      <template #start>
        <div class="flex justify-center items-center h-12" :class="{ 'lg:hidden': isAuthenticated }">
          <LogoLinkButton class="h-20 w-auto" color="var(--p-primary-color)" />
        </div>
        <span class="lg:hidden">
          <Button
            hover="text-primary"
            icon="pi pi-bars"
            severity="secondary"
            variant="text"
            @click="toggleDrawer()"
          />
        </span>
      </template>

      <template #item="{ item, props }">
        <router-link
          v-if="item.to && !isAuthenticated"
          :to="item.to"
          class="flex items-center"
          v-bind="props.action"
        >
          <span
            :class="[
              'transition-colors',
              item.active
                ? 'text-primary font-semibold'
                : 'text-surface-50 group-hover:text-primary',
            ]"
          >
            {{ item.label }}
          </span>
        </router-link>
      </template>

      <template #end>
        <div id="navItems" class="flex items-baseline justify-between gap-2">
          <InputText v-if="isAuthenticated" class="w-32 sm:w-auto" :placeholder="t('search')" type="text" />
          <LocaleMenu />
          <ButtonTheme />
          <Notification v-if="role === 'SECRETARY'" class="mr-4" />
          <AuthPopoverButton />
        </div>
      </template>
    </Menubar>
  </nav>
</template>
