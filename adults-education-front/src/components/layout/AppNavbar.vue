<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Icon } from '@iconify/vue'
import { useAuthStore } from '@/stores/auth'
import { setLocale, getCurrentLocale } from '@/i18n'

const { t } = useI18n()
const router = useRouter()
const auth = useAuthStore()

const mobileMenuOpen = ref(false)
const userDropdownOpen = ref(false)
const localeDropdownOpen = ref(false)
const currentLocale = ref(getCurrentLocale())

const userInitials = computed(() => {
  if (!auth.user) return '?'
  const first = auth.user.firstName?.charAt(0) || ''
  const last = auth.user.lastName?.charAt(0) || ''
  return (first + last).toUpperCase() || '?'
})

function toggleLocale(locale: 'uk' | 'en') {
  setLocale(locale)
  currentLocale.value = locale
  localeDropdownOpen.value = false
}

function handleLogout() {
  auth.logout()
  userDropdownOpen.value = false
  mobileMenuOpen.value = false
  router.push('/')
}

function closeMobileMenu() {
  mobileMenuOpen.value = false
}

// Close dropdowns on outside click
function onClickOutside(e: MouseEvent) {
  const target = e.target as HTMLElement
  if (!target.closest('#user-dropdown-wrapper')) {
    userDropdownOpen.value = false
  }
  if (!target.closest('#locale-dropdown-wrapper')) {
    localeDropdownOpen.value = false
  }
}

onMounted(() => document.addEventListener('click', onClickOutside))
onUnmounted(() => document.removeEventListener('click', onClickOutside))
</script>

