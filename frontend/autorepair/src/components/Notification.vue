<script setup>
  import 'primeicons/primeicons.css'
  import { ref, computed, watch } from 'vue'
  import { RouterLink } from 'vue-router'
  import { useScroll } from '@vueuse/core'
  import { useI18n } from 'vue-i18n' // ✅ Import i18n

  import { usePendingUsersInfinite } from '@/composables/usePendingUsers.js'
  import useOnScrollCallback from '@/composables/useOnScroll.js'
  import { useAppConfirm } from '@/composables/useAppConfirmDialog.js'

  // Components
  import OverlayBadge from 'primevue/overlaybadge'
  import Popover from 'primevue/popover'
  import Button from 'primevue/button'

  const { t } = useI18n()
  const { requireDelete } = useAppConfirm()

  // --- LOGIC ---
  const {
    usersQuery,
    allUsers,
    totalElements,
    pendingCount,
    sortOrder,
    toggleSort,
    activateUserMutation,
    denyUserMutation,
  } = usePendingUsersInfinite()

  // Helpers
  const isLoading = usersQuery.isLoading
  const isFetchingNextPage = usersQuery.isFetchingNextPage
  const fetchNextPage = usersQuery.fetchNextPage
  const hasNextPage = usersQuery.hasNextPage

  const popoverRef = ref(null)
  const listRef = ref(null)

  const { arrivedState } = useScroll(listRef, {
    throttle: 100,
  })

  watch(
    () => arrivedState.bottom,
    (isAtBottom) => {
      if (
        isAtBottom &&
        hasNextPage.value &&
        !isFetchingNextPage.value
      ) {
        fetchNextPage()
      }
    }
  )

  useOnScrollCallback(() => {
    if (popoverRef.value?.visible) {
      popoverRef.value.alignOverlay()
    }
  })

  const toggle = (event) => popoverRef.value.toggle(event)

  const sortIcon = computed(() =>
    sortOrder.value === 'desc'
      ? 'pi pi-sort-amount-down'
      : 'pi pi-sort-amount-up'
  )

  const confirmDeny = (user) => {
    requireDelete({
      header: t('pendingUsers.dialogs.denyTitle'),
      message: t('pendingUsers.dialogs.denyMessage'),
      accept: () => denyUserMutation.mutate(user.id),
    })
  }
</script>

<template>
  <div class="p-0">
    <Button
      @click="toggle"
      severity="secondary"
      text
      :pt:root="{ class: 'py-3 px-4' }"
      rounded
    >
      <OverlayBadge
        v-if="pendingCount > 0"
        :value="pendingCount"
        severity="danger"
        size="small"
      >
        <i class="pi pi-bell text-xl" />
      </OverlayBadge>
      <i v-else class="pi pi-bell text-xl" />
    </Button>

    <Popover
      ref="popoverRef"
      class="surface-card border-round w-full max-w-sm p-0 shadow-lg sm:w-96"
    >
      <div
        class="border-b border-surface-200 p-4 dark:border-surface-700"
      >
        <div class="flex items-center justify-between">
          <h3 class="m-0 text-title-7 font-bold text-color">
            {{ t('pendingUsers.title') }} ({{
              totalElements
            }})
          </h3>
          <Button
            @click="toggleSort"
            :icon="sortIcon"
            size="small"
            severity="secondary"
            text
            :disabled="isLoading"
            rounded
            v-tooltip="t('pendingUsers.sortByID')"
          />
        </div>
      </div>

      <div
        ref="listRef"
        class="max-h-[350px] overflow-y-auto"
      >
        <div
          v-if="isLoading && !allUsers.length"
          class="flex items-center justify-center p-6 text-body-sm text-surface-500"
        >
          <i class="pi pi-spin pi-spinner mr-2 text-lg"></i>
          {{ t('pendingUsers.loading') }}
        </div>

        <div v-else-if="allUsers.length">
          <div
            v-for="user in allUsers"
            :key="user.id"
            class="flex items-start justify-between gap-3 border-b border-surface-200 p-4 transition-colors hover:bg-surface-50 dark:border-surface-700 dark:hover:bg-surface-800"
            :class="{
              'pointer-events-none opacity-50':
                usersQuery.isFetching.value &&
                !isFetchingNextPage,
            }"
          >
            <div class="min-w-0 flex-1">
              <RouterLink
                :to="{
                  name: 'user-profile',
                  params: { id: user.id },
                }"
                class="block cursor-pointer truncate text-body font-semibold text-primary-600 hover:text-primary-700 dark:text-primary-400 dark:hover:text-primary-300"
                @click="popoverRef.hide($event)"
              >
                {{ user.firstName }} {{ user.lastName }}
              </RouterLink>

              <div class="mt-1 flex items-center gap-2">
                <i
                  class="pi pi-briefcase text-xs text-surface-500"
                ></i>
                <span
                  class="text-body-sm text-xs uppercase tracking-wide text-surface-600 dark:text-surface-400"
                >
                  {{
                    t('roles.' + user.role.toLowerCase())
                  }}
                </span>
              </div>
            </div>

            <div
              class="flex shrink-0 flex-col items-end gap-2"
            >
              <Button
                icon="pi pi-check"
                size="small"
                severity="success"
                v-tooltip="
                  t('pendingUsers.actions.activate')
                "
                @click="
                  activateUserMutation.mutate(user.id)
                "
                :loading="
                  activateUserMutation.isPending.value
                "
                :disabled="
                  activateUserMutation.isPending.value ||
                  denyUserMutation.isPending.value
                "
                class="!h-8 !w-8"
              />
              <Button
                icon="pi pi-times"
                size="small"
                severity="danger"
                outlined
                v-tooltip="t('pendingUsers.actions.deny')"
                @click="confirmDeny(user)"
                :loading="denyUserMutation.isPending.value"
                :disabled="
                  denyUserMutation.isPending.value ||
                  activateUserMutation.isPending.value
                "
                class="!h-8 !w-8"
              />
            </div>
          </div>

          <div
            v-if="hasNextPage && isFetchingNextPage"
            class="flex items-center justify-center p-3"
          >
            <i
              class="pi pi-spin pi-spinner mr-2 text-primary-500"
            ></i>
            <span class="text-body-sm text-surface-500">
              {{ t('pendingUsers.loadingMore') }}
            </span>
          </div>

          <div
            v-else-if="!hasNextPage"
            class="bg-surface-50 p-3 text-center text-xs text-surface-500 dark:bg-surface-900"
          >
            {{ t('pendingUsers.endOfList') }}
          </div>
        </div>

        <div
          v-else
          class="flex flex-col items-center gap-3 p-8 text-surface-500"
        >
          <i
            class="pi pi-check-circle text-4xl text-primary-200 dark:text-primary-800"
          ></i>
          <p class="m-0 text-body font-medium">
            {{ t('pendingUsers.emptyState.title') }}
          </p>
          <p class="m-0 text-center text-body-sm">
            {{ t('pendingUsers.emptyState.message') }}
          </p>
        </div>
      </div>
    </Popover>
  </div>
</template>
