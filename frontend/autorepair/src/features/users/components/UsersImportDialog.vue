<template>
  <Dialog
    :visible="visible"
    @update:visible="emit('update:visible', $event)"
    :style="{ width: '700px' }"
    :header="t('allUsers.import.dialog_title')"
    :modal="true"
  >
    <div class="flex flex-col gap-5">

      <div class="bg-blue-50 border border-blue-200 text-blue-700 p-4 rounded-md text-sm">
        <div class="flex items-center gap-2 mb-3">
          <i class="pi pi-info-circle text-xl"></i>
          <strong class="text-base">{{ t('allUsers.import.title') }}</strong>
        </div>

        <div class="ml-2 space-y-4">
          <p class="text-blue-900 font-medium" v-html="t('allUsers.import.intro')"></p>

          <div class="space-y-4">
            <div>
              <h5 class="font-bold text-blue-900 mb-2 border-b border-blue-200 pb-1">
                {{ t('allUsers.import.general_section') }}
              </h5>
              <ul class="list-disc list-outside ml-4 text-xs space-y-2 text-blue-800">
                <li>
                  <strong class="text-blue-900">{{ t('allUsers.import.basic_fields') }}</strong>
                  <br>
                  <span class="opacity-90 italic">{{ t('allUsers.import.basic_fields_desc') }}</span>
                </li>
                <li>{{ t('allUsers.import.password_rule') }}</li>
                <li>
                  {{ t('allUsers.import.id_rule_prefix') }}
                  <span class="font-mono bg-blue-100 px-1 rounded font-bold text-blue-900">personalID</span>
                  {{ t('allUsers.import.id_rule_suffix') }}
                </li>
              </ul>
            </div>

            <div>
              <h5 class="font-bold text-blue-900 mb-2 border-b border-blue-200 pb-1">
                {{ t('allUsers.import.roles_section') }}
              </h5>
              <ul class="list-disc list-outside ml-4 text-xs space-y-2 text-blue-800">
                <li>
                  <strong class="text-blue-900">{{ t('allUsers.import.role_customer') }}:</strong>
                  {{ t('allUsers.import.customer_reqs') }}
                </li>
                <li>
                  <strong class="text-blue-900">{{ t('allUsers.import.role_mechanic') }}:</strong>
                  {{ t('allUsers.import.mechanic_reqs') }}
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <div class="flex flex-col sm:flex-row gap-2 justify-between items-center border-b pb-4 border-surface-200 dark:border-surface-700">
        <span class="font-semibold text-surface-600 dark:text-surface-300">
          {{ t('allUsers.import.template_label') }}
        </span>
        <div class="flex gap-2">
          <Button
            :label="t('allUsers.import.download_excel')"
            icon="pi pi-file-excel"
            size="small"
            variant="outlined"
            severity="success"
            @click="downloadTemplate('excel')"
            v-tooltip.top="t('allUsers.import.download_excel_tooltip')"
          />
          <Button
            :label="t('allUsers.import.download_csv')"
            icon="pi pi-file"
            size="small"
            variant="outlined"
            severity="secondary"
            @click="downloadTemplate('csv')"
            v-tooltip.top="t('allUsers.import.download_csv_tooltip')"
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
          :chooseLabel="t('allUsers.import.browse_file')"
          class="w-full"
          :showUploadButton="false"
          :showCancelButton="false"
        >
          <template #empty>
            <div class="flex flex-col items-center justify-center p-4">
              <i class="pi pi-cloud-upload text-4xl text-surface-400 mb-2"></i>
              <p class="text-surface-500">{{ t('allUsers.import.drag_drop') }}</p>
            </div>
          </template>
        </FileUpload>
      </div>
    </div>

    <template #footer>
      <Button
        :label="t('allUsers.import.close')"
        icon="pi pi-times"
        text
        @click="emit('update:visible', false)"
      />
    </template>
  </Dialog>
</template>

<script setup>
  import { defineProps, defineEmits } from 'vue';
  import { useToast } from 'primevue/usetoast';
  import { useI18n } from 'vue-i18n';


  import { downloadImportTemplate, importUsersFile } from '@/features/users/services/userService.js';


  import Dialog from 'primevue/dialog';
  import Button from 'primevue/button';
  import FileUpload from 'primevue/fileupload';

  const props = defineProps({
    visible: {
      type: Boolean,
      required: true
    }
  });

  const emit = defineEmits(['update:visible', 'success']);
  const toast = useToast();
  const { t } = useI18n();

  const downloadTemplate = async (type) => {
    try {
      const response = await downloadImportTemplate(type);

      const extension = type === 'excel' ? 'xlsx' : 'csv';
      const fileName = `user_import_template.${extension}`;
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', fileName);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error("Download failed", error);
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: t('allUsers.import.toasts.download_error'),
        life: 3000
      });
    }
  };

  const onUploadHandler = async (event) => {
    const file = event.files[0];

    try {
      await importUsersFile(file);

      toast.add({
        severity: 'success',
        summary: t('allUsers.import.toasts.success'),
        detail: t('allUsers.import.toasts.success'),
        life: 3000
      });

      emit('update:visible', false);
      emit('success');

    } catch (error) {
      console.error("Upload Error:", error);

      toast.add({
        severity: 'error',
        summary: t('allUsers.import.toasts.server_error'),
        detail: error.message,
        life: 5000
      });
    }
  };
</script>