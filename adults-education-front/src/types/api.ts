// ── Enums ─────────────────────────────────────────────────────────────
export type Role = 'STUDENT' | 'TEACHER' | 'ADMIN'

export type CourseTopic =
  | 'PROGRAMMING'
  | 'DESIGN'
  | 'MARKETING'
  | 'BUSINESS'
  | 'LANGUAGES'
  | 'SCIENCE'
  | 'ARTS'
  | 'HEALTH'
  | 'FINANCE'
  | 'OTHER'

export type CourseFormat = 'ONLINE' | 'OFFLINE' | 'HYBRID' | 'SELF_PACED'

export type EducationStatus = 'PENDING' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED'

export type EducationLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT'

export type PaymentMethod = 'CARD' | 'BANK_TRANSFER' | 'FREE'

export type PaymentStatus = 'PENDING' | 'SUCCESS' | 'FAILED' | 'REFUNDED'

// ── Auth ──────────────────────────────────────────────────────────────
export interface AuthRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  email: string
  password: string
  firstName: string
  lastName: string
  phone?: string
  avatarUrl?: string
  birthDate: string
  role?: Role
}

export interface AuthResponse {
  token: string
  email: string
  role: Role
  userId: number
  firstName?: string
  lastName?: string
}

// ── User ──────────────────────────────────────────────────────────────
export interface UserResponse {
  id: number
  email: string
  role: Role
  active: boolean
  firstName?: string
  lastName?: string
  fullName?: string
  phone?: string
  bio?: string
  avatarUrl?: string
  birthDate?: string
  specialization?: string
  experienceYears?: number
  createdAt: string
}

export interface UpdateMeRequest {
  firstName?: string
  lastName?: string
  phone?: string
  bio?: string
  avatarUrl?: string
  birthDate?: string
  specialization?: string
  experienceYears?: number
}

export interface ChangePasswordRequest {
  currentPassword: string
  newPassword: string
}

export interface UpdateUserRequest extends UpdateMeRequest {
  email?: string
  role?: Role
  active?: boolean
}

export interface UserFilter {
  email?: string
  role?: Role
  active?: boolean
}

// ── Courses ───────────────────────────────────────────────────────────
export interface CourseRequest {
  title: string
  description: string
  topic: CourseTopic
  format: CourseFormat
  price: number
  durationHours: number
  coverUrl?: string
  teacherId?: number
}

export interface CourseResponse {
  id: number
  title: string
  description: string
  topic: CourseTopic
  format: CourseFormat
  price: number
  durationHours: number
  coverUrl?: string
  teacherId: number
  teacherFullName: string
  teacherAvatarUrl?: string
  lessonsCount: number
  studentsCount: number
  averageRating: number
  reviewsCount: number
  visible: boolean
  createdAt: string
  updatedAt: string
}

export interface CourseFilter {
  title?: string
  topic?: CourseTopic
  format?: CourseFormat
  minPrice?: number
  maxPrice?: number
  minHours?: number
  maxHours?: number
  teacherId?: number
}

// ── Lessons ───────────────────────────────────────────────────────────
export interface LessonRequest {
  title: string
  content?: string
  videoUrl?: string
  orderNumber: number
  courseId: number
}

export interface LessonResponse {
  id: number
  title: string
  content?: string
  videoUrl?: string
  orderNumber: number
  courseId: number
}

export interface LessonPreviewResponse {
  id: number
  title: string
  orderNumber: number
}

// ── Enrollments & Education ───────────────────────────────────────────
export interface EnrollmentRequest {
  courseId: number
  paymentMethod: PaymentMethod
  transactionRef?: string
  studentId?: number
}

export interface EducationResponse {
  id: number
  studentId: number
  studentFullName: string
  studentAvatarUrl?: string
  courseId: number
  courseTitle: string
  courseCoverUrl?: string
  status: EducationStatus
  enrolledDate: string
  issueDate?: string
  level?: EducationLevel
  progress: number
  note?: string
}

export interface PaymentResponse {
  id: number
  educationId: number
  amount: number
  status: PaymentStatus
  method: PaymentMethod
  transactionRef?: string
  createdAt: string
}

export interface EnrollmentResponse {
  education: EducationResponse
  payment: PaymentResponse
}

export interface EducationFilter {
  studentId?: number
  courseId?: number
  teacherId?: number
  status?: EducationStatus
}

export interface UpdateProgressRequest {
  progress: number
}

export interface UpdateEducationByTeacherRequest {
  status?: EducationStatus
  level?: EducationLevel
  note?: string
}

// ── Reviews ───────────────────────────────────────────────────────────
export interface ReviewRequest {
  courseId: number
  rating: number
  comment?: string
  studentId?: number
}

export interface ReviewResponse {
  id: number
  courseId: number
  courseTitle: string
  studentId: number
  studentFullName: string
  rating: number
  comment?: string
  visible: boolean
  createdAt: string
}

export interface ReviewFilter {
  courseId?: number
  studentId?: number
  minRating?: number
  maxRating?: number
}

// ── Certificates ──────────────────────────────────────────────────────
export interface CertificateResponse {
  id: number
  certificateNumber: string
  issueDate: string
  studentName: string
  courseTitle: string
  teacherName: string
  durationHours: number
  educationId: number
  courseId: number
  createdAt: string
}

// ── Files ─────────────────────────────────────────────────────────────
export interface FileUploadResponse {
  url: string
}

// ── Pagination ────────────────────────────────────────────────────────
export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
  first: boolean
  last: boolean
}

// ── Error ─────────────────────────────────────────────────────────────
export interface ApiError {
  status: number
  error: string
  message: string
  timestamp: string
}
