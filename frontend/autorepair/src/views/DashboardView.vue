<script setup>
  import { computed, defineAsyncComponent } from 'vue'
  import { useAuth } from '@/features/auth/useAuth.js'
  import { useI18n } from 'vue-i18n'


  const SecretaryDashboard = defineAsyncComponent(
    () =>
      import(
        '@/features/dashboard/components/SecretaryDashboard.vue'
      )
  )
  const MechanicDashboard = defineAsyncComponent(
    () =>
      import(
        '@/features/dashboard/components/MechanicDashboard.vue'
      )
  )
  const CustomerDashboard = defineAsyncComponent(
    () =>
      import(
        '@/features/dashboard/components/CustomerDashboard.vue'
      )
  )


  const { data: user } = useAuth()
  const { t } = useI18n()

  const currentDashboard = computed(() => {
    const role = user.value?.role
    switch (role) {
      case 'SECRETARY':
        return SecretaryDashboard
      case 'MECHANIC':
        return MechanicDashboard
      case 'CUSTOMER':
        return CustomerDashboard
      default:
        return null
    }
  })
</script>

<template>
  <div class="p-6">
    <component
      :is="currentDashboard"
      v-if="currentDashboard"
    />

    <div v-else class="p-10 text-center text-surface-500">
      <i
        class="pi pi-shield mb-4 text-4xl text-surface-300"
      ></i>
      <p>{{ t('dashboard.noDashboard') }}</p>
    </div>
  </div>
</template>
