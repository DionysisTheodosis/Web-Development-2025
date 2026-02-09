import PrimeVue from 'primevue/config'
import Aura from '@primevue/themes/aura'
import {
  ConfirmationService,
  Ripple,
  StyleClass,
} from 'primevue'
import AnimateOnScroll from 'primevue/animateonscroll'
import FocusTrap from 'primevue/focustrap'
import Tooltip from 'primevue/tooltip';
export default (app) => {
  app.use(PrimeVue, {
    unstyled: false,
    ripple: true,
    inputVariant: 'filled',
    theme: {
      preset: Aura,
      options: {
        prefix: 'p',
        darkModeSelector: ".my-app-dark",
        cssLayer: {
          name: 'primevue',
          order: 'tailwind-base, primevue, tailwind-utilities'
        }
      }
    }
  })
  app.use(ConfirmationService);
  app.directive('ripple', Ripple)
  app.directive('styleclass', StyleClass)
  app.directive('animateonscroll', AnimateOnScroll)
  app.directive('focustrap', FocusTrap)
  app.directive('tooltip', Tooltip);
}


