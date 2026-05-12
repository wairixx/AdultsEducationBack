<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Icon } from '@iconify/vue'
import axios from 'axios'
import DefaultLayout from '@/components/layout/DefaultLayout.vue'
import AppInput from '@/components/common/AppInput.vue'
import AppButton from '@/components/common/AppButton.vue'
import { useAuthStore } from '@/stores/auth'
import type { ApiError, Role } from '@/types/api'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const form = reactive({
  email: '',
  password: '',
  firstName: '',
  lastName: '',
  phone: '',
  birthDate: '',
  role: 'STUDENT' as Role,
})

const errors = reactive({
  email: '',
  password: '',
  firstName: '',
  lastName: '',
  birthDate: '',
  phone: '',
})

const apiError = ref('')
const loading = ref(false)

// Pre-select role from query param
onMounted(() => {
  const roleParam = route.query.role as string
  if (roleParam?.toLowerCase() === 'teacher') {
    form.role = 'TEACHER'
  }
})

function validate(): boolean {
  let valid = true
  errors.email = ''
  errors.password = ''
  errors.firstName = ''
  errors.lastName = ''
  errors.birthDate = ''
  errors.phone = ''

  if (form.phone && !/^\+?[0-9]{10,15}$/.test(form.phone.replace(/[\s-()]/g, ''))) {
    errors.phone = t('validation.phone')
    valid = false
  }

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
  } else if (form.password.length > 100) {
    errors.password = t('validation.passwordMax')
    valid = false
  }

  if (!form.firstName.trim()) {
    errors.firstName = t('validation.required')
    valid = false
  } else if (form.firstName.length > 50) {
    errors.firstName = t('validation.maxLength', { max: 50 })
    valid = false
  }

  if (!form.lastName.trim()) {
    errors.lastName = t('validation.required')
    valid = false
  } else if (form.lastName.length > 50) {
    errors.lastName = t('validation.maxLength', { max: 50 })
    valid = false
  }

  if (!form.birthDate) {
    errors.birthDate = t('validation.required')
    valid = false
  } else {
    const birthDate = new Date(`${form.birthDate}T00:00:00`)
    const today = new Date()
    if (birthDate > today) {
      errors.birthDate = t('validation.birthDateFuture')
      valid = false
    } else {
      let age = today.getFullYear() - birthDate.getFullYear()
      const monthDiff = today.getMonth() - birthDate.getMonth()
      const dayDiff = today.getDate() - birthDate.getDate()
      if (monthDiff < 0 || (monthDiff === 0 && dayDiff < 0)) age -= 1
      if (age < 18) {
        errors.birthDate = t('validation.birthDateAdult')
        valid = false
      }
    }
  }

  return valid
}

async function onSubmit() {
  apiError.value = ''
  if (!validate()) return

  loading.value = true
  try {
    await auth.register({
      email: form.email,
      password: form.password,
      firstName: form.firstName,
      lastName: form.lastName,
      phone: form.phone || undefined,
      birthDate: form.birthDate,
      role: form.role,
    })
    router.push('/')
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
      <div class="fixed inset-0 -z-10 bg-gradient-to-br from-violet-50 via-white to-amber-50"></div>

      <div class="w-full max-w-[500px]">
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
            {{ t('auth.registerTitle') }}
          </h1>
          <p class="mt-2 text-center text-sm text-slate-500">
            {{ t('auth.registerSubtitle') }}
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
            <!-- Role toggle -->
            <div>
              <label class="mb-1.5 block text-sm font-medium text-slate-700">
                {{ t('auth.roleLabel') }}
              </label>
              <div class="flex rounded-lg border border-slate-200 p-1">
                <button
                  type="button"
                  class="flex-1 rounded-md px-3 py-2 text-sm font-medium transition-all duration-200"
                  :class="
                    form.role === 'STUDENT'
                      ? 'bg-amber-500 text-white shadow-sm'
                      : 'text-slate-600 hover:text-slate-900'
                  "
                  @click="form.role = 'STUDENT'"
                >
                  <Icon icon="mdi:school-outline" class="mr-1 inline h-4 w-4" />
                  {{ t('roles.STUDENT') }}
                </button>
                <button
                  type="button"
                  class="flex-1 rounded-md px-3 py-2 text-sm font-medium transition-all duration-200"
                  :class="
                    form.role === 'TEACHER'
                      ? 'bg-amber-500 text-white shadow-sm'
                      : 'text-slate-600 hover:text-slate-900'
                  "
                  @click="form.role = 'TEACHER'"
                >
                  <Icon icon="mdi:human-male-board" class="mr-1 inline h-4 w-4" />
                  {{ t('roles.TEACHER') }}
                </button>
              </div>
            </div>

            <!-- Name row -->
            <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <AppInput
                v-model="form.firstName"
                :label="t('form.firstName')"
                icon="mdi:account-outline"
                :error="errors.firstName"
                required
              />
              <AppInput
                v-model="form.lastName"
                :label="t('form.lastName')"
                icon="mdi:account-outline"
                :error="errors.lastName"
                required
              />
            </div>

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
              :hint="t('validation.passwordHint')"
              required
            />

            <AppInput
              v-model="form.phone"
              :label="t('form.phone')"
              type="tel"
              placeholder="+380..."
              icon="mdi:phone-outline"
              :error="errors.phone"
            />

            <AppInput
              v-model="form.birthDate"
              :label="t('form.birthDate')"
              type="date"
              icon="mdi:calendar-outline"
              :error="errors.birthDate"
              required
            />

            <!-- Avatar hint -->
            <div class="flex items-center gap-2 rounded-lg bg-slate-50 px-3 py-2.5 text-xs text-slate-500">
              <Icon icon="mdi:information-outline" class="h-4 w-4 shrink-0 text-slate-400" />
              {{ t('auth.avatarHint') }}
            </div>

            <AppButton type="submit" variant="primary" size="lg" :loading="loading" full-width>
              {{ t('auth.registerButton') }}
            </AppButton>
          </form>

          <p class="mt-6 text-center text-sm text-slate-500">
            {{ t('auth.hasAccount') }}
            <RouterLink to="/login" class="font-medium text-amber-600 transition-colors hover:text-amber-700">
              {{ t('auth.loginLink') }}
            </RouterLink>
          </p>
        </div>
      </div>
    </div>
  </DefaultLayout>
</template>
