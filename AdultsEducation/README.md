📘 Документація проєкту

🎯 Предметна область: Online Learning Marketplace для дорослих

Платформа Adult Learning Academy — це онлайн-маркетплейс навчальних курсів, орієнтований на дорослу аудиторію (після 18). На відміну від дитячих платформ, тут: професійна спрямованість (програмування, маркетинг, бізнес, дизайн), самостійне навчання у зручному темпі, практична цінність (сертифікати, які можна показати роботодавцю).

Ключові сутності:

User — обліковий запис (email + пароль + роль)
StudentProfile / TeacherProfile — доменні профілі з ПІБ, bio, аватаркою
Course — курс з назвою, темою, форматом, ціною, тривалістю, обкладинкою, викладачем
Lesson — урок у курсі (title, content, video, порядковий номер)
Education — запис студента на курс (статус, прогрес, рівень складності)
LessonCompletion — факт здачі конкретного уроку студентом
Payment — оплата, прив'язана до Education
Review — відгук студента про курс (1-5 зірок)
Certificate — сертифікат про завершення курсу (HTML/PDF)
================================================================================

👥 Ролі та їхні можливості

🌍 GUEST (неавторизований) Користувач тільки досліджує платформу.

Сценарії:

Переглядає каталог курсів, фільтрує за темою/форматом/ціною/тривалістю, шукає за назвою
Відкриває картку курсу: бачить опис, викладача, ціну, тривалість, рейтинг, кількість студентів, список назв уроків без контенту (preview)
Читає відгуки про курс
Відкриває публічний профіль викладача (список курсів, bio, спеціалізація)
Перевіряє сертифікат за номером (наприклад, роботодавець, якому показали URL) — отримує JSON, красивий HTML або завантажує PDF
Може зареєструватися як STUDENT або TEACHER
Може увійти
Не може: записатися, бачити контент уроків, залишати відгуки.
---------------------------------------------------------------------------
🎓 STUDENT
Основний "споживач" платформи.

Сценарії:

Усе що GUEST +
Редагує свій профіль: ім'я, прізвище, телефон, bio, аватарка (upload image)
Змінює пароль (з перевіркою поточного)
Записується на курс: платний через CARD/BANK_TRANSFER (симуляція), безкоштовний — через FREE. При цьому в одній транзакції створюються Education (ACTIVE, 0%) + Payment (SUCCESS/FAILED). Якщо оплата впала — все rollback
Відкриває свої курси у "Мої навчання": бачить прогрес, статус, дату запису, курс, викладача
Вчиться: читає уроки, дивиться відео. Після завершення кожного тисне "Здати урок" — тоді створюється LessonCompletion, прогрес перераховується автоматично (зданих / всього × 100%)
Може відмітити урок як нездан��й (відкат)
Коли прогрес досягає 100% → автоматично: статус = COMPLETED, issueDate = сьогодні, визначається level за тривалістю курсу (<10h = BEGINNER, <40h = INTERMEDIATE, <100h = ADVANCED, ≥100h = EXPERT), автогенерується Certificate
Може форсувати завершення вручну (якщо вважає, що все зрозумів)
Переглядає свій сертифікат: красивий HTML у браузері або завантажує PDF
Ділиться посиланням з роботодавцем — той перевіряє автентичність через публічний verify
Залишає відгук про курс (рейтинг 1-5 + коментар) — тільки якщо записаний. Один відгук на курс
Видаляє свій відгук
Обмеження:

Не може створювати курси
Не може записатися на один курс двічі
Не може залишити два відгуки на один курс
Не бачить контент уроків курсу, на який не записаний
----------------------------------------------------------------------

👨‍🏫 TEACHER Автор контенту.

Сценарії:

Усе що STUDENT базове (профіль, пароль, аватарка) + додаткові поля specialization та experienceYears у профілі
Створює курс з назвою, описом, темою, форматом, ціною, тривалістю, обкладинкою
Редагує тільки свої курси
Додає, редагує, видаляє уроки в своїх курсах (title, content, videoUrl, orderNumber — унікальний у межах курсу)
Може сховати курс від публіки (visible=false) — існуючі записи продовжують діяти
Переглядає список своїх курсів
Переглядає всіх студентів, записаних на свої курси з фільтрацією
Відкриває конкретний запис студента: бачить прогрес, зданість уроків
Може додати коментар до запису студента (note), встановити level або status вручну (наприклад, якщо хоче видати сертифікат раніше)
Обмеження:

