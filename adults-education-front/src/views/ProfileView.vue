<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { Icon } from '@iconify/vue'
import { toast } from 'vue-sonner'
import axios from 'axios'
import DefaultLayout from '@/components/layout/DefaultLayout.vue'
import AppInput from '@/components/common/AppInput.vue'
import AppButton from '@/components/common/AppButton.vue'
import AppCard from '@/components/common/AppCard.vue'
import AvatarUpload from '@/components/common/AvatarUpload.vue'
import { useAuthStore } from '@/stores/auth'
import { updateMyProfile, changePassword } from '@/api/me'
import type { ApiError } from '@/types/api'

const { t } = useI18n()
const auth = useAuthStore()

// ── Profile form ──────────────────────────────────────────────────────
const profileForm = reactive({
  firstName: '',
  lastName: '',
  phone: '',
  bio: '',
  avatarUrl: '',
  specialization: '',
  experienceYears: undefined as number | undefined,
})

const originalProfile = ref('')
const profileLoading = ref(false)
const profileError = ref('')

const isDirty = computed(() => {
  return JSON.stringify(profileForm) !== originalProfile.value
})

const userInitials = computed(() => {
  const f = profileForm.firstName?.charAt(0) || ''
  const l = profileForm.lastName?.charAt(0) || ''
  return (f + l).toUpperCase() || '?'
})

function mapProfileErrorMessage(rawMessage: string) {
  if (
    rawMessage.includes('experienceYears.jakarta.validation.constraints.PositiveOrZero.message') ||
    rawMessage.includes('PositiveOrZero')
  ) {
    return t('validation.experienceYearsPositiveOrZero')
  }

  return rawMessage
}

function loadProfile() {
  if (!auth.user) return
  profileForm.firstName = auth.user.firstName || ''
  profileForm.lastName = auth.user.lastName || ''
  profileForm.phone = auth.user.phone || ''
  profileForm.bio = auth.user.bio || ''
  profileForm.avatarUrl = auth.user.avatarUrl || ''
  profileForm.specialization = auth.user.specialization || ''
  profileForm.experienceYears = auth.user.experienceYears
  originalProfile.value = JSON.stringify(profileForm)
}

onMounted(loadProfile)
watch(() => auth.user, loadProfile)

async function saveProfile() {
  profileError.value = ''
  if (
    typeof profileForm.experienceYears === 'number' &&
    Number.isFinite(profileForm.experienceYears) &&
    profileForm.experienceYears < 0
  ) {
    profileError.value = t('validation.experienceYearsPositiveOrZero')
    return
  }
  profileLoading.value = true
  try {
    await updateMyProfile({
      firstName: profileForm.firstName,
      lastName: profileForm.lastName,
      phone: profileForm.phone || undefined,
      bio: profileForm.bio || undefined,
      // Keep empty string when avatar is removed so backend persists clearing.
      avatarUrl: profileForm.avatarUrl,
      specialization: profileForm.specialization || undefined,
      experienceYears: profileForm.experienceYears,
    })
    await auth.fetchMyProfile()
    originalProfile.value = JSON.stringify(profileForm)
    toast.success(t('profile.saveSuccess'))
  } catch (err) {
    if (axios.isAxiosError(err) && err.response?.data) {
      const message = (err.response.data as ApiError).message || t('errors.generic')
      profileError.value = mapProfileErrorMessage(message)
    } else {
      profileError.value = t('errors.network')
    }
  } finally {
    profileLoading.value = false
  }
}

// ── Password form ─────────────────────────────────────────────────────
const passwordOpen = ref(false)
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
})
const passwordErrors = reactive({
  currentPassword: '',
  newPassword: '',
})
const passwordLoading = ref(false)
const passwordApiError = ref('')

function validatePassword(): boolean {
  let valid = true
  passwordErrors.currentPassword = ''
  passwordErrors.newPassword = ''

  if (!passwordForm.currentPassword) {
    passwordErrors.currentPassword = t('validation.required')
    valid = false
  }
  if (!passwordForm.newPassword) {
    passwordErrors.newPassword = t('validation.required')
    valid = false
  } else if (passwordForm.newPassword.length < 6) {
    passwordErrors.newPassword = t('validation.passwordMin')
    valid = false
  }

  return valid
}

async function savePassword() {
  passwordApiError.value = ''
  if (!validatePassword()) return

  passwordLoading.value = true
  try {
    await changePassword({
      currentPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword,
    })
    passwordForm.currentPassword = ''
    passwordForm.newPassword = ''
    passwordOpen.value = false
    toast.success(t('profile.passwordSuccess'))
  } catch (err) {
    if (axios.isAxiosError(err) && err.response?.data) {
      passwordApiError.value =
        (err.response.data as ApiError).message || t('errors.generic')
    } else {
      passwordApiError.value = t('errors.network')
    }
  } finally {
    passwordLoading.value = false
  }
}
</script>

