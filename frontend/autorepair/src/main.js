import '@/output.css'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import PrimeVue from 'primevue/config'
import App from './App.vue'
import router from './router'

// PrimeVue Theme
import Aura from '@primevue/themes/aura'
import { Ripple, StyleClass } from 'primevue'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia) // ✅ Install Pinia before using any store
app.use(router)

// ✅ Now it's safe to use the store
import { useDarkModeStore } from '@/stores/theme.js'
const darkModeStore = useDarkModeStore()

// PrimeVue Configuration
app.use(PrimeVue, {
  unstyled: false,
  inputVariant: 'filled',
  theme: {
    preset: Aura,
    options: {
      prefix: 'p',
      darkModeSelector: getDarkMode(),
      cssLayer: false
    }
  },
  zIndex: {
    modal: 1100,
    overlay: 1000,
    menu: 1000,
    tooltip: 1100
  }
})

// Register directives
app.directive('ripple', Ripple)
app.directive('styleclass', StyleClass)

// Mount the app
app.mount('#app')

function getDarkMode() {
  if(darkModeStore.darkMode === 'my-app-dark') {
    return '.my-app-dark'
  }
  else if(darkModeStore.darkMode === 'light') {
    return 'none'
  }
  else {
    return 'system'
  }
}
