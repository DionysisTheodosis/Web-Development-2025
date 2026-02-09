import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAuth } from '@/features/auth/useAuth.js'

export default function useSideBarLinks() {
  const { t } = useI18n()

  const { data: user } = useAuth()
  const userRole = computed(
    () => user.value?.role?.toLowerCase() || null
  )

  const sideBarItems = computed(() => {
    const allItems = {
      common: [
        {
          label: t('dashboard'),
          icon: 'pi pi-home',
          to: '/dashboard',
        },
      ],

      customer: [
        {
          label: t('cars.title'),
          icon: 'pi pi-car',
          to: '/cars',
        },
      ],

      mechanic: [],

      secretary: [
        {
          label: t('users'),
          icon: 'pi pi-users',
          items: [
            {
              label: t('route.users.all'),
              icon: 'pi pi-list',
              to: '/users/all',
            },
            {
              label: t('mechanics'),
              icon: 'pi pi-wrench',
              to: '/users/mechanics',
            },
            {
              label: t('customers'),
              icon: 'pi pi-id-card',
              to: '/users/customers',
            },
          ],
        },
        {
          label: t('cars.title'),
          icon: 'pi pi-car',
          to: '/cars',
        },
      ],
    }

    const processMenuItems = (items) =>
      items?.map((item) => ({
        ...item,
        items: item.items
          ? processMenuItems(item.items)
          : undefined,
      }))

    const dashboardItem = allItems.common.filter(
      (i) => i.label === t('dashboard')
    )
    const roleSpecificItems = allItems[userRole.value] || []
    const otherCommonItems = allItems.common.filter(
      (i) => i.label !== t('dashboard')
    )

    return processMenuItems([
      ...dashboardItem,
      ...roleSpecificItems,
      ...otherCommonItems,
    ])
  })

  return { sideBarItems }
}
