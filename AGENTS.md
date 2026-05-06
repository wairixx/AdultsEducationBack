# Agent Instructions — Adults Education Project

## 📌 Repository Overview

This workspace contains **two separate projects**:

1. **`AdultsEducation/`** — Spring Boot REST API (backend). **READ-ONLY for agent.**
2. **`adults-education-frontend/`** — Vue 3 + Vite + TypeScript + Tailwind CSS v4 (frontend). **This is where you work.**

---

## 🔒 Hard Rules

### 1. Backend is read-only
- Do **NOT** edit, create, or delete any file under `AdultsEducation/`.
- You **MAY** read any file there to understand DTOs, endpoints, enums, business rules.
- The primary source of truth is `AdultsEducation/README.md` — read it first for full API documentation.
- Also consult `AdultsEducation/src/main/java/.../model/dto/**` for exact DTO shapes.
- Also consult `AdultsEducation/src/main/java/.../model/enums/**` for enum values.

### 2. If backend is missing something
If during frontend implementation you discover that backend lacks an endpoint / DTO field / feature:
- **STOP. Do not modify backend.**
- Write a clear message to the user explaining:
  - What is missing
  - Why frontend needs it
  - What backend change is suggested (file names, proposed solution)
- Wait for explicit user approval (`yes, modify backend` / `approved`) before touching backend.
- When modifying backend (if approved), **match existing code style**:
  - Same package structure (controller → facade → service → repository)
  - Lombok `@RequiredArgsConstructor`, `@Slf4j`, `@Loggable`
  - i18n message keys in `messages.properties` + `messages_en.properties`
  - Same exception types (`BusinessException`, `ResourceNotFoundException`, etc.)

### 3. Do not break existing frontend setup
- Project was bootstrapped with `npm create vue@latest` + Tailwind v4 via `@tailwindcss/vite`.
- `main.css` uses `@import "tailwindcss";` — do not switch to old Tailwind config.
- Do not eject or change build tools.

---

## 🎨 Design System

### Visual language
- **Minimalistic, modern, clean.** Lots of whitespace.
- **Bright accent colors** on a mostly neutral base.
- **Rounded corners** (`rounded-xl`, `rounded-2xl`).
- **Soft shadows** (`shadow-sm`, `shadow-md`, `shadow-lg`).
- **Smooth transitions** (`transition-all duration-200`).
- Fully **responsive** (mobile-first).

### Color palette (use via Tailwind classes)
- **Primary (brand gold/amber)**: `amber-500`, `amber-600`, `amber-700` — buttons, active states, links
- **Secondary accent (deep purple for CTA)**: `violet-600`, `violet-700`
- **Success**: `emerald-500`, `emerald-600`
- **Danger**: `rose-500`, `rose-600`
- **Warning**: `orange-500`
- **Neutral**: `slate-50`, `slate-100`, `slate-200` (backgrounds), `slate-600`, `slate-800`, `slate-900` (text)
- **Cert / achievement**: gradient `from-amber-400 via-yellow-500 to-amber-600`

### Typography
- Default: system fonts or `Inter` (via Google Fonts if easy).
- Headings: bold, tight tracking (`tracking-tight`).
- Body: `text-slate-700`, `text-slate-600` for secondary.

### Components style guide
- **Buttons**: `rounded-lg`, `px-4 py-2`, `font-medium`, hover states, focus rings.
- **Cards**: `rounded-2xl`, `bg-white`, `shadow-sm`, `border border-slate-100`, hover `shadow-lg transition`.
- **Inputs**: `rounded-lg`, `border-slate-200`, focus `ring-2 ring-amber-500 border-transparent`.
- **Badges** for status: colored `bg-{color}-100 text-{color}-700 rounded-full px-3 py-1 text-xs font-medium`.

### UX principles
- Loading states: skeletons (not spinners, where possible).
- Empty states: friendly illustration or icon + message + action.
- Error states: red banner with clear message from API's `message` field.
- Toast notifications for success/error (use `vue-sonner`).
- Confirmation modals for destructive actions.

---

## 🌐 Localization (i18n)

- Use **`vue-i18n`** (install via `npm install vue-i18n`).
- Two locales: `uk` (default) and `en`.
- All visible text must go through `$t('key')` / `t('key')`.
- Send `Accept-Language: uk` or `Accept-Language: en` header in axios so backend error messages are translated too.
- Locale switcher in navbar (top-right).