<template>
  <nav
    class="sticky top-0 z-50 border-b border-slate-100 bg-white/80 backdrop-blur-lg"
  >
    <div class="mx-auto flex h-16 max-w-7xl items-center justify-between px-4 sm:px-6 lg:px-8">
      <!-- Logo -->
      <RouterLink
        to="/"
        class="flex items-center gap-2 text-xl font-bold tracking-tight text-slate-900 transition-colors hover:text-amber-600"
      >
        <span class="text-2xl">🎓</span>
        <span>AL Academy</span>
      </RouterLink>

      <!-- Desktop nav (center) -->
      <div class="hidden items-center gap-1 md:flex">
        <RouterLink
          to="/courses"
          class="rounded-lg px-3 py-2 text-sm font-medium text-slate-600 transition-all duration-200 hover:bg-slate-50 hover:text-slate-900"
          active-class="!bg-amber-50 !text-amber-700"
        >
          {{ t('nav.courses') }}
        </RouterLink>

        <RouterLink
          v-if="auth.isAuthenticated && auth.user?.role === 'STUDENT'"
          to="/my-courses"
          class="rounded-lg px-3 py-2 text-sm font-medium text-slate-600 transition-all duration-200 hover:bg-slate-50 hover:text-slate-900"
          active-class="!bg-amber-50 !text-amber-700"
        >
          {{ t('nav.myCourses') }}
        </RouterLink>

        <RouterLink
          v-if="auth.isAuthenticated && auth.user?.role === 'TEACHER'"
          to="/dashboard"
          class="rounded-lg px-3 py-2 text-sm font-medium text-slate-600 transition-all duration-200 hover:bg-slate-50 hover:text-slate-900"
          active-class="!bg-amber-50 !text-amber-700"
        >
          {{ t('nav.dashboard') }}
        </RouterLink>

        <RouterLink
          v-if="auth.isAuthenticated && auth.user?.role === 'ADMIN'"
          to="/admin"
          class="rounded-lg px-3 py-2 text-sm font-medium text-slate-600 transition-all duration-200 hover:bg-slate-50 hover:text-slate-900"
          active-class="!bg-amber-50 !text-amber-700"
        >
          {{ t('nav.admin') }}
        </RouterLink>
      </div>

      <!-- Right side -->
      <div class="flex items-center gap-2">
        <!-- Locale switcher -->
        <div id="locale-dropdown-wrapper" class="relative">
          <button
            class="flex items-center gap-1 rounded-lg px-2.5 py-1.5 text-sm font-medium text-slate-600 transition-all duration-200 hover:bg-slate-50 hover:text-slate-900"
            @click.stop="localeDropdownOpen = !localeDropdownOpen"
            aria-label="Змінити мову"
          >
            <Icon icon="mdi:translate" class="h-4 w-4" />
            <span class="uppercase">{{ currentLocale }}</span>
            <Icon icon="mdi:chevron-down" class="h-3.5 w-3.5 transition-transform" :class="{ 'rotate-180': localeDropdownOpen }" />
          </button>
          <Transition
            enter-active-class="transition duration-150 ease-out"
            enter-from-class="scale-95 opacity-0"
            enter-to-class="scale-100 opacity-100"
            leave-active-class="transition duration-100 ease-in"
            leave-from-class="scale-100 opacity-100"
            leave-to-class="scale-95 opacity-0"
          >
            <div
              v-if="localeDropdownOpen"
              class="absolute right-0 mt-2 w-32 origin-top-right rounded-xl border border-slate-100 bg-white py-1 shadow-lg"
            >
              <button
                class="flex w-full items-center gap-2 px-3 py-2 text-sm transition-colors hover:bg-slate-50"
                :class="currentLocale === 'uk' ? 'text-amber-700 font-medium' : 'text-slate-600'"
                @click="toggleLocale('uk')"
              >
                🇺🇦 Українська
              </button>
              <button
                class="flex w-full items-center gap-2 px-3 py-2 text-sm transition-colors hover:bg-slate-50"
                :class="currentLocale === 'en' ? 'text-amber-700 font-medium' : 'text-slate-600'"
                @click="toggleLocale('en')"
              >
                🇬🇧 English
              </button>
            </div>
          </Transition>
        </div>

        <!-- Guest buttons (desktop) -->
        <template v-if="!auth.isAuthenticated">
          <RouterLink
            to="/login"
            class="hidden rounded-lg px-4 py-2 text-sm font-medium text-slate-700 transition-all duration-200 hover:bg-slate-50 md:inline-flex"
          >
            {{ t('nav.login') }}
          </RouterLink>
          <RouterLink
            to="/register"
            class="hidden rounded-lg bg-amber-500 px-4 py-2 text-sm font-medium text-white shadow-sm transition-all duration-200 hover:bg-amber-600 hover:shadow-md md:inline-flex"
          >
            {{ t('nav.register') }}
          </RouterLink>
        </template>

        <!-- User avatar + dropdown (desktop) -->
        <div v-if="auth.isAuthenticated" id="user-dropdown-wrapper" class="relative hidden md:block">
          <button
            class="flex items-center gap-2 rounded-lg px-2 py-1.5 transition-all duration-200 hover:bg-slate-50"
            @click.stop="userDropdownOpen = !userDropdownOpen"
            aria-label="Меню користувача"
          >
            <div
              v-if="auth.user?.avatarUrl"
              class="h-8 w-8 overflow-hidden rounded-full ring-2 ring-amber-200"
            >
              <img :src="auth.user.avatarUrl" alt="avatar" class="h-full w-full object-cover" />
            </div>
            <div
              v-else
              class="flex h-8 w-8 items-center justify-center rounded-full bg-gradient-to-br from-amber-400 to-amber-600 text-xs font-bold text-white ring-2 ring-amber-200"
            >
              {{ userInitials }}
            </div>
            <Icon icon="mdi:chevron-down" class="h-3.5 w-3.5 text-slate-400 transition-transform" :class="{ 'rotate-180': userDropdownOpen }" />
          </button>
          <Transition
            enter-active-class="transition duration-150 ease-out"
            enter-from-class="scale-95 opacity-0"
            enter-to-class="scale-100 opacity-100"
            leave-active-class="transition duration-100 ease-in"
            leave-from-class="scale-100 opacity-100"
            leave-to-class="scale-95 opacity-0"
          >
            <div
              v-if="userDropdownOpen"
              class="absolute right-0 mt-2 w-48 origin-top-right rounded-xl border border-slate-100 bg-white py-1 shadow-lg"
            >
              <div class="border-b border-slate-100 px-3 py-2">
                <p class="text-sm font-medium text-slate-900">{{ auth.user?.fullName }}</p>
                <p class="text-xs text-slate-500">{{ auth.user?.email }}</p>
              </div>
              <RouterLink
                to="/profile"
                class="flex w-full items-center gap-2 px-3 py-2 text-sm text-slate-600 transition-colors hover:bg-slate-50 hover:text-slate-900"
                @click="userDropdownOpen = false"
              >
                <Icon icon="mdi:account-outline" class="h-4 w-4" />
                {{ t('nav.profile') }}
              </RouterLink>
              <button
                class="flex w-full items-center gap-2 px-3 py-2 text-sm text-rose-600 transition-colors hover:bg-rose-50"
                @click="handleLogout"
              >
                <Icon icon="mdi:logout" class="h-4 w-4" />
                {{ t('nav.logout') }}
              </button>
            </div>
          </Transition>
        </div>

        <!-- Mobile hamburger -->
        <button
          class="rounded-lg p-2 text-slate-600 transition-colors hover:bg-slate-50 hover:text-slate-900 md:hidden"
          @click="mobileMenuOpen = !mobileMenuOpen"
          aria-label="Відкрити меню"
        >
          <Icon :icon="mobileMenuOpen ? 'mdi:close' : 'mdi:menu'" class="h-6 w-6" />
        </button>
      </div>
    </div>

    <!-- Mobile menu -->
    <Transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="-translate-y-2 opacity-0"
      enter-to-class="translate-y-0 opacity-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="translate-y-0 opacity-100"
      leave-to-class="-translate-y-2 opacity-0"
    >
      <div
        v-if="mobileMenuOpen"
        class="border-b border-slate-100 bg-white px-4 pb-4 pt-2 md:hidden"
      >
        <div class="flex flex-col gap-1">
          <RouterLink
            to="/courses"
            class="rounded-lg px-3 py-2.5 text-sm font-medium text-slate-600 transition-colors hover:bg-slate-50 hover:text-slate-900"
            active-class="!bg-amber-50 !text-amber-700"
            @click="closeMobileMenu"
          >
            {{ t('nav.courses') }}
          </RouterLink>

          <RouterLink
            v-if="auth.isStudent"
            to="/my-courses"
            class="rounded-lg px-3 py-2.5 text-sm font-medium text-slate-600 transition-colors hover:bg-slate-50 hover:text-slate-900"
            active-class="!bg-amber-50 !text-amber-700"
            @click="closeMobileMenu"
          >
            {{ t('nav.myCourses') }}
          </RouterLink>

          <RouterLink
            v-if="auth.isTeacher"
            to="/dashboard"
            class="rounded-lg px-3 py-2.5 text-sm font-medium text-slate-600 transition-colors hover:bg-slate-50 hover:text-slate-900"
            active-class="!bg-amber-50 !text-amber-700"
            @click="closeMobileMenu"
          >
            {{ t('nav.dashboard') }}
          </RouterLink>

          <RouterLink
            v-if="auth.isAdmin"
            to="/admin"
            class="rounded-lg px-3 py-2.5 text-sm font-medium text-slate-600 transition-colors hover:bg-slate-50 hover:text-slate-900"
            active-class="!bg-amber-50 !text-amber-700"
            @click="closeMobileMenu"
          >
            {{ t('nav.admin') }}
          </RouterLink>
        </div>

        <!-- Mobile auth section -->
        <div class="mt-3 border-t border-slate-100 pt-3">
          <template v-if="!auth.isAuthenticated">
            <RouterLink
              to="/login"
              class="block rounded-lg px-3 py-2.5 text-center text-sm font-medium text-slate-700 transition-colors hover:bg-slate-50"
              @click="closeMobileMenu"
            >
              {{ t('nav.login') }}
            </RouterLink>
            <RouterLink
              to="/register"
              class="mt-1 block rounded-lg bg-amber-500 px-3 py-2.5 text-center text-sm font-medium text-white transition-colors hover:bg-amber-600"
              @click="closeMobileMenu"
            >
              {{ t('nav.register') }}
            </RouterLink>
          </template>

          <template v-else>
            <div class="flex items-center gap-3 px-3 py-2">
              <div
                v-if="auth.user?.avatarUrl"
                class="h-10 w-10 overflow-hidden rounded-full ring-2 ring-amber-200"
              >
                <img :src="auth.user.avatarUrl" alt="avatar" class="h-full w-full object-cover" />
              </div>
              <div
                v-else
                class="flex h-10 w-10 items-center justify-center rounded-full bg-gradient-to-br from-amber-400 to-amber-600 text-sm font-bold text-white ring-2 ring-amber-200"
              >
                {{ userInitials }}
              </div>
              <div>
                <p class="text-sm font-medium text-slate-900">{{ auth.user?.fullName }}</p>
                <p class="text-xs text-slate-500">{{ auth.user?.email }}</p>
              </div>
            </div>
            <RouterLink
              to="/profile"
              class="mt-1 flex items-center gap-2 rounded-lg px-3 py-2.5 text-sm text-slate-600 transition-colors hover:bg-slate-50"
              @click="closeMobileMenu"
            >
              <Icon icon="mdi:account-outline" class="h-4 w-4" />
              {{ t('nav.profile') }}
            </RouterLink>
            <button
              class="mt-1 flex w-full items-center gap-2 rounded-lg px-3 py-2.5 text-sm text-rose-600 transition-colors hover:bg-rose-50"
              @click="handleLogout"
            >
              <Icon icon="mdi:logout" class="h-4 w-4" />
              {{ t('nav.logout') }}
            </button>
          </template>
        </div>
      </div>
    </Transition>
  </nav>
</template>
