import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import '@/assets/main.css'
import setupPrimeVue from './plugins/primevue'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import { useDarkModeStore } from '@/stores/theme.js'
import i18n from '@/plugins/i18n'
import ToastService from 'primevue/toastservice'
import VueQueryPlugin from '@/plugins/vue-query.js'
import { createHead } from '@vueuse/head'

const app = createApp(App)


const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)


app.use(VueQueryPlugin)


app.use(router)
app.use(i18n)


setupPrimeVue(app)
app.use(ToastService)


const head = createHead()
app.use(head)

router.isReady().then(() => {
  app.mount('#app');
});

useDarkModeStore()
