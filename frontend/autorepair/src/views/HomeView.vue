<script setup>
import InputText from 'primevue/inputtext'
import Button from 'primevue/button'
import { ref } from 'vue'

const text = ref('')
</script>

<template>
  <main class="p-6 space-y-4">
    <h1 class="text-3xl font-bold text-primary">Welcome to the Dashboard</h1>

    <div class="space-y-2">
      <label for="example-input" class="block font-medium">Enter something:</label>
      <InputText id="example-input" v-model="text" class="w-full max-w-sm" placeholder="Type something..." />
      <Button label="Save" severity="primary" />
    </div>

    <div class="mt-4 text-lg">
      <strong>Live Preview:</strong> {{ text }}
    </div>
  </main>
</template>