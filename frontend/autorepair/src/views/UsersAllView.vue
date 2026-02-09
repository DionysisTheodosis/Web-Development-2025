<template>
  <div class="card">
    <Toolbar class="mb-6 hidden md:flex">
      <template #start>
        <Button
          :label="t('allUsers.userList.toolbar.addUser')"
          icon="pi pi-user-plus"
          @click="openNew"
          class="!text-body-sm"
        />
        <Button
          v-if="layout === 'grid'"
          icon="pi pi-list-check"
          class="mr-2 !text-body-sm"
          :class="
            multiSelect
              ? 'text-primary-600 dark:text-primary-400'
              : 'text-surface-500 dark:text-surface-400'
          "
          severity="link"
          @click="toggleSelect"
          v-tooltip.top="t('allUsers.userList.menu.select')"
        />
        <Button
          v-if="selectedItems && selectedItems.length"
          :label="t('allUsers.userList.toolbar.delete')"
          icon="pi pi-trash"
          severity="danger"
          variant="outlined"
          class="ml-4 !text-body-sm"
          @click="confirmDeleteSelected"
          :disabled="
            !selectedItems || !selectedItems.length
          "
        />
      </template>

      <template #end>
        <Button
          :label="t('allUsers.userList.toolbar.import')"
          icon="pi pi-upload"
          severity="secondary"
          class="mr-2 !text-body-sm"
          @click="openImport"
        />
      </template>
    </Toolbar>

    <div
      class="mb-4 flex flex-col gap-4 px-1 md:flex-row md:items-center md:justify-between"
    >
      <div class="flex justify-between">
        <SelectButton
          v-model="layout"
          :options="layoutOptions"
          :allowEmpty="false"
          class="m-2"
        >
          <template #option="{ option }">
            <i
              :class="[
                option === 'list'
                  ? 'pi pi-bars'
                  : 'pi pi-th-large',
              ]"
            />
          </template>
        </SelectButton>
        <Button
          icon="pi pi-ellipsis-v"
          class="flex md:hidden"
          text
          rounded
          severity="secondary"
          @click="toggleMenu"
          aria-haspopup="true"
          aria-controls="overlay_menu"
        />

        <Menu
          ref="menu"
          id="overlay_menu"
          :model="menuItems"
          :popup="true"
        />
      </div>

      <IconField class="w-full md:w-auto">
        <InputIcon>
          <i class="pi pi-search" />
        </InputIcon>
        <InputText
          v-if="filters && filters.global"
          v-model="filters['global'].value"
          :placeholder="
            t(
              'allUsers.userList.placeholders.keywordSearch'
            )
          "
          class="w-full !text-body-sm md:w-64"
        />
      </IconField>
    </div>

    <DataTable
      v-if="layout === 'list'"
      tableStyle="min-width: 50rem"
      ref="dt"
      v-model:selection="selectedItems"
      v-model:filters="filters"
      @row-select="onRowSelect"
      filterDisplay="row"
      :value="items"
      scrollable
      scrollHeight="flex"
      dataKey="id"
      :paginator="true"
      :rows="rowsPerPage"
      :totalRecords="totalRecords"
      :lazy="true"
      :loading="isLoading"
      paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
      :rowsPerPageOptions="[5, 10, 25, 50]"
      currentPageReportTemplate="{first} - {last} of {totalRecords}"
      @page="onPage"
      @sort="onSort"
      :sortField="sortField"
      :sortOrder="sortOrder"
      :first="firstRecordIndex"
      :globalFilterFields="[
        'username',
        'lastName',
        'taxNumber',
      ]"
      class="text-body-sm"
    >
      <Column
        selectionMode="multiple"
        :exportable="false"
      ></Column>

      <Column
        field="id"
        :header="t('allUsers.userList.columns.id')"
        sortable
        class="!text-body-sm"
      ></Column>

      <Column
        field="username"
        :header="t('allUsers.userList.columns.username')"
        sortable
        :showFilterMenu="false"
      >
        <template #body="{ data }">
          <span
            class="text-body-sm font-bold text-surface-900 dark:text-surface-0"
          >
            {{ data.username }}
          </span>
        </template>
        <template #filter="{ filterModel, filterCallback }">
          <InputText
            v-model="filterModel.value"
            type="text"
            @keydown.enter="filterCallback()"
            :placeholder="
              t('allUsers.userList.placeholders.search')
            "
            class="w-28 !text-body-sm"
          />
        </template>
      </Column>

      <Column
        field="firstName"
        :header="t('allUsers.userList.columns.firstname')"
        sortable
        class="!text-body-sm"
      ></Column>

      <Column
        field="lastName"
        :header="t('allUsers.userList.columns.lastname')"
        sortable
        :showFilterMenu="false"
        class="!text-body-sm"
      >
        <template #body="{ data }">
          {{ data.lastName }}
        </template>
        <template #filter="{ filterModel, filterCallback }">
          <InputText
            v-model="filterModel.value"
            type="text"
            @keydown.enter="filterCallback()"
            :placeholder="
              t('allUsers.userList.placeholders.search')
            "
            class="w-28 !text-body-sm"
          />
        </template>
      </Column>

      <Column
        v-if="!presetRole || presetRole === 'MECHANIC'"
        field="specialty"
        :header="t('allUsers.userList.columns.specialty')"
        sortable
      >
        <template #body="{ data }">
          <Tag
            v-if="data.specialty"
            :value="data.specialty"
            severity="info"
            class="!text-xs"
          />
          <span
            v-else
            class="block w-full text-center text-body-sm text-surface-400"
          >
            -
          </span>
        </template>
      </Column>

      <Column
        v-if="!presetRole || presetRole === 'CUSTOMER'"
        field="address"
        :header="t('allUsers.userList.columns.address')"
      >
        <template #body="{ data }">
          <span
            v-if="data.address"
            :title="data.address"
            class="block max-w-[15rem] truncate text-body-sm"
          >
            {{ data.address }}
          </span>
          <span
            v-else
            class="block w-full text-center text-body-sm text-surface-400"
          >
            -
          </span>
        </template>
      </Column>

      <Column
        v-if="!presetRole || presetRole === 'CUSTOMER'"
        field="taxNumber"
        :header="t('allUsers.userList.columns.taxNumber')"
        :showFilterMenu="false"
      >
        <template #body="{ data }">
          <span
            v-if="data.taxNumber"
            class="font-mono text-body-sm"
          >
            {{ data.taxNumber }}
          </span>
          <span
            v-else
            class="block w-full text-center text-body-sm text-surface-400"
          >
            -
          </span>
        </template>
        <template #filter="{ filterModel, filterCallback }">
          <InputText
            v-model="filterModel.value"
            type="text"
            @keydown.enter="filterCallback()"
            :placeholder="
              t('allUsers.userList.placeholders.search')
            "
            class="w-28 !text-body-sm"
          />
        </template>
      </Column>

      <Column
        field="identityNumber"
        :header="t('allUsers.userList.columns.personalID')"
        class="font-mono !text-body-sm"
      ></Column>

      <Column
        field="email"
        :header="t('allUsers.userList.columns.email')"
        sortable
        class="!text-body-sm"
      ></Column>

      <Column
        v-if="!presetRole"
        field="role"
        :header="t('allUsers.userList.columns.role')"
        sortable
      >
        <template #body="slotProps">
          <Tag
            :value="
              t(
                'roles.' + slotProps.data.role.toLowerCase()
              )
            "
            :severity="getRoleSeverity(slotProps.data.role)"
            class="!text-xs uppercase"
          />
        </template>
      </Column>

      <Column
        field="active"
        :header="t('allUsers.userList.columns.status')"
        sortable
      >
        <template #body="slotProps">
          <Tag
            v-if="slotProps.data.role != 'SECRETARY'"
            :value="
              slotProps.data.active
                ? t('userProfile.status.active')
                : t('userProfile.status.pending')
            "
            :severity="
              getStatusLabel(slotProps.data.active)
            "
            class="!text-xs"
          />
        </template>
      </Column>

      <Column
        :exportable="false"
        :header="t('allUsers.userList.columns.actions')"
      >
        <template #body="slotProps">
          <div class="flex gap-2">
            <Button
              v-if="slotProps.data.role != 'SECRETARY'"
              :icon="
                slotProps.data.active
                  ? 'pi pi-lock'
                  : 'pi pi-check'
              "
              :severity="
                slotProps.data.active ? 'warn' : 'success'
              "
              outlined
              rounded
              size="small"
              class="mr-2"
              v-tooltip.top="
                slotProps.data.active
                  ? t('allUsers.userList.actions.lock')
                  : t('allUsers.userList.actions.approve')
              "
              @click="onToggleStatus(slotProps.data)"
              :loading="isToggling(slotProps.data.id)"
            />
            <Button
              icon="pi pi-eye"
              rounded
              variant="outlined"
              severity="secondary"
              size="small"
              @click="onViewUser(slotProps.data)"
              v-tooltip.top="
                t('allUsers.userList.actions.view')
              "
            />
            <Button
              icon="pi pi-pencil"
              rounded
              variant="outlined"
              severity="info"
              size="small"
              @click="onEditUser(slotProps.data)"
              v-tooltip.top="
                t('allUsers.userList.actions.edit')
              "
            />
            <Button
              v-if="slotProps.data.role != 'SECRETARY'"
              icon="pi pi-trash"
              rounded
              variant="outlined"
              severity="danger"
              size="small"
              @click="confirmDeleteItem(slotProps.data)"
              v-tooltip.top="
                t('allUsers.userList.actions.delete')
              "
            />
          </div>
        </template>
      </Column>
    </DataTable>

    <DataView
      v-else
      :value="items"
      :layout="'grid'"
      :paginator="true"
      :rows="rowsPerPage"
      :totalRecords="totalRecords"
      :lazy="true"
      @page="onPage"
      :first="firstRecordIndex"
      :rowsPerPageOptions="[5, 10, 25, 50]"
      paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
      currentPageReportTemplate="{first} - {last} of {totalRecords}"
      class="h-full overflow-y-auto"
    >
      <template #grid="slotProps">
        <div
          class="grid grid-cols-1 gap-4 p-2 pb-14 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 dark:bg-surface-800"
        >
          <div
            v-for="(item, index) in slotProps.items"
            :key="index"
            class="col-span-1"
          >
            <div
              class="group relative flex h-full flex-col rounded-xl border bg-surface-0 p-4 shadow-sm transition-all hover:shadow-md dark:bg-surface-900"
              :class="{
                '!border-primary-500 bg-primary-200 dark:bg-primary-800':
                  selectedItems &&
                  selectedItems.includes(item),
              }"
            >
              <div
                v-if="
                  multiSelect && item.role !== 'SECRETARY'
                "
                class="absolute left-4 top-4 z-10"
              >
                <Checkbox
                  v-model="selectedItems"
                  :value="item"
                />
              </div>

              <div
                v-if="!presetRole"
                class="absolute right-4 top-4"
              >
                <Tag
                  :value="
                    t('roles.' + item.role.toLowerCase())
                  "
                  :severity="getRoleSeverity(item.role)"
                  class="!text-xs uppercase"
                />
              </div>

              <div
                class="mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-surface-100 text-primary dark:bg-surface-800"
              >
                <i class="pi pi-user text-2xl" />
              </div>

              <div class="mb-4 flex flex-col gap-2">
                <!-- Username -->
                <span
                  class="truncate text-lg font-bold text-surface-900 dark:text-surface-0"
                  :title="item.username"
                >
                  {{ item.username }}
                </span>

                <!-- Full Name -->
                <span
                  class="text-sm text-surface-600 dark:text-surface-300"
                >
                  {{ item.firstName }} {{ item.lastName }}
                </span>
              </div>

              <!-- Details Grid -->
              <div
                class="mb-4 grid grid-cols-2 gap-x-4 gap-y-2 text-sm"
              >
                <div class="flex flex-col">
                  <span
                    class="text-xs text-surface-400 dark:text-surface-500"
                  >
                    {{
                      t(
                        'allUsers.userList.columns.personalID'
                      )
                    }}
                  </span>
                  <span
                    class="font-mono font-medium text-surface-700 dark:text-surface-200"
                  >
                    {{ item.identityNumber || '-' }}
                  </span>
                </div>
                <div class="col-span-2 flex flex-col">
                  <span
                    class="text-xs text-surface-400 dark:text-surface-500"
                  >
                    {{
                      t('allUsers.userList.columns.email')
                    }}
                  </span>
                  <span
                    class="break-all font-medium text-surface-700 dark:text-surface-200"
                    :title="item.email"
                  >
                    {{ item.email || '-' }}
                  </span>
                </div>
                <div
                  v-if="item.specialty"
                  class="col-span-2 flex flex-col"
                >
                  <span
                    class="text-xs text-surface-400 dark:text-surface-500"
                  >
                    {{
                      t(
                        'allUsers.userList.columns.specialty'
                      )
                    }}
                  </span>
                  <span
                    class="font-medium text-blue-600 dark:text-blue-400"
                  >
                    {{ item.specialty }}
                  </span>
                </div>
                <div
                  v-if="item.taxNumber"
                  class="flex flex-col"
                >
                  <span
                    class="text-xs text-surface-400 dark:text-surface-500"
                  >
                    {{
                      t(
                        'allUsers.userList.columns.taxNumber'
                      )
                    }}
                  </span>
                  <span
                    class="font-mono font-medium text-surface-700 dark:text-surface-200"
                  >
                    {{ item.taxNumber }}
                  </span>
                </div>
                <div
                  v-if="item.address"
                  class="col-span-2 flex flex-col"
                >
                  <span
                    class="text-xs text-surface-400 dark:text-surface-500"
                  >
                    {{
                      t('allUsers.userList.columns.address')
                    }}
                  </span>
                  <span
                    class="truncate font-medium text-surface-700 dark:text-surface-200"
                    :title="item.address"
                  >
                    {{ item.address }}
                  </span>
                </div>
              </div>

              <div
                class="mt-auto flex items-center justify-between border-t pt-4"
              >
                <Tag
                  v-if="item.role != 'SECRETARY'"
                  :value="
                    item.active
                      ? t('userProfile.status.active')
                      : t('userProfile.status.pending')
                  "
                  :severity="getStatusLabel(item.active)"
                  class="!text-xs"
                />
                <div class="flex gap-1">
                  <Button
                    v-if="item.role != 'SECRETARY'"
                    :icon="
                      item.active
                        ? 'pi pi-lock'
                        : 'pi pi-check'
                    "
                    :severity="
                      item.active ? 'warn' : 'success'
                    "
                    text
                    rounded
                    size="small"
                    class="mr-2"
                    v-tooltip.top="
                      item.active
                        ? t(
                            'allUsers.userList.actions.lock'
                          )
                        : t(
                            'allUsers.userList.actions.approve'
                          )
                    "
                    @click="onToggleStatus(item)"
                    :loading="isToggling(item.id)"
                  />
                  <Button
                    icon="pi pi-eye"
                    text
                    rounded
                    severity="secondary"
                    size="small"
                    @click="onViewUser(item)"
                    v-tooltip.top="
                      t(
                        'allUsers.userList.actions.viewProfile'
                      )
                    "
                  />
                  <Button
                    icon="pi pi-pencil"
                    text
                    rounded
                    severity="info"
                    size="small"
                    @click="onEditUser(item)"
                    v-tooltip.top="
                      t(
                        'allUsers.userList.actions.editUser'
                      )
                    "
                  />
                  <Button
                    v-if="item.role != 'SECRETARY'"
                    icon="pi pi-trash"
                    text
                    rounded
                    severity="danger"
                    size="small"
                    @click="confirmDeleteItem(item)"
                    v-tooltip.top="
                      t(
                        'allUsers.userList.actions.deleteUser'
                      )
                    "
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </DataView>

    <UsersImportDialog
      v-model:visible="importDialog"
      @import-completed="refreshTable"
    />

    <UserCreateDialog
      v-model:visible="createDialogVisible"
      :default-role="presetRole"
      @user-created="refreshTable"
    />
  </div>
