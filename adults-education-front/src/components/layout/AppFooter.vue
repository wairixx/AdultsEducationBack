<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { RouterLink } from 'vue-router'
import { Icon } from '@iconify/vue'

const { t } = useI18n()
const currentYear = new Date().getFullYear()
</script>

<template>
  <footer class="bg-slate-900 text-slate-400">
    <div class="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
      <div class="grid grid-cols-1 gap-8 md:grid-cols-3">
        <!-- About -->
        <div>
          <h3 class="mb-4 text-sm font-semibold uppercase tracking-wider text-slate-200">
            {{ t('footer.about') }}
          </h3>
          <div class="mb-4 flex items-center gap-2 text-lg font-bold text-white">
            <span class="text-xl">🎓</span>
            <span>AL Academy</span>
          </div>
          <p class="text-sm leading-relaxed">
            {{ t('footer.aboutText') }}
          </p>
        </div>

        <!-- Links -->
        <div>
          <h3 class="mb-4 text-sm font-semibold uppercase tracking-wider text-slate-200">
            {{ t('footer.links') }}
          </h3>
          <ul class="space-y-2.5">
            <li>
              <RouterLink
                to="/"
                class="text-sm transition-colors duration-200 hover:text-amber-400"
              >
                {{ t('nav.home') }}
              </RouterLink>
            </li>
            <li>
              <RouterLink
                to="/courses"
                class="text-sm transition-colors duration-200 hover:text-amber-400"
              >
                {{ t('nav.courses') }}
              </RouterLink>
            </li>
            <li>
              <RouterLink
                to="/register"
                class="text-sm transition-colors duration-200 hover:text-amber-400"
              >
                {{ t('nav.register') }}
              </RouterLink>
            </li>
          </ul>
        </div>

        <!-- Contact -->
        <div>
          <h3 class="mb-4 text-sm font-semibold uppercase tracking-wider text-slate-200">
            {{ t('footer.contact') }}
          </h3>
          <ul class="space-y-2.5">
            <li class="flex items-center gap-2 text-sm">
              <Icon icon="mdi:email-outline" class="h-4 w-4 text-slate-500" />
              {{ t('footer.contactEmail') }}
            </li>
            <li class="flex items-center gap-2 text-sm">
              <Icon icon="mdi:phone-outline" class="h-4 w-4 text-slate-500" />
              {{ t('footer.contactPhone') }}
            </li>
            <li class="flex items-center gap-2 text-sm">
              <Icon icon="mdi:map-marker-outline" class="h-4 w-4 text-slate-500" />
              {{ t('footer.contactAddress') }}
            </li>
          </ul>

          <!-- Social icons -->
          <div class="mt-6 flex gap-3">
            <a
              href="#"
              class="flex h-9 w-9 items-center justify-center rounded-lg bg-slate-800 text-slate-400 transition-all duration-200 hover:bg-amber-500 hover:text-white"
            >
              <Icon icon="mdi:facebook" class="h-4.5 w-4.5" />
            </a>
            <a
              href="#"
              class="flex h-9 w-9 items-center justify-center rounded-lg bg-slate-800 text-slate-400 transition-all duration-200 hover:bg-amber-500 hover:text-white"
            >
              <Icon icon="mdi:instagram" class="h-4.5 w-4.5" />
            </a>
            <a
              href="#"
              class="flex h-9 w-9 items-center justify-center rounded-lg bg-slate-800 text-slate-400 transition-all duration-200 hover:bg-amber-500 hover:text-white"
            >
              <Icon icon="mdi:linkedin" class="h-4.5 w-4.5" />
            </a>
            <a
              href="#"
              class="flex h-9 w-9 items-center justify-center rounded-lg bg-slate-800 text-slate-400 transition-all duration-200 hover:bg-amber-500 hover:text-white"
            >
              <Icon icon="mdi:youtube" class="h-4.5 w-4.5" />
            </a>
          </div>
        </div>
      </div>

      <!-- Copyright -->
      <div class="mt-10 border-t border-slate-800 pt-6 text-center text-xs">
        {{ t('footer.copyright', { year: currentYear }) }}
      </div>
    </div>
  </footer>
</template>
