<script setup>
  import { computed, useTemplateRef } from 'vue';
  import { RouterLink, useRouter } from 'vue-router';
  import { useI18n } from 'vue-i18n';
  import useOnScrollCallback from '@/composables/useOnScroll.js';
  import { useAuth } from '@/features/auth/useAuth.js';

  // Components
  import Button from 'primevue/button';
  import Popover from 'primevue/popover';
  import Avatar from 'primevue/avatar';
  import Divider from 'primevue/divider';

  const { t } = useI18n();
  const router = useRouter();
  const popoverRef = useTemplateRef('popoverRef');

  // Logic
  const { data: user, isAuthenticated, logout } = useAuth();

  const avatarText = computed(() => {
    if (!user.value?.firstName || !user.value?.lastName) return '';
    return `${user.value.firstName[0]}${user.value.lastName[0]}`.toUpperCase();
  });

  const handleLogout = async () => {
    await logout();
    await router.push('/');
  };

  const toggle = (event) => {
    popoverRef.value.toggle(event);
  };

  // Keep popover aligned on scroll
  useOnScrollCallback(() => {
    if (popoverRef.value?.visible) {
      popoverRef.value.alignOverlay();
    }
  });
</script>

<template>
  <Button
    :pt="{
      root: { class: 'w-10 h-10 text-surface-200 bg-primary-700 hover:border-primary-100 rounded-full' },
      buttonLabel: { class: 'text-surface-900 group-hover:text-primary transition-colors text-body-sm' },
    }"
    type="button"
    @click="toggle"
    :icon="!isAuthenticated ? 'pi pi-user' : null"
    :label="isAuthenticated ? avatarText : null"
    text
    aria-haspopup="true"
    aria-controls="profile-popover"
  />

  <Popover id="profile-popover" ref="popoverRef">
    <div
      v-if="!isAuthenticated"
      class="flex flex-col space-y-2 px-4 py-3"
    >
      <RouterLink to="/register">
        <Button icon="pi pi-user-plus" :label="t('register')" class="w-full !text-body-sm" />
      </RouterLink>
      <RouterLink to="/login">
        <Button icon="pi pi-sign-in" :label="t('login')" severity="secondary" class="w-full !text-body-sm" />
      </RouterLink>
    </div>

    <div v-else class="flex flex-col space-y-2 min-w-[200px]">
      <div class="flex items-center space-x-3 p-3">
        <Avatar :label="avatarText" shape="circle" size="normal" class="shrink-0" />
        <div class="flex flex-col overflow-hidden">
          <span class="text-xs text-surface-500 font-medium uppercase tracking-wide">
            {{ t('route.loggedAs') }}
          </span>
          <span class="text-body font-semibold truncate text-surface-900 dark:text-surface-0">
            {{ user?.username }}
          </span>
        </div>
      </div>

      <Divider class="my-0" />

      <div class="flex flex-col px-2 py-1">
        <RouterLink to="/account" custom v-slot="{ navigate }">
          <Button
            icon="pi pi-user"
            :label="t('route.account')"
            text
            severity="secondary"
            class="w-full justify-start !text-body-sm !px-3"
            @click="navigate"
          />
        </RouterLink>

        <RouterLink to="/settings" custom v-slot="{ navigate }">
          <Button
            icon="pi pi-cog"
            :label="t('route.settings')"
            text
            severity="secondary"
            class="w-full justify-start !text-body-sm !px-3"
            @click="navigate"
          />
        </RouterLink>
      </div>

      <Divider class="my-0" />

      <div class="px-2 pb-2">
        <Button
          icon="pi pi-sign-out"
          :label="t('route.signOut')"
          text
          severity="danger"
          @click="handleLogout"
          class="w-full justify-start !text-body-sm !px-3"
        />
      </div>
    </div>
  </Popover>
</template>