<template>
  <DefaultLayout>
    <div class="mx-auto max-w-4xl px-4 py-10 sm:px-6 lg:px-8">
      <h1 class="text-2xl font-bold tracking-tight text-slate-900">
        {{ t('profile.title') }}
      </h1>
      <p class="mt-1 text-sm text-slate-500">{{ t('profile.subtitle') }}</p>

      <div class="mt-8 grid grid-cols-1 gap-8 lg:grid-cols-3">
        <!-- Left column — avatar & info -->
        <div class="lg:col-span-1">
          <AppCard>
            <div class="flex flex-col items-center py-4">
              <AvatarUpload v-model="profileForm.avatarUrl" :initials="userInitials" />

              <h2 class="mt-4 text-lg font-semibold text-slate-900">
                {{ auth.user?.fullName || '—' }}
              </h2>
              <p class="text-sm text-slate-500">{{ auth.user?.email }}</p>

              <span
                class="mt-3 inline-flex items-center rounded-full px-3 py-1 text-xs font-medium"
                :class="
                  auth.isAdmin
                    ? 'bg-rose-100 text-rose-700'
                    : auth.isTeacher
                      ? 'bg-violet-100 text-violet-700'
                      : 'bg-amber-100 text-amber-700'
                "
              >
                {{ t(`roles.${auth.user?.role || 'STUDENT'}`) }}
              </span>

              <p v-if="auth.user?.createdAt" class="mt-4 flex items-center gap-1 text-xs text-slate-400">
                <Icon icon="mdi:calendar-outline" class="h-3.5 w-3.5" />
                {{ new Date(auth.user.createdAt).toLocaleDateString() }}
              </p>
            </div>
          </AppCard>
        </div>

        <!-- Right column — edit form -->
        <div class="space-y-6 lg:col-span-2">
          <AppCard>
            <template #header>
              <h2 class="text-lg font-semibold text-slate-900">{{ t('profile.editTitle') }}</h2>
            </template>

            <!-- Profile error -->
            <div
              v-if="profileError"
              class="mb-4 flex items-start gap-2 rounded-xl border border-rose-200 bg-rose-50 px-4 py-3"
            >
              <Icon icon="mdi:alert-circle-outline" class="mt-0.5 h-5 w-5 shrink-0 text-rose-500" />
              <p class="text-sm text-rose-700">{{ profileError }}</p>
            </div>

            <form class="space-y-5" @submit.prevent="saveProfile">
              <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <AppInput
                  v-model="profileForm.firstName"
                  :label="t('form.firstName')"
                  icon="mdi:account-outline"
                  required
                />
                <AppInput
                  v-model="profileForm.lastName"
                  :label="t('form.lastName')"
                  icon="mdi:account-outline"
                  required
                />
              </div>

              <AppInput
                v-model="profileForm.phone"
                :label="t('form.phone')"
                type="tel"
                icon="mdi:phone-outline"
                placeholder="+380..."
              />

              <div>
                <label class="mb-1.5 block text-sm font-medium text-slate-700">
                  {{ t('form.bio') }}
                </label>
                <textarea
                  v-model="profileForm.bio"
                  rows="3"
                  class="block w-full rounded-lg border border-slate-200 bg-white px-3.5 py-2.5 text-sm text-slate-900 shadow-sm transition-all duration-200 placeholder:text-slate-400 focus:border-transparent focus:outline-none focus:ring-2 focus:ring-amber-500"
                  :placeholder="t('profile.bioPlaceholder')"
                ></textarea>
              </div>

              <!-- Teacher-specific fields -->
              <template v-if="auth.isTeacher">
                <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
                  <AppInput
                    v-model="profileForm.specialization"
                    :label="t('form.specialization')"
                    icon="mdi:briefcase-outline"
                  />
                  <AppInput
                    v-model.number="profileForm.experienceYears"
                    :label="t('form.experienceYears')"
                    type="number"
                    :min="0"
                    icon="mdi:timer-sand"
                  />
                </div>
              </template>

              <div class="flex justify-end">
                <AppButton
                  type="submit"
                  variant="primary"
                  :loading="profileLoading"
                  :disabled="!isDirty"
                >
                  {{ t('actions.save') }}
                </AppButton>
              </div>
            </form>
          </AppCard>

          <!-- Change password -->
          <AppCard>
            <template #header>
              <button
                type="button"
                class="flex w-full items-center justify-between"
                @click="passwordOpen = !passwordOpen"
              >
                <h2 class="text-lg font-semibold text-slate-900">{{ t('profile.changePassword') }}</h2>
                <Icon
                  icon="mdi:chevron-down"
                  class="h-5 w-5 text-slate-400 transition-transform duration-200"
                  :class="{ 'rotate-180': passwordOpen }"
                />
              </button>
            </template>

            <Transition
              enter-active-class="transition-all duration-200 ease-out"
              enter-from-class="max-h-0 opacity-0"
              enter-to-class="max-h-96 opacity-100"
              leave-active-class="transition-all duration-150 ease-in"
              leave-from-class="max-h-96 opacity-100"
              leave-to-class="max-h-0 opacity-0"
            >
              <div v-if="passwordOpen" class="overflow-hidden">
                <!-- Password error -->
                <div
                  v-if="passwordApiError"
                  class="mb-4 flex items-start gap-2 rounded-xl border border-rose-200 bg-rose-50 px-4 py-3"
                >
                  <Icon icon="mdi:alert-circle-outline" class="mt-0.5 h-5 w-5 shrink-0 text-rose-500" />
                  <p class="text-sm text-rose-700">{{ passwordApiError }}</p>
                </div>

                <form class="space-y-5" @submit.prevent="savePassword">
                  <AppInput
                    v-model="passwordForm.currentPassword"
                    :label="t('form.currentPassword')"
                    type="password"
                    icon="mdi:lock-outline"
                    :error="passwordErrors.currentPassword"
                    required
                  />

                  <AppInput
                    v-model="passwordForm.newPassword"
                    :label="t('form.newPassword')"
                    type="password"
                    icon="mdi:lock-plus-outline"
                    :error="passwordErrors.newPassword"
                    :hint="t('validation.passwordHint')"
                    required
                  />

                  <div class="flex justify-end">
                    <AppButton type="submit" variant="primary" :loading="passwordLoading">
                      {{ t('profile.changePasswordButton') }}
                    </AppButton>
                  </div>
                </form>
              </div>
            </Transition>
          </AppCard>
        </div>
      </div>
    </div>
  </DefaultLayout>
</template>
