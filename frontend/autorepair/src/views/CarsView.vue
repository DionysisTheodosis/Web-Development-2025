<template>
  <div class="p-6">
    <div
      class="rounded-lg bg-white p-4 shadow-lg dark:bg-surface-800"
    >
      <div class="card">
        <Toolbar class="mb-6 hidden md:flex">
          <template #start>
            <Button
              :label="t('allCars.carList.toolbar.addCar')"
              icon="pi pi-car"
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
              v-tooltip.top="
                t('allUsers.userList.menu.select')
              "
            />
            <Button
              v-if="selectedItems && selectedItems.length"
              :label="t('allCars.carList.toolbar.delete')"
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
              :label="t('allCars.carList.toolbar.import')"
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
                  'allCars.carList.placeholders.keywordSearch'
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
            'serialNumber',
            'brand',
            'model',
          ]"
          class="text-body-sm"
        >
          <Column
            selectionMode="multiple"
            :exportable="false"
          ></Column>

          <Column
            field="id"
            :header="t('allCars.carList.columns.id')"
            sortable
            class="!text-body-sm"
          ></Column>

          <Column
            field="serialNumber"
            :header="
              t('allCars.carList.columns.serialNumber')
            "
            sortable
            :showFilterMenu="false"
            class="font-mono !text-body-sm"
          >
            <template #body="{ data }">
              <span class="font-mono text-body-sm">
                {{ data.serialNumber }}
              </span>
            </template>
            <template
              #filter="{ filterModel, filterCallback }"
            >
              <InputText
                v-model="filterModel.value"
                type="text"
                @keydown.enter="filterCallback()"
                :placeholder="
                  t('allCars.carList.placeholders.search')
                "
                class="w-28 !text-body-sm"
              />
            </template>
          </Column>

          <Column
            field="brand"
            :header="t('allCars.carList.columns.brand')"
            sortable
            :showFilterMenu="false"
          >
            <template #body="{ data }">
              <span
                class="text-body-sm font-bold text-surface-900 dark:text-surface-0"
              >
                {{ data.brand }}
              </span>
            </template>
            <template
              #filter="{ filterModel, filterCallback }"
            >
              <InputText
                v-model="filterModel.value"
                type="text"
                @keydown.enter="filterCallback()"
                :placeholder="
                  t('allCars.carList.placeholders.search')
                "
                class="w-28 !text-body-sm"
              />
            </template>
          </Column>

          <Column
            field="model"
            :header="t('allCars.carList.columns.model')"
            sortable
            :showFilterMenu="false"
            class="!text-body-sm"
          >
            <template #body="{ data }">
              {{ data.model }}
            </template>
            <template
              #filter="{ filterModel, filterCallback }"
            >
              <InputText
                v-model="filterModel.value"
                type="text"
                @keydown.enter="filterCallback()"
                :placeholder="
                  t('allCars.carList.placeholders.search')
                "
                class="w-28 !text-body-sm"
              />
            </template>
          </Column>

          <Column
            v-if="!isCustomer"
            field="ownerInfo"
            :header="t('allCars.carList.columns.ownerInfo')"
            class="!text-body-sm"
          >
            <template #body="{ data }">
              <div
                v-if="data.ownerInfo"
                class="flex flex-col gap-1"
              >
                <div class="flex items-center gap-1">
                  <i
                    class="pi pi-user text-xs text-surface-500"
                  ></i>
                  <span
                    class="font-medium text-surface-900 dark:text-surface-0"
                  >
                    {{ data.ownerInfo.fullName }}
                  </span>
                </div>
                <div
                  class="flex items-center gap-1 text-xs text-surface-500"
                >
                  <i
                    class="pi pi-id-card text-xs text-surface-400"
                  ></i>
                  <span>
                    {{ data.ownerInfo.taxNumber || '-' }}
                  </span>
                </div>
              </div>
              <span v-else class="text-surface-400">-</span>
            </template>
          </Column>

          <Column
            field="carType"
            :header="t('allCars.carList.columns.type')"
            sortable
            class="!text-body-sm"
          >
            <template #body="{ data }">
              <Tag
                :value="
                  t(
                    'carTypes.' +
                      data.carType?.toLowerCase()
                  )
                "
                severity="info"
                class="!text-xs"
              />
            </template>
          </Column>

          <Column
            field="fuelType"
            :header="t('allCars.carList.columns.fuel')"
            sortable
            class="!text-body-sm"
          >
            <template #body="{ data }">
              <Tag
                :value="
                  t(
                    'fuelType.' +
                      data.fuelType?.toLowerCase()
                  )
                "
                severity="secondary"
                class="!text-xs"
              />
            </template>
          </Column>

          <Column
            field="productionDate"
            :header="
              t('allCars.carList.columns.productionDate')
            "
            sortable
            class="!text-body-sm"
          >
            <template #body="{ data }">
              {{
                new Date(
                  data.productionDate
                ).toLocaleDateString()
              }}
            </template>
          </Column>

          <Column
            field="acquisitionYear"
            :header="
              t('allCars.carList.columns.acquisitionYear')
            "
            sortable
            class="!text-body-sm"
          >
            <template #body="{ data }">
              {{ data.acquisitionYear || '-' }}
            </template>
          </Column>

          <Column
            field="wheelCount"
            :header="t('allCars.carList.columns.wheels')"
            sortable
            class="!text-body-sm"
          >
            <template #body="{ data }">
              {{ data.wheelCount || '-' }}
            </template>
          </Column>

          <Column
            field="doorCount"
            :header="t('allCars.carList.columns.doors')"
            sortable
            class="!text-body-sm"
          >
            <template #body="{ data }">
              {{ data.doorCount || '-' }}
            </template>
          </Column>

          <Column
            :exportable="false"
            :header="t('allCars.carList.columns.actions')"
          >
            <template #body="slotProps">
              <div class="flex gap-2">
                <Button
                  icon="pi pi-eye"
                  rounded
                  variant="outlined"
                  severity="secondary"
                  size="small"
                  @click="onViewCar(slotProps.data)"
                  v-tooltip.top="
                    t('allCars.carList.actions.view')
                  "
                />
                <Button
                  icon="pi pi-pencil"
                  rounded
                  variant="outlined"
                  severity="info"
                  size="small"
                  @click="onEditCar(slotProps.data)"
                  v-tooltip.top="
                    t('allCars.carList.actions.edit')
                  "
                />

                <Button
                  icon="pi pi-trash"
                  rounded
                  variant="outlined"
                  severity="danger"
                  size="small"
                  @click="confirmDeleteItem(slotProps.data)"
                  v-tooltip.top="
                    t('allCars.carList.actions.delete')
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
                    v-if="multiSelect"
                    class="absolute left-4 top-4 z-10"
                  >
                    <Checkbox
                      v-model="selectedItems"
                      :value="item"
                    />
                  </div>

                  <div class="absolute right-4 top-4">
                    <Tag
                      :value="
                        t(
                          'carTypes.' +
                            item.carType?.toLowerCase()
                        )
                      "
                      severity="info"
                      class="!text-xs uppercase"
                    />
                  </div>

                  <div
                    class="mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-surface-100 text-primary dark:bg-surface-800"
                  >
                    <i class="pi pi-car text-2xl" />
                  </div>

                  <div class="mb-4 flex flex-col gap-2">
                    <!-- Title: Brand + Model -->
                    <span
                      class="truncate text-lg font-bold text-surface-900 dark:text-surface-0"
                      :title="item.brand + ' ' + item.model"
                    >
                      {{ item.brand }} {{ item.model }}
                    </span>

                    <!-- Serial Number -->
                    <span
                      class="font-mono text-sm text-surface-500 dark:text-surface-400"
                    >
                      {{ item.serialNumber }}
                    </span>

                    <!-- Owner Info (if admin) -->
                    <div
                      v-if="!isCustomer && item.ownerInfo"
                      class="mt-1 flex items-center gap-2 rounded-lg bg-surface-100 px-3 py-2 dark:bg-surface-700"
                    >
                      <i
                        class="pi pi-user text-sm text-primary"
                      />
                      <div
                        class="flex flex-col overflow-hidden"
                      >
                        <span
                          class="truncate text-sm font-semibold text-surface-800 dark:text-surface-100"
                        >
                          {{ item.ownerInfo.fullName }}
                        </span>
                        <span
                          class="truncate text-xs text-surface-500 dark:text-surface-400"
                        >
                          {{
                            t(
                              'allCars.carList.placeholders.taxNumber'
                            )
                          }}:
                          {{
                            item.ownerInfo.taxNumber || '-'
                          }}
                        </span>
                      </div>
                    </div>
                  </div>

                  <!-- Specs Grid -->
                  <div
                    class="mb-4 grid grid-cols-2 gap-x-4 gap-y-2 text-sm"
                  >
                    <div class="flex flex-col">
                      <span
                        class="text-xs text-surface-400 dark:text-surface-500"
                      >
                        {{ t('fuelTypes.title') }}
                      </span>
                      <span
                        class="font-medium text-blue-600 dark:text-blue-400"
                      >
                        {{
                          t(
                            'fuelTypes.' +
                              item.fuelType?.toLowerCase()
                          )
                        }}
                      </span>
                    </div>
                    <div class="flex flex-col">
                      <span
                        class="text-xs text-surface-400 dark:text-surface-500"
                      >
                        {{
                          t(
                            'allCars.carList.columns.productionDate'
                          )
                        }}
                      </span>
                      <span
                        class="font-medium text-surface-700 dark:text-surface-200"
                      >
                        {{
                          item.productionDate
                            ? new Date(
                                item.productionDate
                              ).toLocaleDateString()
                            : '-'
                        }}
                      </span>
                    </div>
                    <div class="flex flex-col">
                      <span
                        class="text-xs text-surface-400 dark:text-surface-500"
                      >
                        {{ t('newCarDialog.field.doors') }}
                      </span>
                      <span
                        class="font-medium text-surface-700 dark:text-surface-200"
                      >
                        {{ item.doorCount }}
                      </span>
                    </div>
                    <div class="flex flex-col">
                      <span
                        class="text-xs text-surface-400 dark:text-surface-500"
                      >
                        {{ t('newCarDialog.field.wheels') }}
                      </span>
                      <span
                        class="font-medium text-surface-700 dark:text-surface-200"
                      >
                        {{ item.wheelCount }}
                      </span>
                    </div>
                    <div class="flex flex-col">
                      <span
                        class="text-xs text-surface-400 dark:text-surface-500"
                      >
                        {{
                          t(
                            'allCars.carList.columns.acquisitionYear'
                          )
                        }}
                      </span>
                      <span
                        class="font-medium text-surface-700 dark:text-surface-200"
                      >
                        {{ item.acquisitionYear || '-' }}
                      </span>
                    </div>
                  </div>

                  <div
                    class="mt-auto flex items-center justify-end border-t pt-4"
                  >
                    <div class="flex gap-1">
                      <!-- Actions -->
                      <Button
                        icon="pi pi-eye"
                        text
                        rounded
                        severity="secondary"
                        size="small"
                        @click="onViewCar(item)"
                        v-tooltip.top="
                          t('allCars.carList.actions.view')
                        "
                      />
                      <Button
                        icon="pi pi-pencil"
                        text
                        rounded
                        severity="info"
                        size="small"
                        @click="onEditCar(item)"
                        v-tooltip.top="
                          t('allCars.carList.actions.edit')
                        "
                      />
                      <Button
                        icon="pi pi-trash"
                        text
                        rounded
                        severity="danger"
                        size="small"
                        @click="confirmDeleteItem(item)"
                        v-tooltip.top="
                          t(
                            'allCars.carList.actions.delete'
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

        <CarsImportDialog
          v-model:visible="importDialog"
          @success="refreshTable"
        />

        <NewCarDialog
          v-model:visible="createDialogVisible"
          :default-owner-id="computedOwnerId"
          @success="refreshTable"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
  import { ref, computed, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import { useI18n } from 'vue-i18n'
  import { useQuery } from '@tanstack/vue-query'
  import { onKeyStroke } from '@vueuse/core'


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
  import Menu from 'primevue/menu'
  import Checkbox from 'primevue/checkbox'
  import CarsImportDialog from '@/features/cars/components/CarsImportDialog.vue'
  import NewCarDialog from '@/features/cars/components/NewCarDialog.vue'

  import { useAppConfirm } from '@/composables/useAppConfirmDialog.js'
  import { useCarsList } from '@/features/cars/composables/useCarsList.js'

  const props = defineProps({
    presetOwnerId: {
      type: [Number, String],
      default: null,
    },
  })

  const { requireDelete } = useAppConfirm()
  const { t } = useI18n()
  const router = useRouter()

  const { data: user } = useQuery({
    queryKey: ['user', 'me'],
    staleTime: Infinity,
  })

  const isCustomer = computed(
    () => user.value?.role === 'CUSTOMER'
  )
  const currentUserId = computed(() => user.value?.id)
  const computedOwnerId = computed(
    () =>
      props.presetOwnerId ??
      (isCustomer.value ? currentUserId.value : null)
  )

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
  } = useCarsList(requireDelete, computedOwnerId, true)


  const importDialog = ref(false)
  const createDialogVisible = ref(false)
  const layout = ref('list')
  const layoutOptions = ref(['list', 'grid'])
  const multiSelect = ref(false)
  const menu = ref()

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

  const toggleMenu = (event) => {
    menu.value.toggle(event)
  }

  const toggleSelect = () => {
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

  const onViewCar = (car) => {
    router.push(`/cars/${car.id}`)
  }
  const onEditCar = (car) => {
    router.push(`/cars/${car.id}?edit=true`)
  }
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
      label: t('allCars.carList.toolbar.addCar'),
      icon: 'pi pi-plus',
      command: () => openNew(),
    },
    {
      label: t('allCars.carList.toolbar.import'),
      icon: 'pi pi-download',
      command: () => openImport(),
    },
    { separator: true },
    {
      label: t(
        'allCars.carList.dialogs.deleteSelectedTitle'
      ),
      icon: 'pi pi-trash',
      class: 'text-red-500',
      disabled:
        !selectedItems.value ||
        selectedItems.value.length === 0,
      command: () => confirmDeleteSelected(),
    },
  ])
</script>