Не може записатися на власний курс
Не може редагувати чужі курси
Не може створити два курси з однаковою назвою в себе
----------------------------------------------------------------------


👑 ADMIN
Модератор платформи.

Сценарії:

Переглядає всіх користувачів, фільтрує за роллю, email, активністю
Блокує/розблоковує користувачів (active=false → не може логінитись)
Змінює роль користувача
Видаляє користувачів
Бачить всі курси включно з прихованими
Може будь-який курс сховати/показати
Може видалити будь-який курс
Бачить всі відгуки включно з прихованими
Модерує відгуки: приховує токсичні/спам
Видаляє будь-які відгуки
Моніторить всі навчання на платформі
Має повний доступ до всіх endpoint-ів
=====================================================================

🔐 Аутентифікація
JWT-based. При login/register сервер повертає токен, який діє 24 години. Клієнт мусить передавати його в заголовку:



Authorization: Bearer <token>
=====================================================================

📡 REST API — повна специфікація
Базовий URL: http://localhost:8080

Загальні моменти
Формат помилки
json


{
  "status": 404,
  "error": "Not Found",
  "message": "Курс не знайдено",
  "timestamp": "2025-05-04T12:34:56"
}
Коди статусів:

200 OK — успіх
201 Created — створено ресурс
204 No Content — видалено / дія без відповіді
400 Bad Request — валідація не пройшла або бізнес-помилка
401 Unauthorized — токена нема або протухший
403 Forbidden — роль не дозволяє
404 Not Found
409 Conflict — дублікат (email, title, order)
500 Internal Server Error
Пагінація
На endpoint-ах, що повертають Page<T>, використовуй query-параметри:

page — номер сторінки (0-based), default 0
size — розмір, default 10
sort — поле і напрямок: sort=id,desc або sort=price,asc
Відповідь:

json
{
  "content": [ ... ],
  "totalElements": 42,
  "totalPages": 5,
  "number": 0,
  "size": 10,
  "first": true,
  "last": false
}
i18n
Сервер повертає повідомлення українською за замовчуванням. Щоб отримати англійську — заголовок Accept-Language: en.

=====================================================================

🔑 AUTH
POST /api/auth/register
Публічний. Реєстрація.

Request:

json
{
  "email": "user@mail.com",
  "password": "secret123",
  "firstName": "Марія",
  "lastName": "Коваль",
  "phone": "+380501234567",
  "avatarUrl": "http://localhost:8080/uploads/avatars/xxx.jpg",
  "role": "STUDENT"
}
email — required, email format
password — 6-100 chars
firstName, lastName — required, ≤50 chars
phone — optional, ≤20 chars
avatarUrl — optional
role — optional (STUDENT або TEACHER). Default STUDENT. ADMIN заборонено
-------------------------------------------------------------------

Response 201:

json
{
  "token": "eyJhbGci...",
  "email": "user@mail.com",
  "role": "STUDENT",
  "userId": 7,
  "firstName": "Марія",
  "lastName": "Коваль"
}
----------------------------------------------------------------------


POST /api/auth/login
Публічний.

Request:

json
{"email": "user@mail.com", "password": "secret123"}
Response 200 — такий самий AuthResponse.

----------------------------------------------------------------------


👤 ME
Усі endpoints — isAuthenticated().

GET /api/me
Мій профіль.

Response 200:

json


{
  "id": 7,
  "email": "user@mail.com",
  "role": "STUDENT",
  "active": true,
  "firstName": "Марія",
  "lastName": "Коваль",
  "fullName": "Коваль Марія",
  "phone": "+380501234567",
  "bio": "Я активно навчаюсь!",
  "avatarUrl": "http://...",
  "specialization": null,
  "experienceYears": null,
  "createdAt": "2025-04-01T10:00:00"
}
specialization та experienceYears заповнені тільки для TEACHER.

PUT /api/me
Оновити профіль.

Request (усі поля optional):

json


{
  "firstName": "Марія",
  "lastName": "Коваль",
  "phone": "+380501234567",
  "bio": "Текст про мене",
  "avatarUrl": "http://...",
  "specialization": "Web-design",
  "experienceYears": 5
}
Response 200 — UserResponse.

PUT /api/me/password
Зміна паролю.

Request:

json


