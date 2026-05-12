<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'
import { useI18n } from 'vue-i18n'
import { Icon } from '@iconify/vue'
import { toast } from 'vue-sonner'
import DataTable from '@/components/admin/DataTable.vue'
import ConfirmDialog from '@/components/admin/ConfirmDialog.vue'
import EditModal from '@/components/admin/EditModal.vue'
import AppButton from '@/components/common/AppButton.vue'
import AppInput from '@/components/common/AppInput.vue'
import SearchInput from '@/components/common/SearchInput.vue'
import SelectField from '@/components/common/SelectField.vue'
import AppPagination from '@/components/common/AppPagination.vue'
import AvatarUpload from '@/components/common/AvatarUpload.vue'
import { getAllUsers, updateUser, deleteUser } from '@/api/users'
import { register } from '@/api/auth'
import type { UserResponse, Role, UpdateUserRequest, RegisterRequest, ApiError } from '@/types/api'

const { t } = useI18n()

const loading = ref(true)
const users = ref<UserResponse[]>([])
const page = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)

// Filters
const filterEmail = ref('')
const filterRole = ref<Role | ''>('')
const filterActive = ref<'true' | 'false' | ''>('')

const roleOptions = [
  { value: '', label: t('filters.all') },
  { value: 'STUDENT', label: t('roles.STUDENT') },
  { value: 'TEACHER', label: t('roles.TEACHER') },
  { value: 'ADMIN', label: t('roles.ADMIN') },
]

const roleEditOptions = [
  { value: 'STUDENT', label: t('roles.STUDENT') },
  { value: 'TEACHER', label: t('roles.TEACHER') },
  { value: 'ADMIN', label: t('roles.ADMIN') },
]

const roleCreateOptions = [
  { value: 'STUDENT', label: t('roles.STUDENT') },
  { value: 'TEACHER', label: t('roles.TEACHER') },
]

const activeOptions = [
  { value: '', label: t('filters.all') },
  { value: 'true', label: t('admin.users.activeYes') },
  { value: 'false', label: t('admin.users.activeNo') },
]

const columns = [
  { key: 'avatarName', label: t('admin.users.avatarName') },
  { key: 'email', label: t('admin.users.email') },
  { key: 'phone', label: t('admin.users.phone') },
  { key: 'role', label: t('admin.users.role') },
  { key: 'active', label: t('admin.users.active') },
  { key: 'createdAt', label: t('admin.users.registered') },
  { key: 'actions', label: '' },
]

