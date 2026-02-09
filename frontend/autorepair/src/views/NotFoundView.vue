<template>
  <div
    class="flex h-screen w-full items-center justify-center bg-surface-50 dark:bg-surface-900"
  >
    <div
      class="rounded-lg bg-surface-0 p-6 text-center shadow-md dark:bg-surface-800"
    >
      <h1 class="mb-4 text-6xl font-bold text-primary">
        404
      </h1>
      <p
        class="mb-4 text-xl text-surface-700 dark:text-surface-100"
      >
        {{ t('notFound.message') }}
      </p>
      <p
        class="text-lg text-surface-600 dark:text-surface-300"
      >
        {{ t('notFound.redirecting') }}
        {{ elapseTime }} s...
      </p>
      <Button
        :label="t('notFound.goHome')"
        icon="pi pi-home"
        class="p-button-rounded p-button-outlined p-button-primary mt-4"
        @click="goHome"
      />
    </div>
  </div>
</template>

<script setup>
  import { onMounted, ref } from 'vue'
  import { useRouter } from 'vue-router'
  import { useI18n } from 'vue-i18n'
  import Button from 'primevue/button'

  const router = useRouter()
  const { t } = useI18n()
  const elapseTime = ref(5)

  const goHome = () => {
    router.push('/')
  }

  onMounted(() => {
    const countdownInterval = setInterval(() => {
      if (elapseTime.value > 1) {
        elapseTime.value -= 1
      } else {
        clearInterval(countdownInterval)
        router.push('/')
      }
    }, 1000)
  })
</script>