</template>

<script setup>
  import {
    ref,
    computed,
    onMounted,
    defineProps,
  } from 'vue'
  import { useRouter } from 'vue-router'
  import { onKeyStroke } from '@vueuse/core'
  import { useI18n } from 'vue-i18n'


  import Menu from 'primevue/menu'
  import Checkbox from 'primevue/checkbox'
  import Toolbar from 'primevue/toolbar'
  import Button from 'primevue/button'
  import DataTable from 'primevue/datatable'
  import DataView from 'primevue/dataview'
  import Column from 'primevue/column'
  import IconField from 'primevue/iconfield'
  import InputIcon from 'primevue/inputicon'
  import InputText from 'primevue/inputtext'
  import Tag from 'primevue/tag'
  import SelectButton from 'primevue/selectbutton'
  import UsersImportDialog from '@/features/users/components/UsersImportDialog.vue'
  import UserCreateDialog from '@/features/users/components/NewUserDialog.vue'

  import { useAppConfirm } from '@/composables/useAppConfirmDialog.js'
  import { useUsersList } from '@/features/users/composables/useUsersList.js'

  const router = useRouter()
  const { requireDelete } = useAppConfirm()
  const { t } = useI18n()
  // ✅ 1. PROPS
  const props = defineProps({
    presetRole: {
      type: String,
      default: null,
    },
  })


  const {
    items,
    totalRecords,
    isLoading,
    filters,
    selectedItems,
    rowsPerPage,
    firstRecordIndex,
    sortField,
    sortOrder,
    onPage,
    onSort,
    refreshTable,
    confirmDeleteItem,
    confirmDeleteSelected,
    onToggleStatus,
    isToggling,
  } = useUsersList(requireDelete, props.presetRole)


  const layout = ref('list')
  const layoutOptions = ref(['list', 'grid'])
  const importDialog = ref(false)
  const createDialogVisible = ref(false)

  onMounted(() => {
    if (window.innerWidth < 768) {
      layout.value = 'grid'
    }
  })

  const openNew = () => {
    createDialogVisible.value = true
  }
  const openImport = () => {
    importDialog.value = true
  }
  const onViewUser = (user) => {
    router.push(`/users/${user.id}`)
  }
  const onEditUser = (user) => {
    router.push(`/users/${user.id}?edit=true`)
  }

  const toggleMenu = (event) => {
    menu.value.toggle(event)
  }

  const toggleSelect = (event) => {
    onKeyStroke('Escape', (e) => {
      e.preventDefault()
      multiSelect.value = false
      selectedItems.value = multiSelect.value
        ? []
        : undefined
    })
    multiSelect.value = !multiSelect.value
    selectedItems.value = multiSelect.value ? [] : undefined
  }

  const menu = ref()
  const multiSelect = ref(false)

  const menuItems = computed(() => [
    ...(layout.value === 'grid'
      ? [
          {
            label: t('allUsers.userList.menu.select'),
            icon: 'pi pi-list-check',
            command: () => {
              toggleSelect()
            },
            class: multiSelect.value
              ? 'text-primary-600 dark:text-primary-400 bg-primary-50 dark:bg-primary-900/20 font-bold'
              : '',
          },
        ]
      : []),
    {
      label: t('allUsers.userList.toolbar.addUser'),
      icon: 'pi pi-user-plus',
      command: () => openNew(),
    },
    {
      label: t('allUsers.userList.toolbar.import'),
      icon: 'pi pi-download',
      command: () => openImport(),
    },
    {
      label: t('allUsers.userList.toolbar.export'),
      icon: 'pi pi-upload',
      command: () => exportCSV(),
    },
    { separator: true },
    {
      label: t('allUsers.userList.menu.deleteSelected'),
      icon: 'pi pi-trash',
      class: 'text-red-500',
      disabled:
        !selectedItems.value ||
        selectedItems.value.length === 0,
      command: () => confirmDeleteSelected(),
    },
  ])
  const onRowSelect = (event) => {
    if (event.data.role === 'SECRETARY') {
      selectedItems.value = selectedItems.value.filter(
        (user) => user.id !== event.data.id
      )
    }
  }
  const getStatusLabel = (active) =>
    active ? 'success' : 'warn'

  const getRoleSeverity = (role) => {
    if (!role) return null
    switch (role.toUpperCase()) {
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
</script>
