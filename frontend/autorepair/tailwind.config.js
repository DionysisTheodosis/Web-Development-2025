/** @type {import('tailwindcss').Config} */
import PrimeUI from 'tailwindcss-primeui';
export default {
  content: [
    "./src/**/*.{vue,js}",
    "./index.html",
    "./src/**/*.html",
    "./node_modules/primevue/**/*.{js,vue}",
  ],
  safelist: [
    'noscript-warning',
    'h-screen',
    'w-screen',
    'flex',
    'flex-col',
    'bg-red-900',
    'justify-center',
    'items-center',
    'text-red-300',
    'text-red-200',
    'text-title-1',
    'text-title-6',
    'text-center',
    "md:hidden",
    "md:flex",
    "md:justify-center",
    "shrink-0",
    "shrink-1",
    "justify-center",
    "items-center",
  ],
  darkMode: ['selector', '[class~="my-app-dark"]'],
  plugins: [PrimeUI],
  theme: {
    extend: {
      fontSize: {
        'title-1': ['clamp(3rem, 2.5385rem + 2.0513vw, 4rem)', { lineHeight: '1.1' }],
        'title-2': ['clamp(2.4rem, 2.1231rem + 1.2308vw, 3rem)', { lineHeight: '1.1' }],
        'title-3': ['clamp(2.1rem, 1.9154rem + 0.8205vw, 2.5rem)', { lineHeight: '1.15' }],
        'title-4': ['clamp(1.8rem, 1.7077rem + 0.4103vw, 2rem)', { lineHeight: '1.15' }],
        'title-5': ['clamp(1.6rem, 1.5308rem + 0.3077vw, 1.75rem)', { lineHeight: '1.2' }],
        'title-6': ['clamp(1.4rem, 1.3538rem + 0.2051vw, 1.5rem)', { lineHeight: '1.2' }],
        'title-7': ['clamp(1.2rem, 1.1769rem + 0.1026vw, 1.25rem)', { lineHeight: '1.25' }],
        'body-big': ['1.125rem', { lineHeight: '1.5' }],
        'body': ['1rem', { lineHeight: '1.5' }],
        'body-sm': ['0.875rem', { lineHeight: '1.5' }],
        'subheading': ['1.2rem', { lineHeight: '1.4' }],
      },
    },
  }
}