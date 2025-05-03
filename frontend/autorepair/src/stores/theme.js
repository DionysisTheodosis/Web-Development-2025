import { ref, watch } from 'vue'
import { defineStore } from 'pinia'

export const useDarkModeStore = defineStore('darkMode', () => {
  const darkMode = ref(loadInitialMode())

  // Function to load the initial mode (from localStorage if available)
  function loadInitialMode() {
    const savedMode = localStorage.getItem('darkMode')
    return savedMode || getSystemMode()  // Default to 'system' if nothing saved
  }

  // Update the document's dark class based on the current dark mode
  function updateDarkClass() {
    const isDark = darkMode.value === 'my-app-dark'
    document.documentElement.classList.toggle('my-app-dark', isDark)
  }

  // Setter to directly set the mode
  function setDarkMode(mode) {
    if (mode === 'system') {
      darkMode.value = getSystemMode()  // Detect system preference if set to 'system'
    } else if (mode === 'light' || mode === 'my-app-dark') {
      darkMode.value = mode  // Set to either light or dark manually
    }
    updateDarkClass()
  }

  // Get system's preferred dark or light mode
  function getSystemMode() {
    const darkModeQuery = window.matchMedia('(prefers-color-scheme: dark)')
    return darkModeQuery.matches ? 'my-app-dark' : 'light'
  }

  // Watch for changes in darkMode and update localStorage accordingly
  watch(darkMode, (newVal) => {
    localStorage.setItem('darkMode', newVal)
    updateDarkClass()  // Apply class change immediately
  })

  // Initialize the dark class
  updateDarkClass()

  return {
    darkMode,
    setDarkMode  // Expose setter method to components
  }
})