{"currentPassword": "old", "newPassword": "new123"}
Response 204.

📁 FILES
Upload файлів. Потрібен isAuthenticated(). Обмеження: JPEG/PNG/WebP, ≤5MB (або скільки задано в конфізі).

POST /api/files/avatars
multipart/form-data, поле file.

Response 200:

json


{"url": "http://localhost:8080/uploads/avatars/uuid.jpg"}
Цей URL зберігаєш у профілі через PUT /api/me.

POST /api/files/courses
Тільки TEACHER/ADMIN. Аналогічно, але зберігається в /uploads/courses/. Використовується як coverUrl курсу.

📚 COURSES
POST /api/courses
Тільки TEACHER/ADMIN. Створити курс.

Request:

json


{
  "title": "Java з нуля",
  "description": "Повний курс Java",
  "topic": "PROGRAMMING",
  "format": "ONLINE",
  "price": 2500.00,
  "durationHours": 60,
  "coverUrl": "http://..."
}
Enums:

topic: PROGRAMMING, DESIGN, MARKETING, BUSINESS, LANGUAGES, SCIENCE, ARTS, HEALTH, FINANCE, OTHER
format: ONLINE, OFFLINE, HYBRID, SELF_PACED
Response 201 — CourseResponse (див. нижче).

GET /api/courses/{id}
Публічний. Отримати курс.

Response 200:

json


{
  "id": 1,
  "title": "Java з нуля",
  "description": "...",
  "topic": "PROGRAMMING",
  "format": "ONLINE",
  "price": 2500.00,
  "durationHours": 60,
  "teacherId": 3,
  "teacherFullName": "Петров Іван",
  "lessonsCount": 12,
  "studentsCount": 45,
  "averageRating": 4.7,
  "reviewsCount": 23,
  "visible": true,
  "createdAt": "...",
  "updatedAt": "...",
  "coverUrl": "http://..."
}
Прихований курс (visible=false) віддається: ADMIN, власнику-teacher. Для інших — 404.

GET /api/courses
Публічний. Список публічних курсів із фільтрами.

Query:

title — like search
topic, format — enums
minPrice, maxPrice — BigDecimal
minHours, maxHours — int
teacherId — long
page, size, sort
Response 200 — Page<CourseResponse>.

GET /api/courses/admin
Тільки ADMIN. Той самий формат але включно з visible=false.

GET /api/courses/my
Тільки TEACHER. Мої курси (фільтр teacherId автоматично). Той самий формат.

PUT /api/courses/{id}
TEACHER (owner) або ADMIN. Body той самий що при POST. Response — CourseResponse.

DELETE /api/courses/{id}
TEACHER (owner) або ADMIN. Response 204.

PATCH /api/courses/{id}/visibility?visible=false
TEACHER (owner) або ADMIN. Query visible (bool). Response — CourseResponse.

📖 LESSONS
POST /api/lessons
Тільки TEACHER (owner of course) / ADMIN.

Request:

json


{
  "title": "Вступ",
  "content": "Повний текст уроку...",
  "videoUrl": "https://youtu.be/xxx",
  "orderNumber": 1,
  "courseId": 5
}
Унікальна пара (courseId, orderNumber).

Response 201:

json


{
  "id": 10,
  "title": "Вступ",
  "content": "...",
  "videoUrl": "...",
  "orderNumber": 1,
  "courseId": 5
}
GET /api/lessons/{id}
isAuthenticated(). Отримати урок з повним контентом. Access: ADMIN, власник курсу, або записаний студент (ACTIVE/COMPLETED). Інакше 400 з "не записаний".

Response — LessonResponse.

GET /api/lessons/by-course/{courseId}/preview
Публічний. Список уроків без контенту.

Response:

json


[
  {"id": 10, "title": "Вступ", "orderNumber": 1},
  {"id": 11, "title": "Змінні", "orderNumber": 2}
]
GET /api/lessons/by-course/{courseId}/full
isAuthenticated(). Список уроків з повним контентом (тільки для записаних / викладача / ADMIN).

Response — List<LessonResponse>.

PUT /api/lessons/{id}
TEACHER (owner) / ADMIN. Same body as POST. courseId в request має співпадати з поточним (переносити не можна).

DELETE /api/lessons/{id}
TEACHER (owner) / ADMIN. Response 204.

🎫 ENROLLMENTS
POST /api/enrollments
Тільки STUDENT. Записатись на курс (Unit of Work: Education + Payment).

