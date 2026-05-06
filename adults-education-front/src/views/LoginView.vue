<script setup lang="ts">
import { ref, reactive } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Icon } from '@iconify/vue'
import axios from 'axios'
import DefaultLayout from '@/components/layout/DefaultLayout.vue'
import AppInput from '@/components/common/AppInput.vue'
import AppButton from '@/components/common/AppButton.vue'
import { useAuthStore } from '@/stores/auth'
import type { ApiError } from '@/types/api'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const form = reactive({
  email: '',
  password: '',
})

const errors = reactive({
  email: '',
  password: '',
})

const apiError = ref('')
const loading = ref(false)

function validate(): boolean {
  let valid = true
  errors.email = ''
  errors.password = ''

  if (!form.email.trim()) {
    errors.email = t('validation.required')
    valid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errors.email = t('validation.email')
    valid = false
  }

  if (!form.password) {
    errors.password = t('validation.required')
    valid = false
  } else if (form.password.length < 6) {
    errors.password = t('validation.passwordMin')
    valid = false
  }

  return valid
}

async function onSubmit() {
  apiError.value = ''
  if (!validate()) return

  loading.value = true
  try {
    await auth.login({ email: form.email, password: form.password })
    const redirect = (route.query.redirect as string) || '/'
    router.push(redirect)
  } catch (err) {
    if (axios.isAxiosError(err) && err.response?.data) {
      apiError.value = (err.response.data as ApiError).message || t('errors.generic')
    } else {
      apiError.value = t('errors.network')
    }
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <DefaultLayout>
    <div class="flex min-h-[80vh] items-center justify-center px-4 py-12">
      <!-- Decorative bg -->
      <div class="fixed inset-0 -z-10 bg-gradient-to-br from-amber-50 via-white to-violet-50"></div>

      <div class="w-full max-w-[450px]">
        <!-- Logo -->
        <div class="mb-8 text-center">
          <RouterLink to="/" class="inline-flex items-center gap-2 text-2xl font-bold text-slate-900">
            <span class="text-3xl">🎓</span>
            AL Academy
          </RouterLink>
        </div>

        <!-- Card -->
        <div class="rounded-2xl border border-slate-100 bg-white p-8 shadow-xl">
          <h1 class="text-center text-2xl font-bold tracking-tight text-slate-900">
            {{ t('auth.loginTitle') }}
          </h1>
          <p class="mt-2 text-center text-sm text-slate-500">
            {{ t('auth.loginSubtitle') }}
          </p>

          <!-- API error -->
          <div
            v-if="apiError"
            class="mt-6 flex items-start gap-2 rounded-xl border border-rose-200 bg-rose-50 px-4 py-3"
          >
            <Icon icon="mdi:alert-circle-outline" class="mt-0.5 h-5 w-5 shrink-0 text-rose-500" />
            <p class="text-sm text-rose-700">{{ apiError }}</p>
          </div>

          <form class="mt-6 space-y-5" @submit.prevent="onSubmit">
            <AppInput
              v-model="form.email"
              :label="t('form.email')"
              type="email"
              :placeholder="t('auth.emailPlaceholder')"
              icon="mdi:email-outline"
              :error="errors.email"
              required
            />

            <AppInput
              v-model="form.password"
              :label="t('form.password')"
              type="password"
              :placeholder="t('auth.passwordPlaceholder')"
              icon="mdi:lock-outline"
              :error="errors.password"
              required
            />

            <AppButton type="submit" variant="primary" size="lg" :loading="loading" full-width>
              {{ t('auth.loginButton') }}
            </AppButton>
          </form>

          <p class="mt-6 text-center text-sm text-slate-500">
            {{ t('auth.noAccount') }}
            <RouterLink to="/register" class="font-medium text-amber-600 transition-colors hover:text-amber-700">
              {{ t('auth.signUpLink') }}
            </RouterLink>
          </p>
        </div>
      </div>
    </div>
  </DefaultLayout>
</template>
