<template>
  <Dialog
    :visible="visible"
    @update:visible="emit('update:visible', $event)"
    :style="{ width: '700px' }"
    :header="t('allCars.import.dialog_title')"
    :modal="true"
  >
    <div class="flex flex-col gap-5">
      <div
        class="rounded-md border border-blue-200 bg-blue-50 p-4 text-sm text-blue-700"
      >
        <div class="mb-3 flex items-center gap-2">
          <i class="pi pi-info-circle text-xl"></i>
          <strong class="text-base">
            {{ t('allCars.import.title') }}
          </strong>
        </div>

        <div class="ml-2 space-y-4">
          <p
            class="font-medium text-blue-900"
            v-html="t('allCars.import.intro')"
          ></p>

          <div class="space-y-4">
            <div>
              <h5
                class="mb-2 border-b border-blue-200 pb-1 font-bold text-blue-900"
              >
                {{ t('allCars.import.general_section') }}
              </h5>
              <ul
                class="ml-4 list-outside list-disc space-y-2 text-xs text-blue-800"
              >
                <li>
                  <strong class="text-blue-900">
                    {{
                      t('allCars.import.required_fields')
                    }}
                  </strong>
                  <br />
                  <span class="italic opacity-90">
                    {{
                      t(
                        'allCars.import.required_fields_desc'
                      )
                    }}
                  </span>
                </li>
                <li>
                  {{
                    t('allCars.import.serial_rule_prefix')
                  }}
                  <span
                    class="rounded bg-blue-100 px-1 font-mono font-bold text-blue-900"
                  >
                    serialNumber
                  </span>
                  {{
                    t('allCars.import.serial_rule_suffix')
                  }}
                </li>
                <li>
                  {{ t('allCars.import.date_format') }}
                </li>
              </ul>
            </div>

            <div>
              <h5
                class="mb-2 border-b border-blue-200 pb-1 font-bold text-blue-900"
              >
                {{ t('allCars.import.types_section') }}
              </h5>
              <ul
                class="ml-4 list-outside list-disc space-y-2 text-xs text-blue-800"
              >
                <li>
                  <strong class="text-blue-900">
                    {{
                      t('allCars.import.car_types_label')
                    }}:
                  </strong>
                  <span
                    class="rounded bg-blue-100 px-1 font-mono"
                  >
                    PASSENGER
                  </span>
                  ,
                  <span
                    class="rounded bg-blue-100 px-1 font-mono"
                  >
                    TRUCK
                  </span>
                  ,
                  <span
                    class="rounded bg-blue-100 px-1 font-mono"
                  >
                    BUS
                  </span>
                </li>
                <li>
                  <strong class="text-blue-900">
                    {{
                      t('allCars.import.fuel_types_label')
                    }}:
                  </strong>
                  <span
                    class="rounded bg-blue-100 px-1 font-mono"
                  >
                    ELECTRIC
                  </span>
                  ,
                  <span
                    class="rounded bg-blue-100 px-1 font-mono"
                  >
                    DIESEL
                  </span>
                  ,
                  <span
                    class="rounded bg-blue-100 px-1 font-mono"
                  >
                    LPG
                  </span>
                  ,
                  <span
                    class="rounded bg-blue-100 px-1 font-mono"
                  >
                    HYBRID
                  </span>
                </li>
              </ul>
            </div>

            <div>
              <h5
                class="mb-2 border-b border-blue-200 pb-1 font-bold text-blue-900"
              >
                {{ t('allCars.import.numeric_section') }}
              </h5>
              <ul
                class="ml-4 list-outside list-disc space-y-2 text-xs text-blue-800"
              >
                <li>
                  {{ t('allCars.import.door_count_rule') }}
                </li>
                <li>
                  {{ t('allCars.import.wheel_count_rule') }}
                </li>
                <li>{{ t('allCars.import.year_rule') }}</li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <div
        class="flex flex-col items-center justify-between gap-2 border-b border-surface-200 pb-4 sm:flex-row dark:border-surface-700"
      >
        <span
          class="font-semibold text-surface-600 dark:text-surface-300"
        >
          {{ t('allCars.import.template_label') }}
        </span>
        <div class="flex gap-2">
          <Button
            :label="t('allCars.import.download_excel')"
            icon="pi pi-file-excel"
            size="small"
            variant="outlined"
            severity="success"
            @click="downloadTemplate('excel')"
            v-tooltip.top="
              t('allCars.import.download_excel_tooltip')
            "
          />
          <Button
            :label="t('allCars.import.download_csv')"
            icon="pi pi-file"
            size="small"
            variant="outlined"
            severity="secondary"
            @click="downloadTemplate('csv')"
            v-tooltip.top="
              t('allCars.import.download_csv_tooltip')
            "
          />
        </div>
      </div>

      <div class="mt-1">
        <FileUpload
          mode="advanced"
          name="file"
          accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"
          :maxFileSize="1000000"
          :customUpload="true"
          @uploader="onUploadHandler"
          :auto="true"
          :chooseLabel="t('allCars.import.browse_file')"
          class="w-full"
          :showUploadButton="false"
          :showCancelButton="false"
        >
          <template #empty>
            <div
              class="flex flex-col items-center justify-center p-4"
            >
              <i
                class="pi pi-cloud-upload mb-2 text-4xl text-surface-400"
              ></i>
              <p class="text-surface-500">
                {{ t('allCars.import.drag_drop') }}
              </p>
            </div>
          </template>
        </FileUpload>
      </div>
    </div>

    <template #footer>
      <Button
        :label="t('allCars.import.close')"
        icon="pi pi-times"
        text
        @click="emit('update:visible', false)"
      />
    </template>
  </Dialog>
</template>

<script setup>
  import { defineProps, defineEmits } from 'vue'
  import { useToast } from 'primevue/usetoast'
  import { useI18n } from 'vue-i18n'

  // Import Service Functions
  import {
    downloadCarImportTemplate,
    importCarsFile,
  } from '@/features/cars/services/carService.js'

  // --- Components ---
  import Dialog from 'primevue/dialog'
  import Button from 'primevue/button'
  import FileUpload from 'primevue/fileupload'

  const props = defineProps({
    visible: {
      type: Boolean,
      required: true,
    },
  })

  const emit = defineEmits(['update:visible', 'success'])
  const toast = useToast()
  const { t } = useI18n()

  const downloadTemplate = async (type) => {
    try {
      const response = await downloadCarImportTemplate(type)

      const extension = type === 'excel' ? 'xlsx' : 'csv'
      const fileName = `car_import_template.${extension}`
      const url = window.URL.createObjectURL(
        new Blob([response.data])
      )
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', fileName)
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    } catch (error) {
      console.error('Download failed', error)
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: t('allCars.import.toasts.download_error'),
        life: 3000,
      })
    }
  }

  const onUploadHandler = async (event) => {
    const file = event.files[0]

    try {
      await importCarsFile(file)

      toast.add({
        severity: 'success',
        summary: t('allCars.import.toasts.success'),
        detail: t('allCars.import.toasts.success'),
        life: 3000,
      })

      emit('update:visible', false)
      emit('success')
    } catch (error) {
      console.error('Upload Error:', error)

      toast.add({
        severity: 'error',
        summary: t('allCars.import.toasts.server_error'),
        detail: error.message,
        life: 5000,
      })
    }
  }
</script>