Request:

json


{
  "courseId": 5,
  "paymentMethod": "CARD",
  "transactionRef": "**** 4242"
}
paymentMethod: CARD, BANK_TRANSFER, FREE
Для безкоштовного курсу — FREE, інакше 400
Для платного — CARD/BANK_TRANSFER
transactionRef — optional опис платежу
Response 201:

json


{
  "education": {
    "id": 20, "studentId": 7, "studentFullName": "Коваль Марія",
    "studentAvatarUrl": "...",
    "courseId": 5, "courseTitle": "Java з нуля", "courseCoverUrl": "...",
    "status": "ACTIVE",
    "enrolledDate": "2025-05-04", "issueDate": null,
    "level": null, "progress": 0, "note": null
  },
  "payment": {
    "id": 15, "educationId": 20, "amount": 2500.00,
    "status": "SUCCESS", "method": "CARD",
    "transactionRef": "**** 4242",
    "createdAt": "2025-05-04T12:34:56"
  }
}
Можливі помилки:

409 — вже записані
400 — "не можна на свій курс", "неправильний метод оплати", "курс прихований"
🎓 EDUCATIONS
Усі — isAuthenticated().

GET /api/educations/{id}
Повертає запис для: студента-власника, викладача курсу, ADMIN.

Response — EducationResponse:

json


{
  "id": 20,
  "studentId": 7, "studentFullName": "Коваль Марія", "studentAvatarUrl": "...",
  "courseId": 5, "courseTitle": "Java з нуля", "courseCoverUrl": "...",
  "status": "ACTIVE",
  "enrolledDate": "2025-05-04",
  "issueDate": null,
  "level": null,
  "progress": 40,
  "note": "Відмінний студент"
}
status: PENDING, ACTIVE, COMPLETED, CANCELLED level: BEGINNER, INTERMEDIATE, ADVANCED, EXPERT (може бути null)

GET /api/educations
Тільки ADMIN. Усі записи.

Query: studentId, courseId, teacherId, status + pagination.

GET /api/educations/my-as-student
Тільки STUDENT. Мої навчання. Query: courseId, status + pagination.

GET /api/educations/my-as-teacher
Тільки TEACHER. Записи студентів на моїх курсах. Query: studentId, courseId, status + pagination.

PATCH /api/educations/{id}/progress
Тільки STUDENT-owner. Ручне встановлення прогресу (альтернатива до здачі окремих уроків).

Request:

json


{"progress": 50}
Прогрес 0-100. Статус має бути ACTIVE.

POST /api/educations/{id}/complete
Тільки STUDENT-owner. Форсоване завершення. Встановлює status=COMPLETED, progress=100, issueDate, level. Не генерує сертифікат автоматично (треба через /lessons-complete доходити до 100%). Якщо хочеш, щоб форсоване теж генерувало — додай certificateService.issueCertificate(e) у EducationServiceImpl.completeCourse.

PATCH /api/educations/{id}/teacher
Тільки TEACHER (owner of course) / ADMIN.

Request (усі optional):

json


{
  "status": "COMPLETED",
  "level": "ADVANCED",
  "note": "Прекрасно виконаний фінальний проєкт"
}
POST /api/educations/{id}/lessons/{lessonId}/complete
Тільки STUDENT-owner. "Здати урок".

Logic:

Створюється LessonCompletion (idempotent — повторно не дублюється)
Прогрес перераховується: (done/total) * 100
Якщо 100% → статус стає COMPLETED, створюється Certificate
Response — оновлений EducationResponse.

DELETE /api/educations/{id}/lessons/{lessonId}/complete
Відкат здачі. Прогрес перераховується. Може перевести зі COMPLETED назад? — НІ, бо умова статусу ACTIVE перевіряється. Цей endpoint працює тільки поки курс ACTIVE.

GET /api/educations/{id}/completed-lessons
Список ID зданих уроків. Зручно для фронта, щоб показати галочки.

Response:

json


[10, 11, 13]
⭐ REVIEWS
POST /api/reviews
Тільки STUDENT + записаний на курс.

Request:

json


{
  "courseId": 5,
  "rating": 5,
  "comment": "Чудовий курс!"
}
rating: 1-5
comment — optional, ≤2000
Response 201:

json


