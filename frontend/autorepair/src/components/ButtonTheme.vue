<script setup>
import Button from "primevue/button";
import { computed } from "vue";
import { useDarkModeStore } from "@/stores/theme.js";
const darkModeStore = useDarkModeStore();
const isDark = computed(() => darkModeStore.darkMode === "my-app-dark");
function toggleDarkMode() {
	darkModeStore.setDarkMode(isDark.value ? "light" : "my-app-dark");
}
</script>
<template>
	<Button
		:icon="isDark ? 'pi pi-sun' : 'pi pi-moon'"
		aria-label="Toggle Dark Mode"
		rounded
		text
		@click="toggleDarkMode"
	/>
</template>
