package com.example.gossipapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gossipapp.data.local.dao.CampuslyDao
import com.example.gossipapp.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        CourseEntity::class,
        TimetableEntity::class,
        AssignmentEntity::class,
        ExamEntity::class,
        TaskEntity::class,
        NoteEntity::class,
        StudySessionEntity::class,
        FlashcardEntity::class,
        GpaRecordEntity::class,
        EventEntity::class,
        OpportunityEntity::class,
        StudyGroupEntity::class,
        MarketplaceListingEntity::class,
        BudgetEntity::class,
        SavingsGoalEntity::class,
        AttendanceRecordEntity::class,
        ForumMessageEntity::class,
        ForumReplyEntity::class,
        PrivateConversationEntity::class,
        PrivateMessageEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class CampuslyDatabase : RoomDatabase() {

    abstract fun dao(): CampuslyDao

    companion object {
        @Volatile
        private var INSTANCE: CampuslyDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `forum_messages` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `senderName` TEXT NOT NULL,
                        `senderEmail` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `message` TEXT NOT NULL,
                        `timestampMillis` INTEGER NOT NULL,
                        `likesCount` INTEGER NOT NULL,
                        `isLiked` INTEGER NOT NULL,
                        `isAnonymous` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `users` ADD COLUMN `isAdmin` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `timetable` ADD COLUMN `userEmail` TEXT NOT NULL DEFAULT 'student@campusly.app'")
                db.execSQL("ALTER TABLE `assignments` ADD COLUMN `userEmail` TEXT NOT NULL DEFAULT 'student@campusly.app'")
                db.execSQL("ALTER TABLE `exams` ADD COLUMN `userEmail` TEXT NOT NULL DEFAULT 'student@campusly.app'")
                db.execSQL("ALTER TABLE `tasks` ADD COLUMN `userEmail` TEXT NOT NULL DEFAULT 'student@campusly.app'")
                db.execSQL("ALTER TABLE `tasks` ADD COLUMN `subject` TEXT NOT NULL DEFAULT 'General'")
                db.execSQL("ALTER TABLE `tasks` ADD COLUMN `dateString` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `tasks` ADD COLUMN `timeString` TEXT NOT NULL DEFAULT '09:00'")
                db.execSQL("ALTER TABLE `tasks` ADD COLUMN `durationMinutes` INTEGER NOT NULL DEFAULT 60")
                db.execSQL("ALTER TABLE `tasks` ADD COLUMN `isSuggested` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `notes` ADD COLUMN `userEmail` TEXT NOT NULL DEFAULT 'student@campusly.app'")
                db.execSQL("ALTER TABLE `study_sessions` ADD COLUMN `sessionId` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `study_sessions` ADD COLUMN `userEmail` TEXT NOT NULL DEFAULT 'student@campusly.app'")
                db.execSQL("ALTER TABLE `study_sessions` ADD COLUMN `isBreak` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `flashcards` ADD COLUMN `userEmail` TEXT NOT NULL DEFAULT 'student@campusly.app'")
                db.execSQL("ALTER TABLE `gpa_records` ADD COLUMN `userEmail` TEXT NOT NULL DEFAULT 'student@campusly.app'")
                db.execSQL("ALTER TABLE `events` ADD COLUMN `creatorEmail` TEXT NOT NULL DEFAULT 'student@campusly.app'")
                db.execSQL("ALTER TABLE `events` ADD COLUMN `approvalStatus` TEXT NOT NULL DEFAULT 'Approved'")
                db.execSQL("ALTER TABLE `opportunities` ADD COLUMN `creatorEmail` TEXT NOT NULL DEFAULT 'student@campusly.app'")
                db.execSQL("ALTER TABLE `opportunities` ADD COLUMN `approvalStatus` TEXT NOT NULL DEFAULT 'Approved'")
                db.execSQL("ALTER TABLE `study_groups` ADD COLUMN `creatorEmail` TEXT NOT NULL DEFAULT 'student@campusly.app'")
                db.execSQL("ALTER TABLE `study_groups` ADD COLUMN `approvalStatus` TEXT NOT NULL DEFAULT 'Approved'")
                db.execSQL("ALTER TABLE `marketplace_listings` ADD COLUMN `sellerEmail` TEXT NOT NULL DEFAULT 'student@campusly.app'")
                db.execSQL("ALTER TABLE `marketplace_listings` ADD COLUMN `listingType` TEXT NOT NULL DEFAULT 'For Sale'")
                db.execSQL("ALTER TABLE `budgets` ADD COLUMN `userEmail` TEXT NOT NULL DEFAULT 'student@campusly.app'")
                db.execSQL("ALTER TABLE `savings_goals` ADD COLUMN `userEmail` TEXT NOT NULL DEFAULT 'student@campusly.app'")
                db.execSQL("ALTER TABLE `attendance_records` ADD COLUMN `userEmail` TEXT NOT NULL DEFAULT 'student@campusly.app'")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `forum_replies` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `postId` INTEGER NOT NULL,
                        `senderName` TEXT NOT NULL,
                        `senderEmail` TEXT NOT NULL,
                        `replyText` TEXT NOT NULL,
                        `timestampMillis` INTEGER NOT NULL,
                        `isAnonymous` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `private_conversations` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `listingId` INTEGER NOT NULL,
                        `listingTitle` TEXT NOT NULL,
                        `participant1Email` TEXT NOT NULL,
                        `participant2Email` TEXT NOT NULL,
                        `lastMessage` TEXT NOT NULL,
                        `lastMessageTimestamp` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `private_messages` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `conversationId` INTEGER NOT NULL,
                        `senderEmail` TEXT NOT NULL,
                        `senderName` TEXT NOT NULL,
                        `messageText` TEXT NOT NULL,
                        `timestampMillis` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `users` ADD COLUMN `passwordHash` TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getDatabase(context: Context): CampuslyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CampuslyDatabase::class.java,
                    "campusly_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val context: Context) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.dao())
                    }
                }
            }
        }

        private suspend fun populateInitialData(dao: CampuslyDao) {
            val defaultStudentEmail = "student@campusly.app"
            dao.insertUser(
                UserEntity(
                    id = "1",
                    email = defaultStudentEmail,
                    fullName = "Lucy Vance",
                    university = "University of Science & Technology",
                    courseProgram = "Computer Science",
                    yearOfStudy = "Year 3",
                    preferredStudyTimes = "Evening (18:00 - 22:00)",
                    bio = "CS Major | Mobile App Developer & Tech Enthusiast",
                    isAdmin = false
                )
            )

            val course1 = dao.insertCourse(CourseEntity(code = "APT2060", name = "Data Structures & Algorithms", lecturer = "Dr. Example", creditUnits = 3, semester = "Semester 1", colorHex = "#8B7CF6"))
            val course2 = dao.insertCourse(CourseEntity(code = "IST2045", name = "Computer Networks", lecturer = "Prof. Smith", creditUnits = 3, semester = "Semester 1", colorHex = "#5FAF8F"))
            val course3 = dao.insertCourse(CourseEntity(code = "APT3080", name = "Management Information Systems", lecturer = "Dr. Johnson", creditUnits = 3, semester = "Semester 1", colorHex = "#F27C66"))
            val course4 = dao.insertCourse(CourseEntity(code = "APT2022", name = "Assembly Programming", lecturer = "Eng. Williams", creditUnits = 4, semester = "Semester 1", colorHex = "#111827"))

            dao.insertTimetable(TimetableEntity(userEmail = defaultStudentEmail, courseId = course4, courseCode = "APT2022", courseName = "Assembly Programming", instructor = "Eng. Williams", room = "Lab 1", dayOfWeek = 2, startTime = "09:00", endTime = "10:30"))
            dao.insertTimetable(TimetableEntity(userEmail = defaultStudentEmail, courseId = course3, courseCode = "APT3080", courseName = "Management Information Systems", instructor = "Dr. Johnson", room = "Room 302", dayOfWeek = 2, startTime = "11:00", endTime = "12:30"))
            dao.insertTimetable(TimetableEntity(userEmail = defaultStudentEmail, courseId = course1, courseCode = "APT2060", courseName = "Data Structures & Algorithms", instructor = "Dr. Example", room = "Lab 3", dayOfWeek = 2, startTime = "13:20", endTime = "15:00"))
            dao.insertTimetable(TimetableEntity(userEmail = defaultStudentEmail, courseId = course2, courseCode = "IST2045", courseName = "Computer Networks", instructor = "Prof. Smith", room = "Hall B", dayOfWeek = 3, startTime = "10:00", endTime = "12:00"))

            val now = System.currentTimeMillis()
            val oneDayMs = 24 * 60 * 60 * 1000L
            dao.insertAssignment(AssignmentEntity(userEmail = defaultStudentEmail, courseId = course1, courseName = "Data Structures & Algorithms", title = "Operating Systems Lab Assignment", description = "Implement process scheduling algorithms in C++.", dueDateMillis = now + oneDayMs, dueTimeString = "23:59", priority = "High", status = "In progress"))
            dao.insertAssignment(AssignmentEntity(userEmail = defaultStudentEmail, courseId = course2, courseName = "Computer Networks", title = "Subnetting Problem Set", description = "Calculate host ranges for Class B CIDR blocks.", dueDateMillis = now + (3 * oneDayMs), dueTimeString = "17:00", priority = "Medium", status = "Not started"))

            dao.insertExam(ExamEntity(userEmail = defaultStudentEmail, courseId = course1, courseName = "Data Structures & Algorithms", examDateMillis = now + (12 * oneDayMs), timeString = "09:00 AM", venue = "Main Examination Hall", topics = "Binary Trees, Graphs, Sorting Algorithms"))
            dao.insertExam(ExamEntity(userEmail = defaultStudentEmail, courseId = course2, courseName = "Computer Networks", examDateMillis = now + (18 * oneDayMs), timeString = "02:00 PM", venue = "Science Complex 101", topics = "OSI Model, TCP/IP, Routing Protocols"))

            dao.insertTask(TaskEntity(userEmail = defaultStudentEmail, subject = "Data Structures & Algorithms", title = "Review BFS & DFS Lecture Notes", description = "Focus on queue vs stack state tracking", dateString = "Today", timeString = "16:00", durationMinutes = 45, priority = "High", category = "Smart Plan", isCompleted = false))
            dao.insertTask(TaskEntity(userEmail = defaultStudentEmail, subject = "Computer Networks", title = "Practice Class B CIDR Subnetting", description = "Solve 5 practice problems before quiz", dateString = "Tomorrow", timeString = "14:00", durationMinutes = 60, priority = "Medium", category = "Smart Plan", isCompleted = false))

            dao.insertNote(NoteEntity(userEmail = defaultStudentEmail, courseId = course1, courseName = "Data Structures & Algorithms", title = "Graph Traversal (BFS & DFS)", content = "BFS uses a Queue data structure (FIFO). DFS uses a Stack data structure (LIFO) or recursion.", isPinned = true, folder = "Algorithms"))
            dao.insertNote(NoteEntity(userEmail = defaultStudentEmail, courseId = course2, courseName = "Computer Networks", title = "OSI 7-Layer Model Notes", content = "1. Physical\n2. Data Link\n3. Network\n4. Transport\n5. Session\n6. Presentation\n7. Application", isFavorite = true, folder = "Networks"))

            dao.insertGpaRecord(GpaRecordEntity(userEmail = defaultStudentEmail, semesterName = "Year 1 - Semester 1", gpaScore = 3.42, creditUnits = 18))
            dao.insertGpaRecord(GpaRecordEntity(userEmail = defaultStudentEmail, semesterName = "Year 1 - Semester 2", gpaScore = 3.61, creditUnits = 18))
            dao.insertGpaRecord(GpaRecordEntity(userEmail = defaultStudentEmail, semesterName = "Year 2 - Semester 1", gpaScore = 3.55, creditUnits = 18))

            dao.insertEvent(EventEntity(creatorEmail = defaultStudentEmail, title = "Annual University Hackathon 2026", organizer = "Tech Club", dateString = "Oct 15, 2026", location = "Student Center", description = "24-hour building marathon for tech innovations with Ksh 100k prize pool.", category = "Hackathon", isInterested = true, approvalStatus = "Approved"))
            dao.insertEvent(EventEntity(creatorEmail = defaultStudentEmail, title = "Career Fair & Networking", organizer = "Career Placement Office", dateString = "Oct 22, 2026", location = "Auditorium A", description = "Meet industry leaders and recruiters from top tech companies.", category = "Career", approvalStatus = "Approved"))
            dao.insertEvent(EventEntity(creatorEmail = defaultStudentEmail, title = "AI & Machine Learning Workshop", organizer = "Data Science Guild", dateString = "Nov 5, 2026", location = "Lab 4", description = "Hands-on machine learning workshop using Python and PyTorch.", category = "Workshop", approvalStatus = "Pending"))

            dao.insertOpportunity(OpportunityEntity(creatorEmail = defaultStudentEmail, title = "Software Engineering Intern", organization = "Safaricom Tech Labs", deadlineString = "Nov 1, 2026", description = "3-month paid internship for final and third-year computer science students.", category = "Internship", status = "Saved", approvalStatus = "Approved"))
            dao.insertOpportunity(OpportunityEntity(creatorEmail = defaultStudentEmail, title = "Global Student Leadership Fellowship", organization = "Youth Africa Foundation", deadlineString = "Nov 15, 2026", description = "Fully funded leadership summit for outstanding undergraduates.", category = "Fellowship", status = "Saved", approvalStatus = "Approved"))

            dao.insertStudyGroup(StudyGroupEntity(creatorEmail = defaultStudentEmail, name = "DSA Survivors Group", courseName = "Data Structures & Algorithms", memberCount = 23, topicSummary = "Linked Lists, Trees, Graphs & Dynamic Programming", isJoined = true, approvalStatus = "Approved"))
            dao.insertStudyGroup(StudyGroupEntity(creatorEmail = defaultStudentEmail, name = "Networks & Protocols Guild", courseName = "Computer Networks", memberCount = 15, topicSummary = "Packet Tracing, Subnetting, Socket Programming", approvalStatus = "Approved"))

            dao.insertMarketplaceListing(MarketplaceListingEntity(sellerEmail = "david@campusly.app", sellerName = "David K.", title = "Casio FX-991EX Scientific Calculator", description = "In perfect condition, lightly used for one semester.", priceKes = 2500.0, category = "Electronics", location = "Hostel B", condition = "Like New", listingType = "For Sale"))
            dao.insertMarketplaceListing(MarketplaceListingEntity(sellerEmail = "sarah@campusly.app", sellerName = "Sarah M.", title = "Data Structures & Algorithms in Java (4th Ed)", description = "Standard textbook, clean pages with minimal highlighting.", priceKes = 1800.0, category = "Textbooks", location = "Library Lobby", condition = "Good", listingType = "For Sale"))
            dao.insertMarketplaceListing(MarketplaceListingEntity(sellerEmail = defaultStudentEmail, sellerName = "Lucy Vance", title = "Looking for Lab Coat (Size M)", description = "Need a clean white chemistry lab coat for organic chemistry lab.", priceKes = 1000.0, category = "Supplies", location = "Science Complex", condition = "Used", listingType = "Wanted"))

            dao.insertBudget(BudgetEntity(userEmail = defaultStudentEmail, category = "Food & Groceries", targetAmount = 15000.0, spentAmount = 9200.0))
            dao.insertBudget(BudgetEntity(userEmail = defaultStudentEmail, category = "Transport", targetAmount = 5000.0, spentAmount = 2800.0))
            dao.insertBudget(BudgetEntity(userEmail = defaultStudentEmail, category = "Books & Supplies", targetAmount = 6000.0, spentAmount = 4500.0))
            dao.insertSavingsGoal(SavingsGoalEntity(userEmail = defaultStudentEmail, title = "New Developer Laptop", targetKes = 80000.0, savedKes = 35000.0))

            dao.insertAttendance(AttendanceRecordEntity(userEmail = defaultStudentEmail, courseId = course1, courseName = "Data Structures & Algorithms", attendedSessions = 14, totalSessions = 16))
            dao.insertAttendance(AttendanceRecordEntity(userEmail = defaultStudentEmail, courseId = course2, courseName = "Computer Networks", attendedSessions = 12, totalSessions = 14))

            val currentTime = System.currentTimeMillis()
            val hourMs = 3600000L
            dao.insertForumMessage(ForumMessageEntity(senderName = "Campus Admin", senderEmail = "admin@campusly.app", category = "Announcements", message = "📢 Welcome to Campusly Broadcast Forum! Connect with fellow students, share study tips, and post campus announcements.", timestampMillis = currentTime - (2 * hourMs), likesCount = 15, isLiked = true))
            val msg2 = dao.insertForumMessage(ForumMessageEntity(senderName = "Alex Kimani", senderEmail = "alex@campusly.app", category = "Study Help", message = "Anyone up for a Data Structures & Algorithms study session in the main library around 4 PM today?", timestampMillis = currentTime - (1 * hourMs), likesCount = 6, isLiked = false))

            dao.insertForumReply(ForumReplyEntity(postId = msg2, senderName = "Lucy Vance", senderEmail = defaultStudentEmail, replyText = "Count me in! I'll bring my graph traversal notes.", timestampMillis = currentTime - (45 * 60 * 1000L)))
            dao.insertForumReply(ForumReplyEntity(postId = msg2, senderName = "David K.", senderEmail = "david@campusly.app", replyText = "Great! See you guys on the 2nd floor study area.", timestampMillis = currentTime - (30 * 60 * 1000L)))
        }
    }
}
