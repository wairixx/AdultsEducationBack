# AL Academy Frontend 🎓

This is the Vue 3 + TypeScript frontend for the Adults Education platform (AL Academy). It provides a full-featured e-learning experience with separate interfaces for Guests, Students, Teachers, and Administrators.

## 🚀 Technology Stack
- **Framework**: Vue 3 (Composition API, `<script setup>`)
- **Language**: TypeScript
- **Styling**: Tailwind CSS v4 (`@tailwindcss/vite`)
- **State Management**: Pinia (Auth & User state)
- **Routing**: Vue Router
- **HTTP Client**: Axios (with centralized JWT & Error handling interceptors)
- **i18n**: `vue-i18n` (Ukrainian and English support)
- **Icons**: `@iconify/vue`
- **Toasts**: `vue-sonner`

## 📁 Folder Structure
```
src/
├── api/           # Axios-based API client and domain-specific endpoints
├── assets/        # Global CSS (main.css) and static assets
├── components/    # Reusable UI components
│   ├── admin/     # Admin-specific tables and modals
│   ├── common/    # Generic UI (Buttons, Inputs, Modals, Pagination, Skeletons)
│   ├── course/    # Course-specific UI (Cards, Filters)
│   └── layout/    # App shell (Navbar, Footer, Layouts)
├── composables/   # Reusable logic (usePagination)
├── i18n/          # Localization strings (uk.ts, en.ts)
├── router/        # Vue Router configuration & Auth/Role Guards
├── stores/        # Pinia stores (auth.ts)
├── types/         # TypeScript interfaces mirroring backend DTOs
├── views/         # Route pages (Home, Auth, Catalogs, Dashboards)
├── App.vue        # Root component with Suspense & Page Transitions
└── main.ts        # App initialization
```

## 🔐 Roles & Features

### 1. Guest
- Browse the public course catalog.
- View detailed course descriptions and lesson previews.
- Read public reviews.
- Register as either a Student or a Teacher.

### 2. Student (`STUDENT` role)
- Enroll in courses (handles payment method selection).
- Access a personalized "My Courses" dashboard tracking progress.
- Enter "Learning Mode" to watch videos and mark lessons as completed.
- Leave reviews and ratings for completed courses.
- Generate and download PDF certificates upon course completion.

### 3. Teacher (`TEACHER` role)
- Access a dedicated Teacher Dashboard with aggregate statistics.
- Create, edit, hide, and manage courses (including cover image uploads).
- Manage course curriculum (add, edit, and manually reorder lessons).
- Oversee enrolled students (view progress, update their learning level, and leave internal notes).

### 4. Admin (`ADMIN` role)
- Access a comprehensive Admin Panel (`/admin`).
- **Users**: Search, filter, edit roles, block/unblock, and delete accounts.
- **Courses**: View all courses (including hidden ones), toggle visibility globally, or force delete.
- **Reviews**: Moderate the community by hiding or deleting inappropriate reviews.
- **Enrollments**: Read-only global oversight of all platform enrollments.

## 🛠 Prerequisites
- Node.js (v18 or newer recommended)
- The accompanying Spring Boot Backend API running on `http://localhost:8080`.

## 💻 How to Run

1. **Install dependencies**:
   ```bash
   npm install
   ```

2. **Start the development server**:
   ```bash
   npm run dev
   ```
   *The app will be available at `http://localhost:5173`.*

3. **Type-check & Compile**:
   ```bash
   npm run type-check
   ```

4. **Lint**:
   ```bash
   npm run lint
   ```

5. **Build for production**:
   ```bash
   npm run build
   ```

## ⚠️ Known Limitations & TODOs
- **Lesson Reordering**: Currently, teachers must manually edit the "Order Number" of lessons to rearrange them. A bulk-reordering API endpoint is needed on the backend to implement smooth Drag-and-Drop functionality on the frontend.
- **Video Hosting**: The platform expects direct YouTube URLs or standard MP4 links. Advanced video player features (like HLS streaming) would require a dedicated video streaming server.
- **Statistics API**: The Admin and Teacher dashboards currently calculate some aggregate stats on the client-side. Dedicated `/api/stats` endpoints could improve performance for larger datasets.
