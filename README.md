# Campusly — Academic Planner & Campus Community App

**Campusly** is a modern Android application designed for university students to manage coursework, personal study plans, campus broadcast forums, peer-to-peer marketplace listings, and academic schedules.

---

## 🌟 Key Features

### 🚀 Updated Live Home Dashboard
- **Top Priority Card**: Automatically ranks and displays the single most urgent open task, assignment, exam, or upcoming class based on real-time urgency and deadlines.
- **Dynamic "Due Soon" Countdowns**: Calculates remaining time in days, hours, or minutes for assignments due within 24 hours.
- **Automatic Expired Item Clearing**: Expired tasks, finished assignment deadlines, and past exams automatically clear from active Home cards without manual deletion, while remaining preserved in the **Planner** tab for historical records.
- **Weekly Class Recurrence**: Finished class occurrences for today clear automatically, and the next occurrence for the weekly schedule is recalculated for the following week.
- **Shared Focus Study Timer**: Displays a live countdown card on Home when a study timer is running. Opening or closing the study timer dialog shares state and never resets an active session.

### 🔐 Institutional `.ac.ke` Authentication
- **Strict Institutional Domain Check**: Registration, sign-in, and password recovery are restricted to verified institutional email addresses ending in `.ac.ke` (e.g., `@usiu.ac.ke`, `@students.uonbi.ac.ke`). Personal emails (e.g., `@gmail.com`) and lookalike domains (e.g., `usiu.ac.ke.example.com`) are rejected.
- **Verified Email Password Recovery**: Password recovery triggers a verified email link to the user's registered mailbox. Knowing an email address alone cannot reset a password or open protected screens.
- **Blank Password Bypass Guard**: Prevents legacy session bypasses and requires complete credentials.
- **No Pre-populated Demo Account**: Fresh installs start with a clean database. User profiles use exact names and credentials entered during registration.

### 🛡️ Admin Moderation Review
- **Trusted Backend Role Provisioning**: Admin privileges are managed through backend configuration (`CampuslyBackendConfig`). Hardcoded client-side admin lists and role toggles have been removed.
- **Discover → Admin Review 🛡️**: Visible exclusively to provisioned admin account IDs. Admins can review, approve, or reject student-submitted events, career opportunities, and study groups.

### 🎴 Live AI Flashcards & Scoped Storage
- **Live AI Generation**: Connects to an authenticated HTTPS endpoint to generate structured question-and-answer flashcard decks from arbitrary topics or selected notes.
- **Database-Level Ownership Scoping**:
  - Single-card edits and deletes are scoped by `(id, userEmail)` at database write time.
  - Whole-deck deletion is scoped by `(userEmail, deckName)`, ensuring that a deck deleted by one student leaves another student's deck with the same name intact.

### 💬 Cross-Device Sharing & Moderation
- **Forum & Marketplace**: Broadcast campus posts, replies, and peer-to-peer buy/sell listings.
- **Private Conversations**: Private message threads are accessible strictly to conversation participants.
- **Moderated Submissions**: Pending or rejected events and study groups are visible only to the author and admins. Approved items appear in shared student feeds.

---

## 🛠️ Setup & Installation Instructions

### Prerequisites
- **Android Studio**: Android Studio Ladybug (2024.2) or higher
- **JDK**: JDK 17 or higher
- **Android SDK**: `compileSdk = 37`, `minSdk = 24`, `targetSdk = 37`

### Building & Running the Project

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/AkhwaleLucy/Campusly-App.git
   cd Campusly-App
   git checkout campusly-fixes-review
   ```

2. **Open in Android Studio**:
   - Open Android Studio, select **File > Open**, and navigate to the `Campusly-App` directory.
   - Allow Gradle to sync dependencies.

3. **Build the Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run Unit Tests**:
   ```bash
   ./gradlew test
   ```

---

## 🔑 Backend Configuration & Environment Variables

### Admin Provisioning Steps
To grant an account admin privileges:
1. Register an account with your institutional `.ac.ke` email (e.g. `student@usiu.ac.ke`).
2. Obtain your verified account ID (`UserEntity.id`).
3. Provision the ID in your backend response or in `CampuslyBackendConfig.kt`:
   ```kotlin
   CampuslyBackendConfig.configureTrustedAdminAccounts(
       adminIds = setOf("<YOUR_ACCOUNT_ID>")
   )
   ```

### AI Endpoint Configuration
To configure an external live AI model endpoint, set environment variables or invoke `CampuslyAiService.configureBackend()`:
- `CAMPUSLY_AI_ENDPOINT` = `https://your-api-server.com/api/ai/flashcards`
- `CAMPUSLY_AI_KEY` = `YOUR_SERVER_SECRET_KEY`

---

## 🧪 Verification & Test Results

- **Build Verification**: `./gradlew assembleDebug` passed (`BUILD SUCCESSFUL`).
- **Domain Security**: Tested `.ac.ke` institutional email acceptance and rejection of personal/lookalike domains.
- **Task CRUD**: Verified scoped task insertion, editing, completion toggling, and deletion per user account.
- **Home Dashboard**: Verified Top Priority ranking, dynamic Due Soon countdowns, and automatic clearing of expired items.