{
  "id": 30, "courseId": 5, "courseTitle": "Java з нуля",
  "studentId": 7, "studentFullName": "Коваль Марія",
  "rating": 5, "comment": "Чудовий курс!",
  "visible": true,
  "createdAt": "2025-05-04T13:00:00"
}
GET /api/reviews/{id}
Публічний. Один відгук.

GET /api/reviews
Публічний. Список. Query: courseId, studentId, minRating, maxRating + pagination. Повертає тільки visible=true.

GET /api/reviews/admin
Тільки ADMIN. Усі включно з прихованими.

DELETE /api/reviews/{id}
STUDENT-author / ADMIN.

PATCH /api/reviews/{id}/visibility?visible=false
Тільки ADMIN. Модерація.

👥 USERS (ADMIN)
Усі — тільки ADMIN.

GET /api/users/{id}, GET /api/users, PUT /api/users/{id}, DELETE /api/users/{id}
UserFilter: email, role, active + pagination.

UpdateUserRequest:

json


{
  "email": "new@mail.com",
  "firstName": "...", "lastName": "...", "phone": "...", "bio": "...",
  "avatarUrl": "...",
  "specialization": "...", "experienceYears": 3,
  "role": "TEACHER",
  "active": false
}
🏆 CERTIFICATES
GET /api/certificates/verify/{number}
Публічний. JSON-дані сертифіката.

Response:

json


{
  "id": 5,
  "certificateNumber": "CERT-B5007EE429F3",
  "issueDate": "2025-05-04",
  "studentName": "Коваль Марія",
  "courseTitle": "Основи Figma",
  "teacherName": "Шевченко Олена",
  "durationHours": 8,
  "educationId": 20,
  "courseId": 3,
  "createdAt": "2025-05-04T13:00:00"
}
GET /api/certificates/verify/{number}/view
Публічний. HTML (повна HTML-сторінка, готова до друку). Content-Type: text/html; charset=UTF-8.

GET /api/certificates/verify/{number}/pdf
Публічний. PDF-файл. Content-Disposition: attachment.

GET /api/certificates/by-education/{educationId}
isAuthenticated(). Сертифікат по запису. Доступно: студенту-власнику, викладачу курсу, ADMIN.

Response — CertificateResponse (той самий що verify).
====================================================================================


🎨 Приклади сценаріїв (для UX-дизайнера)

Сценарій 1. Студент знаходить курс і записується

Головна → каталог курсів → фільтри/пошук
Клік на картку → картка курсу (опис, викладач, рейтинг, уроки-preview, відгуки)
Клік "Записатися" → модалка з паролем → якщо гість, редірект на реєстрацію
Після логіну → модалка оплати → вибір картки → успіх
Редірект до /my-courses → курс з прогресом 0%
----------------------------------------------------------------------

Сценарій 2. Студент проходить курс

/my-courses → клік на курс
Сторінка курсу в режимі навчання: список уроків (галочки на зданих з /completed-lessons), показує який наступний
Клік на урок → плеєр + текст + кнопка "Здати урок"
Після натискання → progress bar наверху оновлюється
Після останнього → toast "Вітаємо! Ви завершили курс. Сертифікат готовий" → модалка з сертифікатом
Клік "Скачати PDF" → download
----------------------------------------------------------------------

Сценарій 3. Викладач створює курс

/dashboard → "Мої курси" → "+ Новий курс"
Форма: назва, опис, тема, формат, ціна, тривалість, завантажити обкладинку
Submit → редірект до сторінки курсу (поки прихований)
"Додати уроки" → форма уроку → ще, ще, ще
"Опублікувати" → PATCH visibility=true → курс у публічному каталозі
---------------------------------------------------------------------

Сценарій 4. Роботодавець перевіряє сертифікат

Студент дає URL з номером: yoursite.com/cert/CERT-B5007EE429F3
Фронт: GET verify → показує картку "Сертифікат автентичний"
Кнопка "Переглянути" → iframe з HTML або PDF
----------------------------------------------------------------------

Сценарій 5. Адмін модерує
/admin/reviews → бачить нові відгуки
Клік "приховати" на образливий → PATCH visibility=false
Публічні користувачі більше не бачать цей відгук
=======================================================================

