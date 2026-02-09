<template>
  <ConfirmDialog group="headless">
    <template #container="{ message, acceptCallback, rejectCallback }">
      <div class="flex flex-col items-center p-8 bg-surface-0 dark:bg-surface-900 rounded-lg shadow-xl border border-surface-200 dark:border-surface-700 w-full max-w-md">

        <div class="rounded-full inline-flex justify-center items-center h-24 w-24 -mt-20 mb-6 shadow-md"
             :class="message.iconClass || 'bg-primary text-primary-contrast'">
          <i :class="[message.icon || 'pi pi-question', '!text-4xl']"></i>
        </div>

        <span class="font-bold text-2xl block mb-2 text-center text-surface-900 dark:text-surface-0">
                    {{ message.header }}
                </span>
        <p class="mb-0 text-center text-surface-600 dark:text-surface-200">
          {{ message.message }}
        </p>

        <div class="flex items-center gap-3 mt-8 w-full">
          <Button
            :label="message.rejectLabel || t('confirmDialog.cancelButton')"
            variant="outlined"
            @click="rejectCallback"
            class="flex-1"
            severity="secondary"
          ></Button>

          <Button
            :label="message.acceptLabel || t('confirmDialog.confirmButton')"
            :severity="message.acceptSeverity || 'primary'"
            @click="acceptCallback"
            class="flex-1"
          ></Button>
        </div>

      </div>
    </template>
  </ConfirmDialog>
  <Toast />
</template>
<script setup>
  import Toast from 'primevue/toast';
  import ConfirmDialog from 'primevue/confirmdialog';
  import Button from 'primevue/button';
  import { useI18n } from 'vue-i18n'
  const {t} = useI18n()
</script>