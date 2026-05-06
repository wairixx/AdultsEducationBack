import './assets/main.css'
import 'vue-sonner/style.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router, { setHydrationPromise } from './router'
import i18n from './i18n'
import { useAuthStore } from './stores/auth'

const app = createApp(App)

const pinia = createPinia()
app.use(pinia)
app.use(i18n)
app.use(router)

// Hydrate auth state — the router guard will await this promise
const authStore = useAuthStore()
const hydrationPromise = authStore.hydrate()
setHydrationPromise(hydrationPromise)

app.mount('#app')
