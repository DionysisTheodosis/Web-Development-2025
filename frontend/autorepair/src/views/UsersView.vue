<script setup>
import InputText from 'primevue/inputtext';
import Button from 'primevue/button';
import { ref } from 'vue';
const text = ref('');
</script>

<template>
  <main>
    <div>
      <InputText v-model="text"/>
      <Button label="Save" severity="primary"/>
      <br>
      {{text}}

    </div>
  </main>
</template>