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
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
