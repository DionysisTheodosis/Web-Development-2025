import { VueQueryPlugin, QueryClient } from '@tanstack/vue-query'

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 60, // 1 hour
      gcTime: 1000 * 60 * 60 * 5, // 5 hours
      retry: false,
      refetchOnWindowFocus: false,
    },
  },
})

export default (app) => {
  app.use(VueQueryPlugin, { queryClient })
}
