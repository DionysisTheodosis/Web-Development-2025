<script setup>
  import { computed, ref } from 'vue'
  import { Form } from '@primevue/forms'
  import InputText from 'primevue/inputtext'
  import Button from 'primevue/button'
  import Password from 'primevue/password'
  import Card from 'primevue/card'
  import { z } from 'zod'
  import { zodResolver } from '@primevue/forms/resolvers/zod'
  import { useI18n } from 'vue-i18n'
  import 'primeicons/primeicons.css'
  import { useSpinner } from '@/components/spinner/useSpinner.js'
  import Spinner from '@/components/spinner/Spinner.vue'
  import BaseFormField from '@/components/BaseFormField.vue'
  import LogoLinkButton from '@/components/LogoLinkButton.vue'
  import LocaleMenu from '@/components/locale/LocaleMenu.vue'
  import router from '@/router/index.js'
  import { useAuth } from '@/features/auth/useAuth.js'

  const { t } = useI18n({ useScope: 'global' })
  const { loading, showSpinner, hideSpinner } = useSpinner()

  const initialValues = ref({
    username: '',
    password: '',
  })

  const formResolver = computed(() =>
    zodResolver(
      z.object({
        username: z.string().nonempty({
          message: t('loginPage.usernameValidationError'),
        }),
        password: z.string().nonempty({
          message: t('loginPage.passwordValidationError'),
        }),
      })
    )
  )

  const invalidated = ref(false)
  const { login, isFetching } = useAuth()
  const onFormSubmit = async ({ valid, values }) => {
    if (!valid) return
    try {
      showSpinner()

      await login({
        username: values.username,
        password: values.password,
      })

      const queryRedirect =
        router.currentRoute.value.query.redirect
      const redirectPath =
        typeof queryRedirect === 'string' &&
        queryRedirect.length > 0
          ? queryRedirect
          : '/dashboard'

      await router.replace(redirectPath)
    } catch (error) {
      invalidated.value = true
      console.error('Login failed:', error)
    } finally {
      hideSpinner()
    }
  }
</script>

<template>
  <Card
    class="relative rounded-3xl bg-surface-100 pb-10 pt-10 shadow-lg md:p-20 dark:bg-surface-900"
  >
    <template #header>
      <LocaleMenu class="absolute right-4 top-4 z-10" />
    </template>

    <template #title>
      <div
        class="mb-0 flex items-center justify-center py-0"
      >
        <LogoLinkButton
          class="mb-0 h-40 w-auto py-0"
          color="var(--p-primary-color)"
        />
      </div>
      <h1 class="mb-2 text-center text-title-4 font-bold">
        {{ t('loginPage.title') }}
      </h1>
      <p class="mb-4 text-center text-subheading">
        {{ t('loginPage.subtitle') }}
      </p>
    </template>

    <template #content>
      <Form
        v-slot="$form"
        :initialValues="initialValues.value"
        :resolver="formResolver"
        @submit="onFormSubmit"
        class="mx-10 mb-3 flex flex-col space-y-4"
      >
        <BaseFormField
          name="username"
          icon="pi pi-user"
          :component="InputText"
          :inputProps="{
            type: 'text',
            autocomplete: 'username',
          }"
          :label="t('loginPage.username')"
          :invalid="invalidated"
        />

        <BaseFormField
          name="password"
          icon="pi pi-key"
          :component="Password"
          :inputProps="{
            toggleMask: true,
            feedback: false,
            autocomplete: 'current-password',
          }"
          :label="t('loginPage.password')"
          :invalid="invalidated"
        />

        <div v-if="loading" class="flex justify-center">
          <Spinner />
        </div>

        <Button
          v-else
          type="submit"
          severity="primary"
          icon="pi pi-sign-in"
          :label="t('loginPage.loginButton')"
          :disabled="
            !$form.valid ||
            !$form.username?.value ||
            !$form.password?.value
          "
          class="sm:mx-auto"
        />
      </Form>
    </template>

    <template #footer>
      <div
        class="flex items-center justify-center space-x-2"
      >
        <p>{{ t('loginPage.registerTitle') }}</p>
        <RouterLink
          to="/register"
          class="text-secondary font-medium hover:underline"
        >
          {{ t('loginPage.registerButton') }}
        </RouterLink>
      </div>
    </template>
  </Card>
</template>
