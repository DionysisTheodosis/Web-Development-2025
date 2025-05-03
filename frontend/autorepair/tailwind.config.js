/** @type {import('tailwindcss').Config} */
import PrimeUI from 'tailwindcss-primeui';
export default {
  content: [
    "./src/**/*.{vue,js,ts,jsx,tsx}",  // Added vue extension
    "./index.html",
    "./src/**/*.html",
    "./node_modules/primevue/**/*.{js,ts,vue}",
  ],
  plugins: [PrimeUI],
}