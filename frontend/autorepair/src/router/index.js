// router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import i18nInstance from '@/plugins/i18n.js'
import { useHead } from '@vueuse/head'
import apiClient from '@/plugins/api-client.js'
import { queryClient } from '@/plugins/vue-query.js'
import CarProfileView from '@/views/CarProfileView.vue'

const HomeView = () => import('@/views/HomeView.vue')
const DashboardView = () =>
  import('@/views/DashboardView.vue')
const UsersView = () => import('@/views/UsersView.vue')
const UsersAllView = () =>
  import('@/views/UsersAllView.vue')
const UserProfileView = () =>
  import('@/views/UserProfileView.vue')

const SettingsView = () => import('@/views/SettingsVue.vue')
const MechanicsView = () =>
  import('@/views/MechanicsView.vue')
const CustomersView = () =>
  import('@/views/CustomersView.vue')
const NotFoundView = () =>
  import('@/views/NotFoundView.vue')
const LoginView = () =>
  import('@/features/auth/login/LoginView.vue')
const RegisterView = () =>
  import('@/features/auth/register/RegisterView.vue')
const CarsView = () => import('@/views/CarsView.vue')

const t = i18nInstance.global.t

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomeView,
    meta: {
      title: 'route.home',
      description: 'Welcome to Auto Repair',
      requiresUnAuth: true,
    },
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: DashboardView,
    meta: {
      requiresAuth: true,
      title: 'route.dashboard',
      layout: 'main',
    },
  },
  {
    path: '/account',
    name: 'Account',
    component: () =>
      import('@/features/account/AccountView.vue'),
    meta: {
      requiresAuth: true,
      title: 'route.account',
      ico: 'pi pi-user',
      layout: 'main',
    },
  },

  {
    path: '/settings',
    name: 'settings',
    component: SettingsView,
    meta: {
      requiresAuth: true,
      title: 'route.settings',
      ico: 'pi pi-cog',
      layout: 'main',
    },
  },
  {
    path: '/cars',
    name: 'CarsRoot',
    component: () => import('@/layouts/BlankLayout.vue'),
    redirect: { name: 'Cars' },
    meta: {
      requiresAuth: true,
      layout: 'main',
      title: 'cars.title',
      ico: 'pi pi-car',
    },
    children: [
      {
        path: '',
        name: 'Cars',
        component: CarsView,
        meta: {
          allowedRoles: ['SECRETARY', 'CUSTOMER'],
          ico: 'pi pi-car',
        },
      },
      {
        path: ':id',
        name: 'car-profile',
        component: CarProfileView,
        props: true,
        meta: {
          title: 'route.car.profile',
          allowedRoles: [
            'SECRETARY',
            'CUSTOMER',
            'MECHANIC',
          ],
          ico: 'pi pi-car',
        },
      },
    ],
  },
  ,
  {
    path: '/users',
    component: UsersView,
    redirect: '/users/all',
    meta: {
      title: 'route.users.title',
      requiresAuth: true,
      allowedRoles: ['SECRETARY'],
      layout: 'main',
    },
    children: [
      {
        path: 'all',
        name: 'users-all-view',
        component: UsersAllView,
        meta: {
          title: 'route.users.all',
          ico: 'pi pi-users',
          requiresAuth: true,
          allowedRoles: ['SECRETARY'],
        },
      },
      {
        path: 'mechanics',
        name: 'mechanics-view',
        component: MechanicsView,
        meta: {
          title: 'route.users.mechanics',
          ico: 'pi pi-users',
          requiresAuth: true,
          allowedRoles: ['SECRETARY'],
        },
      },
      {
        path: 'customers',
        name: 'customers-view',
        component: CustomersView,
        meta: {
          title: 'route.users.customers',
          ico: 'pi pi-users',
          requiresAuth: true,
          allowedRoles: ['SECRETARY'],
        },
      },
      {
        path: ':id',
        name: 'user-profile',
        component: UserProfileView,
        props: true,
        meta: {
          title: 'route.users.profile',
          ico: 'pi pi-user',
          requiresAuth: true,
          allowedRoles: ['SECRETARY'],
        },
      },
    ],
  },

  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { requiresUnAuth: true, layout: 'auth' },
  },

  {
    path: '/register',
    name: 'register',
    component: RegisterView,
    meta: { requiresUnAuth: true, layout: 'auth' },
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: NotFoundView,
    meta: { requiresAuth: false, layout: 'auth' },
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  async scrollBehavior(to, from, savedPosition) {
    if (savedPosition) return savedPosition
    if (to.hash)
      return { el: to.hash, top: 20, behavior: 'smooth' }
    return { top: 0 }
  },
})

router.beforeEach(async (to) => {
  let user = queryClient.getQueryData(['user', 'me'])

  if (!user) {
    try {
      user = await queryClient.fetchQuery({
        queryKey: ['user', 'me'],
        queryFn: async () => {
          const { data } = await apiClient.get('/auth/me', {
            withCredentials: true,
          })
          return data
        },
        staleTime: 1000 * 60 * 60,
      })
    } catch (err) {
      user = null
    }
  }

  const isAuthenticated = !!user
  const userRole = user?.role

  if (to.meta.requiresAuth && !isAuthenticated) {
    return {
      name: 'login',
      query: { redirect: to.fullPath },
    }
  }

  if (to.meta.requiresUnAuth && isAuthenticated) {
    return { name: 'Dashboard' }
  }
  const allowedRoles = to.matched.flatMap(
    (r) => r.meta.allowedRoles || []
  )
  if (
    allowedRoles.length &&
    (!isAuthenticated || !allowedRoles.includes(userRole))
  ) {
    return { name: 'Dashboard' }
  }

  return true
})

router.afterEach((to) => {
  const defaultTitle = 'Auto Repair'
  const titleKey = to.meta?.title
  const translatedTitle = titleKey
    ? t(titleKey)
    : defaultTitle
  const defaultDescription =
    'Πλατφόρμα διαχείρισης πελατών, μηχανικών, αυτοκινήτων και ραντεβού.'

  useHead({
    title: translatedTitle,
    meta: [
      {
        name: 'description',
        content: to.meta.description || defaultDescription,
      },
      { property: 'og:title', content: translatedTitle },
      {
        property: 'og:description',
        content: to.meta.description || defaultDescription,
      },
      {
        property: 'og:image',
        content: 'https://i.imgur.com/qUDeFVO.jpeg',
      },
      {
        property: 'og:url',
        content: window.location.origin + to.fullPath,
      },
      { property: 'og:type', content: 'website' },
      { name: 'twitter:title', content: translatedTitle },
      {
        name: 'twitter:description',
        content: to.meta.description || defaultDescription,
      },
      {
        name: 'twitter:image',
        content: 'https://i.imgur.com/qUDeFVO.jpeg',
      },
      {
        name: 'twitter:card',
        content: 'summary_large_image',
      },
    ],
  })
})

export default router