---

## 🏗 Frontend Architecture

### Stack
- Vue 3 `<script setup>` + Composition API
- TypeScript (strict)
- Vite
- Tailwind CSS v4
- Pinia (state: auth, user)
- Vue Router (with guards)
- Axios (API client with JWT interceptor)
- vue-i18n
- vue-sonner (toasts)
- @iconify/vue (icons)
- dayjs (dates)

### Folder structure
```
src/
├── api/           # axios-based API functions per domain
├── assets/
├── components/
│   ├── common/    # Button, Input, Modal, Card, Pagination, ...
│   ├── layout/    # AppNavbar, AppFooter, AppSidebar
│   ├── course/    # CourseCard, CourseFilters, ...
│   └── ...
├── composables/   # useAuth, useDebounce, usePagination
├── i18n/
│   ├── index.ts
│   ├── uk.ts
│   └── en.ts
├── router/
├── stores/        # Pinia
├── types/         # TS interfaces mirroring backend DTOs
├── utils/
├── views/         # pages
├── App.vue
└── main.ts
```

### Naming conventions
- Components: `PascalCase.vue` (e.g., `CourseCard.vue`).
- Composables: `useCamelCase.ts`.
- API functions: `camelCase` (e.g., `fetchCourseById`).
- Types: `PascalCase` (e.g., `CourseResponse`).

### State management rule
- `auth` store — current user, token, login/logout/register.
- Pages own their fetch state (loading, error, data) via composables.
- Don't over-centralize; prefer local state + API calls in views.

---

## 🧩 Backend API Integration

- **Base URL**: `http://localhost:8080`
- **CORS**: already configured for `http://localhost:5173`.
- **Auth**: JWT. Store in `localStorage`. Add `Authorization: Bearer <token>` in axios interceptor.
- On `401` response: clear auth state, redirect to `/login`.
- On `403`: show "Access denied" page or toast.
- **Swagger UI** available at `http://localhost:8080/swagger-ui.html` — inspect when unsure.

### Role-based access
Four experience levels:
- **Guest**: browse courses, view lessons preview, verify certificates.
- **STUDENT**: everything above + enrollments, progress, reviews, certificates, profile.
- **TEACHER**: create/manage courses, lessons; view enrolled students.
- **ADMIN**: full access + moderation.

Router guards must:
- Redirect unauthenticated users trying to access protected routes → `/login`.
- Redirect role-mismatched users → `/403` or home.

---

## 📋 Features Checklist (roadmap)

The frontend must implement:

1. **Auth**: login, register (with role choice: STUDENT/TEACHER), logout, password change.
2. **Profile (`/me`)**: view + edit profile, avatar upload, role-specific fields.
3. **Public course catalog**: list with **pagination**, **search by title**, **filters** (topic, format, price range, hours range), sorting (price, duration, rating, date).
4. **Course detail page**: full info, teacher mini-card, lessons preview list, reviews section, "Enroll" CTA.
5. **Enrollment flow**: modal with payment method selection, transaction ref input.
6. **Student dashboard (`/my-courses`)**: list of enrollments with progress bars, filter by status.
7. **Learning mode (`/my-courses/:id`)**: sidebar with lessons (checkmarks on completed), main area with lesson content + video + "Mark as completed" button. Auto-celebrate when course finishes.
8. **Certificate view**: beautiful page with preview, share link, download PDF buttons.
9. **Public certificate verify (`/cert/:number`)**: shows authenticity, HTML/PDF view.
10. **Teacher dashboard**: my courses list, create/edit course form with cover upload, lesson management.
11. **Teacher — student management**: see students on my courses, update status/level/note.
12. **Admin panel**: users table with blocking, courses moderation (show/hide), reviews moderation.
13. **Pagination component** — reusable everywhere.
14. **Search** — debounced inputs, clear buttons.
15. **Locale switcher** — UK/EN, persisted to localStorage.

---

## ✅ Quality gates before declaring a feature "done"

- TypeScript passes (`npm run type-check`).
- ESLint passes (`npm run lint`).
- Feature works for all relevant roles.
- Empty / loading / error states handled.
- Mobile responsive.
- All text localized.
- Reusing existing components where possible.