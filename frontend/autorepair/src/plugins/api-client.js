import axios from 'axios'
import { useToastBus } from '@/composables/useToastBus.js'
import i18nInstance from './i18n.js'
import { queryClient } from '@/plugins/vue-query.js'
import router from '@/router/index.js'

const t = i18nInstance.global.t.bind(i18nInstance.global)

const getTranslatedDetail = (msg, scope) => {
  if (!msg || typeof msg !== 'string')
    return scope.global.t('unknown_api_message')
  return scope.global.te(msg) ? scope.global.t(msg) : msg
}

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1/',
  withCredentials: true,
})

let lastErrorKey = null
const ignoreUrls = ['/auth/me']

apiClient.interceptors.response.use(
  (response) => {
    const method = response.config.method?.toUpperCase()
    const skipToast = response.config.skipToast === true

    if (
      response.config.url?.includes('/api/v1/auth/me') ||
      response.config.url?.includes('/api/v1/auth/login')
    ) {
      queryClient.setQueryData(
        ['user', 'me'],
        response.data
      )
    }

    if (
      ['POST', 'PUT', 'PATCH', 'DELETE'].includes(method) &&
      !skipToast
    ) {
      const apiMessage =
        response.data?.message || response.data || 'success'
      const translatedDetail = getTranslatedDetail(
        apiMessage,
        i18nInstance
      )

      useToastBus.messages.push({
        severity: 'success',
        summary: t('success'),
        detail: translatedDetail,
        life: 3000,
      })
    }
    return response
  },
  async (error) => {
    const status = error.response?.status
    const url = error.config?.url
    const currentRoute = router.currentRoute.value
    const skipErrorToast =
      error.config?.skipErrorToast === true
    if (
      status === 401 &&
      !url?.includes('/api/v1/auth/logout') &&
      !url?.includes('/api/v1/auth/me') &&
      !url?.includes('/api/v1/auth/login')
    ) {
      queryClient.setQueryData(['user', 'me'], null)
      if (
        currentRoute.name !== 'login' &&
        !currentRoute.meta.requiresUnAuth
      ) {
        router.replace({
          name: 'login',
          query: { redirect: currentRoute.fullPath },
        })
      }
    }

    if (status === 403) {
      router.back()
    }

    if (!ignoreUrls.includes(url) && !skipErrorToast) {
      const key = `${status}-${url}`

      if (key !== lastErrorKey) {
        lastErrorKey = key

        setTimeout(() => {
          lastErrorKey = null
        }, 2000)

        const rawMessage =
          error.response?.data?.message ||
          error.response?.data?.error ||
          error.message ||
          'defaultError'
        const translatedDetail = getTranslatedDetail(
          rawMessage,
          i18nInstance
        )

        useToastBus.messages.push({
          severity:
            status >= 500 || status === 401
              ? 'error'
              : 'warn',
          summary: `${t('error')} ${status || ''}`,
          detail: translatedDetail,
          life: 4000,
        })
      }
    }

    return Promise.reject(error)
  }
)

export default apiClient
