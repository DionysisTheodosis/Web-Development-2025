import { ref, onMounted,onScopeDispose } from 'vue'

export function useHandleSectionActive() {
  const elementAtPosition = ref(null)

  const handleScroll = () => {
    const scrollTop = window.scrollY || document.documentElement.scrollTop
    const targetPosition = scrollTop + 400
    let foundSectionId = null
    const sectionIds = ['hero', 'about', 'services', 'whyUs']
    sectionIds.forEach((id) => {
      const section = document.getElementById(id)
      if (section) {
        const rect = section.getBoundingClientRect()
        const sectionTop = rect.top + scrollTop
        if (sectionTop <= targetPosition && sectionTop + rect.height > targetPosition) {
          foundSectionId = id
        }
      }
    })
    elementAtPosition.value = foundSectionId
  }

  onMounted(() => {
    handleScroll()
    window.addEventListener('scroll', handleScroll)
  })

  onScopeDispose(() => {
    window.removeEventListener('scroll', handleScroll)
  })
  return {
    elementAtPosition,
  }
}
