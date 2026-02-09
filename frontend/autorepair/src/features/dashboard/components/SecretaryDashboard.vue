<script setup>
  import { computed } from 'vue'
  import { useRouter } from 'vue-router'

  import DataTable from 'primevue/datatable'
  import Column from 'primevue/column'
  import Button from 'primevue/button'
  import Tag from 'primevue/tag'
  import Skeleton from 'primevue/skeleton'

  import { useI18n } from 'vue-i18n'

  import { useDashboard } from '@/features/dashboard/composables/useDashboard.js'

  const router = useRouter()
  const { t } = useI18n()

  const {
    stats,
    statsLoading,
    recentUsers,
    recentLoading,
  } = useDashboard()


  const tableData = computed(() => {
    return recentUsers.value?.content || []
  })

  const getRoleSeverity = (role) => {
    if (!role) return null
    switch (role) {
      case 'SECRETARY':
        return 'info'
      case 'MECHANIC':
        return 'secondary'
      case 'CUSTOMER':
        return 'primary'
      default:
        return null
    }
  }

  const goToPendingUsers = () => {
    router.push('/users?status=pending')
  }
</script>

<template>
  <div class="space-y-6">
    <div class="mb-6">
      <h1
        class="text-title-2 font-bold text-surface-900 dark:text-surface-0"
      >
        {{ t('dashboard.secretaryTitle') }}
      </h1>
      <p
        class="text-body text-surface-500 dark:text-surface-400"
      >
        {{ t('dashboard.welcome') }}
      </p>
    </div>

    <div
      class="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-4"
    >
      <!-- PENDING USERS CARD -->
      <div
        class="card group cursor-pointer rounded-r-xl border-l-4 border-orange-500 bg-orange-50 p-4 shadow-sm transition-all hover:shadow-md dark:bg-orange-900/20"
        @click="goToPendingUsers"
      >
        <div class="mb-2 flex items-start justify-between">
          <div>
            <span
              class="block text-xs font-medium uppercase tracking-wider text-surface-500 dark:text-surface-400"
            >
              {{ t('dashboard.secretary.pendingApproval') }}
            </span>

            <div v-if="statsLoading" class="mt-2">
              <Skeleton width="2rem" height="2rem" />
            </div>
            <span
              v-else
              class="mt-1 block text-title-2 font-bold text-orange-600 dark:text-orange-400"
            >
              {{ stats?.pendingUsers || 0 }}
            </span>
          </div>
          <div
            class="flex h-10 w-10 items-center justify-center rounded-full bg-orange-100 text-orange-600 transition-colors group-hover:bg-orange-200 dark:bg-orange-800 dark:text-orange-300"
          >
            <i class="pi pi-exclamation-circle text-xl"></i>
          </div>
        </div>
        <span
          class="text-body-sm font-medium text-orange-600/80 dark:text-orange-400/80"
        >
          {{ t('dashboard.secretary.requiresAttention') }}
        </span>
      </div>

      <!-- TOTAL USERS CARD -->
      <div
        class="card rounded-xl border border-surface-200 bg-surface-0 p-4 shadow-sm dark:border-surface-700 dark:bg-surface-800"
      >
        <div class="flex items-start justify-between">
          <div>
            <span
              class="block text-xs font-medium uppercase tracking-wider text-surface-500 dark:text-surface-400"
            >
              {{ t('dashboard.secretary.totalUsers') }}
            </span>
            <div v-if="statsLoading" class="mt-2">
              <Skeleton width="3rem" height="2rem" />
            </div>
            <span
              v-else
              class="mt-1 block text-title-2 font-bold text-surface-900 dark:text-surface-0"
            >
              {{ stats?.totalUsers || 0 }}
            </span>
          </div>
          <div
            class="flex h-10 w-10 items-center justify-center rounded-full bg-primary-100 text-primary-600 dark:bg-primary-900/40 dark:text-primary-400"
          >
            <i class="pi pi-users text-xl"></i>
          </div>
        </div>
      </div>

      <!-- MECHANICS CARD -->
      <div
        class="card rounded-xl border border-surface-200 bg-surface-0 p-4 shadow-sm dark:border-surface-700 dark:bg-surface-800"
      >
        <div class="flex items-start justify-between">
          <div>
            <span
              class="block text-xs font-medium uppercase tracking-wider text-surface-500 dark:text-surface-400"
            >
              {{ t('dashboard.secretary.mechanics') }}
            </span>
            <div v-if="statsLoading" class="mt-2">
              <Skeleton width="2rem" height="2rem" />
            </div>
            <span
              v-else
              class="mt-1 block text-title-2 font-bold text-surface-900 dark:text-surface-0"
            >
              {{ stats?.activeMechanics || 0 }}
            </span>
          </div>
          <div
            class="flex h-10 w-10 items-center justify-center rounded-full bg-cyan-100 text-cyan-600 dark:bg-cyan-900/40 dark:text-cyan-400"
          >
            <i class="pi pi-wrench text-xl"></i>
          </div>
        </div>
        <span
          class="mt-2 block text-body-sm text-surface-500"
        >
          {{ t('dashboard.secretary.activeStaff') }}
        </span>
      </div>

      <!-- CUSTOMERS CARD -->
      <div
        class="card rounded-xl border border-surface-200 bg-surface-0 p-4 shadow-sm dark:border-surface-700 dark:bg-surface-800"
      >
        <div class="flex items-start justify-between">
          <div>
            <span
              class="block text-xs font-medium uppercase tracking-wider text-surface-500 dark:text-surface-400"
            >
              {{ t('dashboard.secretary.customers') }}
            </span>
            <div v-if="statsLoading" class="mt-2">
              <Skeleton width="2rem" height="2rem" />
            </div>
            <span
              v-else
              class="mt-1 block text-title-2 font-bold text-surface-900 dark:text-surface-0"
            >
              {{ stats?.activeCustomers || 0 }}
            </span>
          </div>
          <div
            class="flex h-10 w-10 items-center justify-center rounded-full bg-indigo-100 text-indigo-600 dark:bg-indigo-900/40 dark:text-indigo-400"
          >
            <i class="pi pi-car text-xl"></i>
          </div>
        </div>
        <span
          class="mt-2 block text-body-sm text-surface-500"
        >
          {{ t('dashboard.secretary.registeredClients') }}
        </span>
      </div>
    </div>

    <!-- RECENT REGISTRATIONS TABLE (Full Width since chart is gone) -->
    <div
      class="card rounded-xl border border-surface-200 bg-surface-0 p-1 shadow-md dark:border-surface-700 dark:bg-surface-800"
    >
      <div
        class="flex items-center justify-between border-b border-surface-100 p-5 dark:border-surface-700"
      >
        <h3
          class="text-title-4 font-bold text-surface-900 dark:text-surface-0"
        >
          {{ t('dashboard.secretary.recentRegistrations') }}
        </h3>
        <Button
          :label="t('dashboard.secretary.viewAll')"
          icon="pi pi-arrow-right"
          text
          size="small"
          @click="router.push('/users')"
        />
      </div>

      <DataTable
        :value="tableData"
        :loading="recentLoading"
        scrollable
        scrollHeight="300px"
        tableStyle="min-width: 40rem"
      >
        <Column
          field="username"
          :header="t('dashboard.secretary.columns.user')"
        >
          <template #body="{ data }">
            <div class="flex flex-col">
              <span
                class="font-bold text-surface-900 dark:text-surface-0"
              >
                {{ data.firstName }} {{ data.lastName }}
              </span>
              <span class="text-body-sm text-surface-500">
                {{ data.username }}
              </span>
            </div>
          </template>
        </Column>

        <Column
          field="role"
          :header="t('dashboard.secretary.columns.role')"
        >
          <template #body="{ data }">
            <Tag
              :value="t('roles.' + data.role.toLowerCase())"
              :severity="getRoleSeverity(data.role)"
              class="px-2 text-[10px] uppercase"
            />
          </template>
        </Column>

        <Column
          :header="t('dashboard.secretary.columns.status')"
          alignFrozen="right"
        >
          <template #body="{ data }">
            <Tag
              :value="
                data.active
                  ? t('userProfile.status.active')
                  : t('userProfile.status.pending')
              "
              :severity="data.active ? 'success' : 'warn'"
              :icon="
                data.active ? 'pi pi-check' : 'pi pi-clock'
              "
            />
          </template>
        </Column>

        <Column
          :header="t('dashboard.secretary.columns.action')"
        >
          <template #body="{ data }">
            <Button
              icon="pi pi-pencil"
              rounded
              text
              severity="secondary"
              size="small"
              @click="
                router.push(`/users/${data.id}?edit=true`)
              "
              v-tooltip.top="
                t('dashboard.secretary.editDetails')
              "
            />
          </template>
        </Column>
      </DataTable>
    </div>
  </div>
</template>
>
