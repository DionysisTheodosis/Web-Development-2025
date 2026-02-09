<script setup>
  import { computed } from 'vue'
  import { useAuth } from '@/features/auth/useAuth.js'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()
  const { data: user } = useAuth()

  const greeting = computed(() => {
    const hour = new Date().getHours()
    if (hour < 12) return t('dashboard.greetings.morning')
    if (hour < 18) return t('dashboard.greetings.afternoon')
    return t('dashboard.greetings.evening')
  })
</script>

<template>
  <div
    class="flex h-full flex-col items-center justify-center p-8 text-center text-surface-600 dark:text-surface-200"
  >
    <h1 class="mb-4 text-4xl font-bold">
      {{ greeting }},
      {{ user?.firstName || user?.username }}!
    </h1>
    <p class="text-xl opacity-80">
      {{ t('dashboard.mechanicWelcome') }}
    </p>
  </div>
</template>
