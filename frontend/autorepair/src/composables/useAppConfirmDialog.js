// src/composables/useAppConfirm.js
import { useConfirm } from 'primevue/useconfirm'
import { useI18n } from 'vue-i18n'

export function useAppConfirm() {
  const confirm = useConfirm()
  const { t } = useI18n()

  const showDialog = (options) => {
    confirm.require({
      group: 'headless',
      ...options,
    })
  }

  const requireDelete = (options = {}) => {
    showDialog({
      header:
        options.header || t('confirmDialog.delete.header'),
      message:
        options.message ||
        t('confirmDialog.delete.message'),
      acceptLabel:
        options.acceptLabel ||
        t('confirmDialog.delete.confirm'),
      rejectLabel:
        options.rejectLabel ||
        t('confirmDialog.cancelButton'),

      icon: 'pi pi-trash',
      iconClass: 'bg-red-500 text-white',
      acceptSeverity: 'danger',

      accept: options.accept || (() => {}),
      reject: options.reject || (() => {}),
    })
  }

  const requireSave = (options = {}) => {
    showDialog({
      header:
        options.header || t('confirmDialog.save.header'),
      message:
        options.message || t('confirmDialog.save.message'),
      acceptLabel:
        options.acceptLabel ||
        t('confirmDialog.save.confirm'),
      rejectLabel:
        options.rejectLabel ||
        t('confirmDialog.cancelButton'),

      icon: 'pi pi-check',
      iconClass: 'bg-green-500 text-white',
      acceptSeverity: 'success',

      accept: options.accept || (() => {}),
      reject: options.reject || (() => {}),
    })
  }

  const requireUnsavedChanges = (options = {}) => {
    showDialog({
      header:
        options.header || t('confirmDialog.unsaved.header'),
      message:
        options.message ||
        t('confirmDialog.unsaved.message'),
      acceptLabel:
        options.acceptLabel ||
        t('confirmDialog.unsaved.confirm'),
      rejectLabel:
        options.rejectLabel ||
        t('confirmDialog.unsaved.stay'),

      icon: 'pi pi-exclamation-triangle',
      iconClass: 'bg-orange-500 text-white',
      acceptSeverity: 'danger',

      accept: options.accept || (() => {}),
      reject: options.reject || (() => {}),
    })
  }

  return {
    requireDelete,
    requireSave,
    requireUnsavedChanges,
  }
}
