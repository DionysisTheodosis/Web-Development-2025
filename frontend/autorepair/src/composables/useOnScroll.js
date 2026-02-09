import { onMounted, onScopeDispose } from 'vue'

export default function useOnScrollCallback(callback) {
  if (typeof callback !== 'function') {
    return
  }
  const handleScroll = (event) => {
    callback(event)
  }

  onMounted(() => {
    window.addEventListener('scroll', handleScroll, {
      passive: true,
    })
  })

  onScopeDispose(() => {
    window.removeEventListener('scroll', handleScroll)
  })
}
