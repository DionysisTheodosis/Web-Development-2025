import { ref, watch } from 'vue';
import { defineStore } from 'pinia';


const PREFERENCE_STORAGE_KEY = 'theme-preference';
const ACTIVE_MODE_STORAGE_KEY = 'darkMode';

export const useDarkModeStore = defineStore('darkMode', () => {


  const initialPreference = localStorage.getItem(PREFERENCE_STORAGE_KEY) || 'system';
  const themePreference = ref(initialPreference);


  const darkMode = ref(loadInitialMode(themePreference.value));

  let darkModeQuery = null;


  function getSystemMode() {
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    return prefersDark ? 'my-app-dark' : 'light';
  }

  function loadInitialMode(preference) {
    if (preference === 'system') {

      return getSystemMode();
    }

    return preference;
  }

  function updateDarkClass() {
    const isDark = darkMode.value === 'my-app-dark';
    document.documentElement.classList.toggle('my-app-dark', isDark);
  }

  // --- Actions ---

  function setDarkMode(mode) {
    themePreference.value = mode;
    localStorage.setItem(PREFERENCE_STORAGE_KEY, mode);

    // 2. Determine and Set Active Mode
    if (mode === 'system') {
      watchSystemPreference();
      darkMode.value = getSystemMode();
    } else {
      stopWatchingSystemPreference();
      darkMode.value = mode;
    }

    updateDarkClass();
  }



  function watchSystemPreference() {
    stopWatchingSystemPreference();
    darkModeQuery = window.matchMedia('(prefers-color-scheme: dark)');
    darkModeQuery.addEventListener('change', handleSystemPreferenceChange);
  }

  function stopWatchingSystemPreference() {
    if (darkModeQuery) {
      darkModeQuery.removeEventListener('change', handleSystemPreferenceChange);
      darkModeQuery = null;
    }
  }

  function handleSystemPreferenceChange(event) {
    if (themePreference.value === 'system') {
      darkMode.value = event.matches ? 'my-app-dark' : 'light';
      updateDarkClass();
    }
  }


  watch(darkMode, (newVal) => {
    if(typeof newVal !== 'string'){
      console.error('Invalid darkMode value:', newVal);
      return;
    }
    if (themePreference.value !== 'system') {
      localStorage.setItem(ACTIVE_MODE_STORAGE_KEY, newVal);
    } else {
      localStorage.removeItem(ACTIVE_MODE_STORAGE_KEY);
    }
  });



  if (themePreference.value === 'system') {
    watchSystemPreference();
  }
  updateDarkClass();

  return {
    darkMode,
    setDarkMode,
    themePreference,
  };
});