<script setup>
  import { useI18n } from 'vue-i18n'
  import { computed, ref, watch } from 'vue'
  import Button from 'primevue/button'
  import Menu from 'primevue/menu'
  import { setLocale } from '@/plugins/i18n.js'
  const { t, locale, availableLocales } = useI18n()
  const menu = ref()
  const isTwoLocales = computed(() => availableLocales.length <= 2)
  const nextLocale = computed(() => {
    if (availableLocales.length > 1) {
      return locale.value === availableLocales[0] ? availableLocales[1] : availableLocales[0]
    } else {
      return availableLocales[0]
    }
  })

  const toggle = () => {
    const targetLocale = nextLocale.value
    setLocale(targetLocale)
  }

  watch(locale, (newLocale) => {
    document.documentElement.lang = newLocale
  }, { immediate: true })
</script>

<template>
  <div>
    <Button
      v-if="isTwoLocales"
      @click="toggle"
      :label="t(`locale.${nextLocale}.short`)"
      variant="text"
      rounded
      severity="secondary"
      aria-label="Locale Toggle"
      :pt="{
        root: {
          class: 'text-surface-0 hover:text-primary  ',
        }
      }"

    />
    <Menu v-else ref="menu" id="overlay_menu" :model="menuItems" :popup="true" />
  </div>
</template>