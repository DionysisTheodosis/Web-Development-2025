<script setup>
  import useFooterSection from './useFooterSection.js'
  import { computed } from 'vue'
  import { useAuth } from '@/features/auth/useAuth.js'

  const { data: currentUser } = useAuth()
  const isLoggedIn = computed(() => !!currentUser.value)

  const {
    developerName,
    developer,
    copyright,
    address,
    email,
    phone,
  } = useFooterSection()

  const currentYear = new Date().getFullYear()
</script>

<template>
  <footer
    v-if="!isLoggedIn"
    class="mt-10 bg-surface-300 px-4 py-8 text-center text-surface-700 dark:bg-surface-800 dark:text-surface-200"
  >
    <div class="mx-auto max-w-6xl space-y-4">
      <p class="text-lg font-semibold">
        <span class="text-primary-700">&copy;</span>
        {{ currentYear }} AutoRepair. {{ copyright }}
      </p>
      <address class="not-italic">
        <ul class="list-none p-0">
          <li>
            <i
              class="pi pi-warehouse mr-1 text-primary-600"
            ></i>
            {{ address }}
          </li>
        </ul>
      </address>
      <p>
        <a
          :href="`tel:${phone}`"
          aria-label="Call us at {{ phone }}"
          class="mr-2 transition hover:text-primary-500"
        >
          <i class="pi pi-phone mr-1 text-primary-600"></i>
          {{ phone }}
        </a>
        |
        <a
          :href="`mailto:${email}`"
          aria-label="Email us at {{ email }}"
          class="ml-2 transition hover:text-primary-500"
        >
          <i
            class="pi pi-envelope mr-1 text-primary-600"
          ></i>
          {{ email }}
        </a>
      </p>

      <p
        class="mt-6 items-center text-xs text-surface-500 dark:text-surface-400"
      >
        {{ developer }}
        <a
          class="inline-flex items-center gap-1 hover:text-primary-500"
        >
          <span class="underline">{{ developerName }}</span>
        </a>
      </p>
    </div>
  </footer>

  <footer
    v-else
    class="mb-5 py-5 text-center text-surface-700 dark:text-surface-200"
  >
    <div class="max-w-4-xl mx-auto space-y-4">
      <p class="text-md font-semibold">
        <span class="text-primary-700">&copy;</span>
        {{ currentYear }} AutoRepair. {{ copyright }}
      </p>
      <p
        class="mt-6 items-center text-xs text-surface-500 dark:text-surface-400"
      >
        {{ developer }}
        <a
          class="inline-flex items-center gap-1 hover:text-primary-500"
        >
          <span class="underline">{{ developerName }}</span>
        </a>
      </p>
    </div>
  </footer>
</template>
