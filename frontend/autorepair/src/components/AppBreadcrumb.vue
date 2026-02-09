<script setup>
  import { computed } from 'vue'
  import Breadcrumb from 'primevue/breadcrumb'
  import { useI18n } from 'vue-i18n'
  import { useRoute } from 'vue-router'
  const route = useRoute()
  const { t } = useI18n()
  const home = {
    icon: 'pi pi-home', route: { name: 'Dashboard' },
  }

  const breadcrumbItems = computed(() => {
    return route.matched
      .filter(record => record.meta?.title && record.name !== 'Dashboard')
      .map(record => {
        const paramKeys = (record.path.match(/:(\w+)/g) || []).map(k => k.slice(1))
        const params = paramKeys.reduce((acc, key) => {
          if (route.params[key]) acc[key] = route.params[key]
          return acc
        }, {})

        return {
          label: t(record.meta.title),
          icon: record.meta.ico,
          route: record?.name ? { name: record.name, params } : undefined
        }
      })
  })

</script>

<template>
  <Breadcrumb :pt:root:class="'dark:bg-transparent'" :home="home" :model="breadcrumbItems">
    <template #item="{ item, props }">
      <router-link v-if="item.route" v-slot="{ href, navigate }" :to="item.route" custom>
        <a :href="href" v-bind="props.action" @click="navigate">
          <span :class="[item.icon, 'text-color']" />
          <span class="text-primary font-semibold">{{ item.label }}</span>
        </a>
      </router-link>
      <a v-else :href="item.url" :target="item.target" v-bind="props.action">
        <span class="text-surface-700 dark:text-surface-0">{{ item.label }}</span>
      </a>
    </template>
  </Breadcrumb>
</template>

<style scoped></style>
