<template>
  <div class="flex flex-col h-full">
    <div class="hidden max-md:flex items-center justify-between px-3 pt-4">
      <svg width="35" height="40" viewBox="0 0 35 40" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path
          d="M25.87 18.05L23.16 17.45L25.27 20.46V29.78L32.49 23.76V13.53L29.18 14.73L25.87 18.04V18.05ZM25.27 35.49L29.18 31.58V27.67L25.27 30.98V35.49ZM20.16 17.14H20.03H20.17H20.16ZM30.1 5.19L34.89 4.81L33.08 12.33L24.1 15.67L30.08 5.2L30.1 5.19ZM5.72 14.74L2.41 13.54V23.77L9.63 29.79V20.47L11.74 17.46L9.03 18.06L5.72 14.75V14.74ZM9.63 30.98L5.72 27.67V31.58L9.63 35.49V30.98ZM4.8 5.2L10.78 15.67L1.81 12.33L0 4.81L4.79 5.19L4.8 5.2ZM24.37 21.05V34.59L22.56 37.29L20.46 39.4H14.44L12.34 37.29L10.53 34.59V21.05L12.42 18.23L17.45 26.8L22.48 18.23L24.37 21.05ZM22.85 0L22.57 0.69L17.45 13.08L12.33 0.69L12.05 0H22.85Z"
          fill="var(--p-primary-color)"
        />
        <path
          d="M30.69 4.21L24.37 4.81L22.57 0.69L22.86 0H26.48L30.69 4.21ZM23.75 5.67L22.66 3.08L18.05 14.24V17.14H19.7H20.03H20.16H20.2L24.1 15.7L30.11 5.19L23.75 5.67ZM4.21002 4.21L10.53 4.81L12.33 0.69L12.05 0H8.43002L4.22002 4.21H4.21002ZM21.9 17.4L20.6 18.2H14.3L13 17.4L12.4 18.2L12.42 18.23L17.45 26.8L22.48 18.23L22.5 18.2L21.9 17.4ZM4.79002 5.19L10.8 15.7L14.7 17.14H14.74H15.2H16.85V14.24L12.24 3.09L11.15 5.68L4.79002 5.2V5.19Z"
          fill="var(--p-text-color)"
        />
      </svg>
      <span class="font-semibold text-2xl text-primary">Your Logo</span>
      <Button
        icon="pi pi-times"
        @click="$emit('close-drawer')"
        rounded
        outlined
        class="p-2"
      />
    </div>

    <div class="flex-1 overflow-y-auto p-3">
      <PanelMenu :model="primeItems" :router="true" />
    </div>

    <div class="mt-auto p-3">
      <Divider />
      <div class="flex items-center gap-3 p-2">
        <Avatar
          :label="getInitial(userRole)"
          class="bg-primary text-surface-primary"
          shape="circle"
        />
        <span class="font-bold">{{ userName }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import PanelMenu from 'primevue/panelmenu'
import Button from 'primevue/button'
import Avatar from 'primevue/avatar'
import Divider from 'primevue/divider'
import 'primeicons/primeicons.css'

const route = useRoute()
// In a real app, userRole and userName would likely come from props or a store
const userRole = ref('secretary') // Use ref if it might change reactively
const userName = ref('John Doe')    // Use ref if it might change reactively

const getInitial = (name) => (name ? name[0].toUpperCase() : '?') // Handle potential empty name

// Define the structure of all possible menu items
const allItems = {
  common: [
    {
      label: 'Dashboard',
      icon: 'pi pi-home',
      to: '/'
    },
    {
      label: 'Messages',
      icon: 'pi pi-comments',
      to: '/messages'
    },
    {
      label: 'Appointments',
      icon: 'pi pi-calendar',
      to: '/appointments'
    },
    {
      label: 'Settings',
      icon: 'pi pi-cog',
      to: '/settings'
    }
  ],
  customer: [
    {
      label: 'Cars',
      icon: 'pi pi-car',
      to: '/cars'
    }
  ],
  secretary: [
    {
      label: 'Users',
      icon: 'pi pi-users',
      items: [
        {
          label: 'Mechanics',
          icon: 'pi pi-users', // Consider a more specific icon if available
          items: [
            { label: 'View', icon: 'pi pi-table', to: '/users/mechanics/view' },
            { label: 'Search', icon: 'pi pi-search', to: '/users/mechanics/search' }
          ]
        },
        {
          label: 'Customers',
          icon: 'pi pi-users', // Consider a more specific icon if available
          items: [
            { label: 'View', icon: 'pi pi-table', to: '/users/customers/view' },
            { label: 'Search', icon: 'pi pi-search', to: '/users/customers/search' }
          ]
        }
      ]
    }
    // Add other secretary-specific top-level items here if needed
  ]
  // Add other roles like 'mechanic', 'admin' here
}

// Computed property to generate the final menu items for PanelMenu
const primeItems = computed(() => {
  // Recursive function to transform raw items into PanelMenu format
  // and add the active class based on the current route
  const processMenuItems = (items) => {
    if (!items) return undefined; // Base case for recursion if items is null/undefined

    return items.map(item => {
      // Check if the current route exactly matches the item's 'to' property
      const isActive = item.to && route.path === item.to;

      // Recursively process sub-items if they exist
      const processedSubItems = item.items ? processMenuItems(item.items) : undefined;

      // Return the object structure expected by PanelMenu
      return {
        label: item.label,
        icon: item.icon,
        to: item.to,
        // Assign processed sub-items, will be undefined if none
        items: processedSubItems,
        // Apply the 'active-menu-item' class if this item is active
        class: isActive ? 'active-menu-item' : ''
        // Note: PrimeVue's PanelMenu typically handles expansion state automatically
        // based on the active route, especially if child routes are configured correctly
        // in your Vue Router setup.
      };
    });
  };

  // 1. Get dashboard item (if it exists in common)
  const dashboardItem = allItems.common.filter(i => i.label === 'Dashboard');

  // 2. Get role-specific items (using the current userRole)
  //    The || [] ensures it works even if the role doesn't exist in allItems
  const roleSpecificItems = allItems[userRole.value] || [];

  // 3. Get other common items (excluding Dashboard)
  const otherCommonItems = allItems.common.filter(i => i.label !== 'Dashboard');

  // 4. Combine the items in the desired order
  const combinedRawItems = [
    ...dashboardItem,
    ...roleSpecificItems,
    ...otherCommonItems
  ];

  // 5. Process the combined list recursively to get the final structure
  return processMenuItems(combinedRawItems);
});

</script>

<style>
/* Active state styling */
.panelmenu .panelmenu-header > a.active-menu-item, /* Top-level active header */
.panelmenu .menuitem > .p-menuitem-link.active-menu-item /* Sub-menu active link */
{
  /* Use PrimeVue CSS variables for consistency */
  background-color: var(--primary-color) !important;
  color: var(--primary-color-text) !important; /* Often white or a light color */
}

/* Style icons within active items */
.panelmenu .panelmenu-header > a.active-menu-item .p-menuitem-icon,
.panelmenu .menuitem > .menuitem-link.active-menu-item .p-menuitem-icon {
  color: var(--primary-color-text) !important; /* Match text color */
}

/* Optional: Style parent item when a child is active (more complex) */
/* You might need additional logic in `processMenuItems` to add a class like 'active-parent' */
/*
.p-panelmenu .p-panelmenu-header > a.active-parent {
  background-color: rgba(var(--primary-color-rgb), 0.1); // Example subtle background
}
*/

/* Custom transitions (already present and fine) */
.p-menuitem-link, .panelmenu-header > a {
  transition: background-color 0.2s ease, color 0.2s ease;
}

/* Ensure proper surface is used for text in non-active state if needed */
/* Default PrimeVue themes usually handle this, but uncomment/adjust if needed */
/*
.p-panelmenu .p-menuitem-link {
  color: var(--text-color);
}
.p-panelmenu .p-menuitem-icon {
  color: var(--text-color-secondary);
}
*/
</style>