async function loadData() {
  loading.value = true
  try {
    const res = await getAllUsers(
      {
        email: filterEmail.value || undefined,
        role: filterRole.value || undefined,
        active: filterActive.value ? filterActive.value === 'true' : undefined,
      },
      page.value,
      10,
      'createdAt,desc',
    )
    users.value = res.content
    totalPages.value = res.totalPages
    totalElements.value = res.totalElements
  } catch {
    toast.error(t('errors.generic'))
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

watch([filterEmail, filterRole, filterActive], () => {
  page.value = 0
  loadData()
})

function setPage(p: number) {
  page.value = p
  loadData()
}

// Quick toggle Active
async function toggleActive(user: UserResponse) {
  try {
    const res = await updateUser(user.id, { active: !user.active })
    const idx = users.value.findIndex((u) => u.id === user.id)
    if (idx !== -1) users.value[idx] = res
    toast.success(t('admin.users.successUpdate'))
  } catch {
    /* handled by interceptor */
  }
}

// ── Create Modal ──────────────────────────────────────────────────────
const createModalOpen = ref(false)
const createSaving = ref(false)
const createForm = ref<RegisterRequest>({
  email: '',
  password: '',
  firstName: '',
  lastName: '',
  phone: '',
  birthDate: '',
  role: 'STUDENT',
  avatarUrl: '',
})
const createRole = ref('STUDENT')
const createPhoneError = ref('')
const createApiError = ref('')
const createTeacherForm = ref({
  specialization: '',
  experienceYears: undefined as number | undefined,
})

function openCreate() {
  createForm.value = {
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    phone: '',
    birthDate: '',
    avatarUrl: '',
  }
  createRole.value = 'STUDENT'
  createTeacherForm.value = {
    specialization: '',
    experienceYears: undefined,
  }
  createPhoneError.value = ''
  createModalOpen.value = true
}

async function saveCreate() {
  createPhoneError.value = ''
  createApiError.value = ''
  if (createForm.value.phone && !/^\+?[0-9]{10,15}$/.test(createForm.value.phone.replace(/[\s-()]/g, ''))) {
    createPhoneError.value = t('validation.phone')
    return
  }
  createSaving.value = true
  try {
    const authResponse = await register({
      ...createForm.value,
      role: createRole.value as Role,
    }, { skipToast: true })
    if (createRole.value === 'TEACHER') {
      await updateUser(authResponse.userId, {
        specialization: createTeacherForm.value.specialization || undefined,
        experienceYears: createTeacherForm.value.experienceYears,
      }, { skipToast: true })
    }
    toast.success(t('admin.users.successCreate'))
    createModalOpen.value = false
    loadData()
  } catch (err) {
    if (axios.isAxiosError(err) && err.response?.data) {
      createApiError.value = (err.response.data as ApiError).message || t('errors.generic')
    } else {
      createApiError.value = t('errors.network')
    }
  } finally {
    createSaving.value = false
  }
}

// ── Edit Modal ────────────────────────────────────────────────────────
const editModalOpen = ref(false)
const editingUser = ref<UserResponse | null>(null)
const editForm = ref<UpdateUserRequest>({})
const editRole = ref<string>('STUDENT')
const editActive = ref<string>('true')
const editPhoneError = ref('')
const editApiError = ref('')
const editSaving = ref(false)

function openEdit(user: UserResponse) {
  editingUser.value = user
  editForm.value = {
    email: user.email,
    firstName: user.firstName ?? '',
    lastName: user.lastName ?? '',
    phone: user.phone ?? '',
    bio: user.bio ?? '',
    birthDate: user.birthDate ?? '',
    specialization: user.specialization ?? '',
    experienceYears: user.experienceYears ?? undefined,
    avatarUrl: user.avatarUrl ?? '',
  }
  editRole.value = user.role
  editActive.value = String(user.active)
  editPhoneError.value = ''
  editModalOpen.value = true
}

async function saveEdit() {
  if (!editingUser.value) return
  editPhoneError.value = ''
  editApiError.value = ''
  if (editForm.value.phone && !/^\+?[0-9]{10,15}$/.test(editForm.value.phone.replace(/[\s-()]/g, ''))) {
    editPhoneError.value = t('validation.phone')
    return
  }
  editSaving.value = true
  try {
    const payload: UpdateUserRequest = {
      ...editForm.value,
      role: editRole.value as Role,
      active: editActive.value === 'true',
    }
    if (editRole.value !== 'TEACHER') {
      payload.specialization = undefined
      payload.experienceYears = undefined
    }
    const res = await updateUser(editingUser.value.id, payload, { skipToast: true })
    const idx = users.value.findIndex((u) => u.id === res.id)
    if (idx !== -1) users.value[idx] = res
    await loadData()
    toast.success(t('admin.users.successUpdate'))
    editModalOpen.value = false
  } catch (err) {
    if (axios.isAxiosError(err) && err.response?.data) {
      editApiError.value = (err.response.data as ApiError).message || t('errors.generic')
    } else {
      editApiError.value = t('errors.network')
    }
  } finally {
    editSaving.value = false
  }
}

// ── Delete Confirm ────────────────────────────────────────────────────
const confirmOpen = ref(false)
const userToDelete = ref<number | null>(null)
const deleteLoading = ref(false)

function openDelete(id: number) {
  userToDelete.value = id
  confirmOpen.value = true
}

async function executeDelete() {
  if (!userToDelete.value) return
  deleteLoading.value = true
  try {
    await deleteUser(userToDelete.value)
    toast.success(t('admin.users.successDelete'))
    loadData()
  } catch (error) {
    if (axios.isAxiosError(error) && error.response?.status === 409) {
      toast.error(t('admin.users.deleteConflict'))
    }
  } finally {
    deleteLoading.value = false
    confirmOpen.value = false
  }
}

function roleBadgeClass(role: Role) {
  return {
    'bg-amber-100 text-amber-800': role === 'ADMIN',
    'bg-violet-100 text-violet-800': role === 'TEACHER',
    'bg-sky-100 text-sky-800': role === 'STUDENT',
  }
}
</script>

<template>
  <div class="space-y-6">
    <!-- Header + Filters -->
    <div class="flex flex-col gap-4">
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-slate-900">{{ t('admin.users.title') }}</h1>
          <p class="text-sm text-slate-500 mt-1">
            {{ t('admin.users.totalCount', { count: totalElements }) }}
          </p>
        </div>
      </div>
      <div class="flex flex-col sm:flex-row sm:items-center gap-3">
        <div class="flex flex-col sm:flex-row gap-3">
          <SearchInput
            v-model="filterEmail"
            :placeholder="t('admin.users.search')"
            class="w-full sm:w-72"
          />
          <SelectField
            v-model="filterRole"
            :options="roleOptions"
            class="w-full sm:w-44"
          />
          <SelectField
            v-model="filterActive"
            :options="activeOptions"
            class="w-full sm:w-36"
          />
        </div>
        <AppButton variant="primary" class="sm:ml-auto" @click="openCreate">
          <Icon icon="mdi:plus" class="h-5 w-5" />
          {{ t('admin.users.createUser') }}
        </AppButton>
      </div>
    </div>

    <!-- Table -->
    <DataTable :columns="columns" :data="users" :loading="loading">
      <template #col-avatarName="{ item }">
        <div class="flex items-center gap-3">
          <div class="h-9 w-9 rounded-full bg-slate-200 overflow-hidden flex-shrink-0">
            <img
              v-if="item.avatarUrl"
              :src="item.avatarUrl"
              alt="User avatar"
              class="h-full w-full object-cover"
            />
            <div
              v-else
              class="flex h-full w-full items-center justify-center text-xs font-bold text-slate-500"
            >
              {{ item.fullName ? item.fullName.charAt(0).toUpperCase() : '?' }}
            </div>
          </div>
          <div>
            <p class="font-medium text-slate-900 text-sm">{{ item.fullName || '—' }}</p>
            <p v-if="item.specialization" class="text-xs text-slate-400">
              {{ item.specialization }}
            </p>
          </div>
        </div>
      </template>

      <template #col-phone="{ item }">
        <span class="text-sm text-slate-600">{{ item.phone || '—' }}</span>
      </template>

      <template #col-role="{ item }">
        <span
          class="inline-flex rounded-full px-2.5 py-0.5 text-xs font-medium"
          :class="roleBadgeClass(item.role)"
        >
          {{ t(`roles.${item.role}`) }}
        </span>
      </template>

      <template #col-active="{ item }">
        <button
          @click="toggleActive(item)"
          class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium transition-colors"
          :class="
            item.active
              ? 'bg-emerald-50 text-emerald-700 hover:bg-emerald-100'
              : 'bg-rose-50 text-rose-700 hover:bg-rose-100'
          "
          :aria-label="item.active ? t('admin.users.block') : t('admin.users.unblock')"
        >
          <span
            class="h-2 w-2 rounded-full"
            :class="item.active ? 'bg-emerald-500' : 'bg-rose-500'"
          ></span>
          {{ item.active ? t('admin.users.activeYes') : t('admin.users.activeNo') }}
        </button>
      </template>

      <template #col-createdAt="{ item }">
        <span class="text-sm text-slate-600">
          {{ new Date(item.createdAt).toLocaleDateString() }}
        </span>
      </template>

      <template #col-actions="{ item }">
        <div class="flex justify-end gap-1">
          <button
            @click="openEdit(item)"
            class="p-1.5 text-slate-400 hover:text-amber-600 transition-colors"
            :title="t('actions.edit')"
            :aria-label="t('actions.edit')"
          >
            <Icon icon="mdi:pencil" class="h-5 w-5" />
          </button>
          <button
            @click="openDelete(item.id)"
            class="p-1.5 text-slate-400 hover:text-rose-600 transition-colors"
            :title="t('actions.delete')"
            :aria-label="t('actions.delete')"
          >
            <Icon icon="mdi:delete" class="h-5 w-5" />
          </button>
        </div>
      </template>
    </DataTable>

    <AppPagination
      v-if="totalPages > 1"
      :current-page="page"
      :total-pages="totalPages"
      @page-change="setPage"
    />

    <!-- Delete Confirm -->
    <ConfirmDialog
      :is-open="confirmOpen"
      :title="t('admin.users.deleteConfirm')"
      :message="t('admin.users.deleteMessage')"
      :loading="deleteLoading"
      danger
      @confirm="executeDelete"
      @cancel="confirmOpen = false"
    />

    <!-- ═══ Create User Modal ═══ -->
    <EditModal
      :is-open="createModalOpen"
      :title="t('admin.users.createTitle')"
      @close="createModalOpen = false"
    >
      <div v-if="createApiError" class="mb-4 flex items-start gap-2 rounded-xl border border-rose-200 bg-rose-50 px-4 py-3">
        <Icon icon="mdi:alert-circle-outline" class="mt-0.5 h-5 w-5 shrink-0 text-rose-500" />
        <p class="text-sm text-rose-700">{{ createApiError }}</p>
      </div>
      <div class="space-y-4">
        <div class="flex justify-center pb-2">
          <AvatarUpload v-model="createForm.avatarUrl" :initials="createForm.firstName?.charAt(0) || '?'" />
        </div>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <AppInput v-model="createForm.firstName" :label="t('form.firstName')" required />
          <AppInput v-model="createForm.lastName" :label="t('form.lastName')" required />
        </div>
        <AppInput
          v-model="createForm.email"
          :label="t('form.email')"
          type="email"
          required
        />
        <AppInput
          v-model="createForm.password"
          :label="t('form.password')"
          type="password"
          :hint="t('validation.passwordHint')"
          required
        />
        <AppInput v-model="createForm.phone" :label="t('form.phone')" type="tel" :error="createPhoneError" />
        <AppInput
          v-model="createForm.birthDate"
          :label="t('form.birthDate')"
          type="date"
          required
        />
        <SelectField
          v-model="createRole"
          :label="t('admin.users.role')"
          :options="roleCreateOptions"
        />
        <div v-if="createRole === 'TEACHER'" class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <AppInput
            v-model="createTeacherForm.specialization"
            :label="t('form.specialization')"
          />
          <AppInput
            v-model="createTeacherForm.experienceYears"
            :label="t('form.experienceYears')"
            type="number"
            :min="0"
          />
        </div>
        <p class="text-xs text-slate-400">
          {{ t('admin.users.createHint') }}
        </p>
      </div>
      <template #footer>
        <AppButton variant="ghost" @click="createModalOpen = false">
          {{ t('actions.cancel') }}
        </AppButton>
        <AppButton variant="primary" :loading="createSaving" @click="saveCreate">
          {{ t('actions.create') }}
        </AppButton>
      </template>
    </EditModal>

    <!-- ═══ Edit User Modal ═══ -->
    <EditModal
      :is-open="editModalOpen"
      :title="t('admin.users.editTitle')"
      @close="editModalOpen = false"
    >
      <div v-if="editApiError" class="mb-4 flex items-start gap-2 rounded-xl border border-rose-200 bg-rose-50 px-4 py-3">
        <Icon icon="mdi:alert-circle-outline" class="mt-0.5 h-5 w-5 shrink-0 text-rose-500" />
        <p class="text-sm text-rose-700">{{ editApiError }}</p>
      </div>
      <div v-if="editingUser" class="space-y-4">
        <div class="flex justify-center pb-2">
          <AvatarUpload v-model="editForm.avatarUrl" :initials="editForm.firstName?.charAt(0) || '?'" />
        </div>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <AppInput v-model="editForm.firstName" :label="t('form.firstName')" />
          <AppInput v-model="editForm.lastName" :label="t('form.lastName')" />
        </div>
        <AppInput v-model="editForm.email" :label="t('form.email')" type="email" />
        <AppInput v-model="editForm.phone" :label="t('form.phone')" type="tel" :error="editPhoneError" />
        <AppInput
          v-model="editForm.birthDate"
          :label="t('form.birthDate')"
          type="date"
        />
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <SelectField
            v-model="editRole"
            :label="t('admin.users.role')"
            :options="roleEditOptions"
          />
          <SelectField
            v-model="editActive"
            :label="t('admin.users.active')"
            :options="[
              { value: 'true', label: t('admin.users.activeYes') },
              { value: 'false', label: t('admin.users.activeNo') },
            ]"
          />
        </div>
        <div>
          <label class="mb-1.5 block text-sm font-medium text-slate-700">{{ t('form.bio') }}</label>
          <textarea
            v-model="editForm.bio"
            rows="3"
            class="block w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 shadow-sm transition-all duration-200 focus:border-transparent focus:outline-none focus:ring-2 focus:ring-amber-500"
            :placeholder="t('profile.bioPlaceholder')"
          ></textarea>
        </div>
        <div v-if="editRole === 'TEACHER'" class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <AppInput v-model="editForm.specialization" :label="t('form.specialization')" />
          <AppInput
            v-model="editForm.experienceYears"
            :label="t('form.experienceYears')"
            type="number"
          />
        </div>
      </div>
      <template #footer>
        <AppButton variant="ghost" @click="editModalOpen = false">
          {{ t('actions.cancel') }}
        </AppButton>
        <AppButton variant="primary" :loading="editSaving" @click="saveEdit">
          {{ t('actions.save') }}
        </AppButton>
      </template>
    </EditModal>
  </div>
</template>