🏗️ Технічний стек для фронту
Фронт може бути React/Vue/Angular/Next — backend стандартний REST+JSON
CORS дозволяє http://localhost:5173 (Vite) та http://localhost:3000 (CRA/Next)
Auth: JWT у localStorage, Axios interceptor додає Authorization: Bearer
На 401 — редірект на login
На 403 — показ "немає прав"
На полях з файлами — multipart/form-data через FormData
Роутинг: /, /courses, /courses/:id, /login, /register, /my-courses, /my-courses/:id, /dashboard (teacher), /admin/*, /profile, /cert/:number
Role-based guards на клієнті + незалежний захист від сервера
=========================================================================

📦 Всі DTO — одним списком (для швидкої генерації TypeScript-типів)

// Enums
type Role = 'STUDENT' | 'TEACHER' | 'ADMIN';
type CourseTopic = 'PROGRAMMING' | 'DESIGN' | 'MARKETING' | 'BUSINESS' | 
                   'LANGUAGES' | 'SCIENCE' | 'ARTS' | 'HEALTH' | 'FINANCE' | 'OTHER';
type CourseFormat = 'ONLINE' | 'OFFLINE' | 'HYBRID' | 'SELF_PACED';
type EducationStatus = 'PENDING' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED';
type EducationLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT';
type PaymentMethod = 'CARD' | 'BANK_TRANSFER' | 'FREE';
type PaymentStatus = 'PENDING' | 'SUCCESS' | 'FAILED' | 'REFUNDED';

interface AuthRequest { email: string; password: string; }
interface RegisterRequest {
  email: string; password: string;
  firstName: string; lastName: string;
  phone?: string; avatarUrl?: string;
  role?: Role;
}
interface AuthResponse {
  token: string; email: string; role: string; userId: number;
  firstName?: string; lastName?: string;
}

interface UserResponse {
  id: number; email: string; role: Role; active: boolean;
  firstName?: string; lastName?: string; fullName?: string;
  phone?: string; bio?: string; avatarUrl?: string;
  specialization?: string; experienceYears?: number;
  createdAt: string;
}
interface UpdateMeRequest {
  firstName?: string; lastName?: string; phone?: string; bio?: string;
  avatarUrl?: string; specialization?: string; experienceYears?: number;
}
interface ChangePasswordRequest { currentPassword: string; newPassword: string; }
interface UpdateUserRequest extends UpdateMeRequest { email?: string; role?: Role; active?: boolean; }
interface UserFilter { email?: string; role?: Role; active?: boolean; }

interface CourseRequest {
  title: string; description: string;
  topic: CourseTopic; format: CourseFormat;
  price: number; durationHours: number;
  coverUrl?: string;
}
interface CourseResponse extends CourseRequest {
  id: number; teacherId: number; teacherFullName: string;
  lessonsCount: number; studentsCount: number;
  averageRating: number; reviewsCount: number;
  visible: boolean; createdAt: string; updatedAt: string;
}
interface CourseFilter {
  title?: string; topic?: CourseTopic; format?: CourseFormat;
  minPrice?: number; maxPrice?: number;
  minHours?: number; maxHours?: number;
  teacherId?: number;
}

interface LessonRequest {
  title: string; content?: string; videoUrl?: string;
  orderNumber: number; courseId: number;
}
interface LessonResponse extends LessonRequest { id: number; }
interface LessonPreviewResponse { id: number; title: string; orderNumber: number; }

interface EnrollmentRequest { courseId: number; paymentMethod: PaymentMethod; transactionRef?: string; }
interface EducationResponse {
  id: number;
  studentId: number; studentFullName: string; studentAvatarUrl?: string;
  courseId: number; courseTitle: string; courseCoverUrl?: string;
  status: EducationStatus;
  enrolledDate: string; issueDate?: string;
  level?: EducationLevel; progress: number; note?: string;
}
interface PaymentResponse {
  id: number; educationId: number; amount: number;
  status: PaymentStatus; method: PaymentMethod;
  transactionRef?: string; createdAt: string;
}
interface EnrollmentResponse { education: EducationResponse; payment: PaymentResponse; }
interface EducationFilter { studentId?: number; courseId?: number; teacherId?: number; status?: EducationStatus; }
interface UpdateProgressRequest { progress: number; }
interface UpdateEducationByTeacherRequest { status?: EducationStatus; level?: EducationLevel; note?: string; }

interface ReviewRequest { courseId: number; rating: number; comment?: string; }
interface ReviewResponse {
  id: number; courseId: number; courseTitle: string;
  studentId: number; studentFullName: string;
  rating: number; comment?: string; visible: boolean;
  createdAt: string;
}
interface ReviewFilter { courseId?: number; studentId?: number; minRating?: number; maxRating?: number; }

interface CertificateResponse {
  id: number; certificateNumber: string; issueDate: string;
  studentName: string; courseTitle: string; teacherName: string;
  durationHours: number;
  educationId: number; courseId: number;
  createdAt: string;
}

interface FileUploadResponse { url: string; }

interface Page<T> {
  content: T[]; totalElements: number; totalPages: number;
  number: number; size: number; first: boolean; last: boolean;
}

interface ApiError { status: number; error: string; message: string; timestamp: string; }
===============================================

📋 Повний список endpoint-ів (коротко)
| Method | Endpoint | Auth | Опис |
| :-- | :-- | :-- | :-- |
| POST | /api/auth/register | 🌍 | Реєстрація |
| POST | /api/auth/login | 🌍 | Логін |
| GET | /api/me | 🔐 | Мій профіль |
| PUT | /api/me | 🔐 | Оновити профіль |
| PUT | /api/me/password | 🔐 | Змінити пароль |
| POST | /api/files/avatars | 🔐 | Upload аватарки |
| POST | /api/files/courses | 👨‍🏫👑 | Upload обкладинки |
| POST | /api/courses | 👨‍🏫👑 | Створити курс |
| GET | /api/courses | 🌍 | Список курсів |
| GET | /api/courses/{id} | 🌍 | Курс по id |
| GET | /api/courses/my | 👨‍🏫 | Мої курси |
| GET | /api/courses/admin | 👑 | Всі курси |
| PUT | /api/courses/{id} | 👨‍🏫👑 | Оновити |
| DELETE | /api/courses/{id} | 👨‍🏫👑 | Видалити |
| PATCH | /api/courses/{id}/visibility | 👨‍🏫👑 | Показ/прих |
| POST | /api/lessons | 👨‍🏫👑 | Створити урок |
| GET | /api/lessons/{id} | 🔐 | Урок (повний) |
| GET | /api/lessons/by-course/{courseId}/preview | 🌍 | Preview уроків |
| GET | /api/lessons/by-course/{courseId}/full | 🔐 | Повні уроки |
| PUT | /api/lessons/{id} | 👨‍🏫👑 | Оновити урок |
| DELETE | /api/lessons/{id} | 👨‍🏫👑 | Видалити урок |
| POST | /api/enrollments | 🎓 | Записатися на курс |
| GET | /api/educations/{id} | 🔐 | Один запис |
| GET | /api/educations | 👑 | Всі записи |
| GET | /api/educations/my-as-student | 🎓 | Мої як студент |
| GET | /api/educations/my-as-teacher | 👨‍🏫 | Мої як викладач |
| PATCH | /api/educations/{id}/progress | 🎓 | Встановити прогрес |
| POST | /api/educations/{id}/complete | 🎓 | Форс завершення |
| PATCH | /api/educations/{id}/teacher | 👨‍🏫👑 | Teacher edit |
| POST | /api/educations/{id}/lessons/{lessonId}/complete | 🎓 | Здати урок |
| DELETE | /api/educations/{id}/lessons/{lessonId}/complete | 🎓 | Скасувати здачу |
| GET | /api/educations/{id}/completed-lessons | 🔐 | Список зданих |
| POST | /api/reviews | 🎓 | Лишити відгук |
| GET | /api/reviews/{id} | 🌍 | Один відгук |
| GET | /api/reviews | 🌍 | Список відгуків |
| GET | /api/reviews/admin | 👑 | Всі відгуки |
| DELETE | /api/reviews/{id} | 🎓👑 | Видалити |
| PATCH | /api/reviews/{id}/visibility | 👑 | Модерація |
| GET | /api/users/{id} | 👑 | Користувач |
| GET | /api/users | 👑 | Користувачі |
| PUT | /api/users/{id} | 👑 | Оновити |
| DELETE | /api/users/{id} | 👑 | Видалити |
| GET | /api/certificates/verify/{number} | 🌍 | Перевірити |
| GET | /api/certificates/verify/{number}/view | 🌍 | HTML |
| GET | /api/certificates/verify/{number}/pdf | 🌍 | PDF |
| GET | /api/certificates/by-education/{educationId} | 🔐 | Мій сертифікат |