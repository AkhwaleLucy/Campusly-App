package com.example.gossipapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val email: String,
    val fullName: String,
    val university: String,
    val courseProgram: String,
    val yearOfStudy: String,
    val preferredStudyTimes: String = "Evening",
    val bio: String = "Student at University",
    val profilePicUrl: String? = null,
    val passwordHash: String = "",
    val isAdmin: Boolean = false
)

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val lecturer: String,
    val creditUnits: Int,
    val semester: String,
    val colorHex: String,
    val notes: String = ""
)

@Entity(tableName = "timetable")
data class TimetableEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "",
    val courseId: Long = 0,
    val courseCode: String,
    val courseName: String,
    val instructor: String,
    val room: String,
    val dayOfWeek: Int, // 1 = Monday, 7 = Sunday
    val startTime: String, // e.g., "13:20"
    val endTime: String, // e.g., "15:00"
    val notes: String = ""
)

@Entity(tableName = "assignments")
data class AssignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "",
    val courseId: Long = 0,
    val courseName: String,
    val title: String,
    val description: String = "",
    val dueDateMillis: Long,
    val dueTimeString: String = "23:59",
    val priority: String = "Medium", // High, Medium, Low
    val estimatedDurationMinutes: Int = 60,
    val status: String = "Not started", // Not started, In progress, Submitted, Completed
    val attachmentUrl: String? = null,
    val notes: String = ""
)

@Entity(tableName = "exams")
data class ExamEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "",
    val courseId: Long = 0,
    val courseName: String,
    val examDateMillis: Long,
    val timeString: String,
    val venue: String,
    val topics: String = "",
    val examFormat: String = "Written",
    val notes: String = ""
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "",
    val subject: String = "General",
    val title: String,
    val description: String = "",
    val dateString: String = "",
    val timeString: String = "09:00",
    val durationMinutes: Int = 60,
    val deadlineMillis: Long = System.currentTimeMillis() + 86400000L,
    val priority: String = "Medium", // High, Medium, Low
    val category: String = "General", // General, Smart Plan, Assignment, Exam Prep
    val isCompleted: Boolean = false,
    val isSuggested: Boolean = false
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "",
    val courseId: Long = 0,
    val courseName: String = "",
    val title: String,
    val content: String,
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val folder: String = "General"
)

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String = UUID.randomUUID().toString(),
    val userEmail: String = "",
    val courseId: Long = 0,
    val courseName: String,
    val topic: String,
    val durationMinutes: Int,
    val dateMillis: Long = System.currentTimeMillis(),
    val isBreak: Boolean = false
)

@Entity(tableName = "flashcards")
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "",
    val deckName: String,
    val courseName: String,
    val front: String,
    val back: String,
    val difficulty: String = "Medium"
)

@Entity(tableName = "gpa_records")
data class GpaRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "",
    val semesterName: String,
    val gpaScore: Double,
    val creditUnits: Int
)

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val creatorEmail: String = "",
    val title: String,
    val organizer: String,
    val dateString: String,
    val location: String,
    val description: String,
    val category: String,
    val isInterested: Boolean = false,
    val approvalStatus: String = "Approved" // Pending, Approved, Rejected
)

@Entity(tableName = "opportunities")
data class OpportunityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val creatorEmail: String = "",
    val title: String,
    val organization: String,
    val deadlineString: String,
    val description: String,
    val category: String,
    val status: String = "Saved", // Saved, Applied, Interview, Accepted
    val approvalStatus: String = "Approved" // Pending, Approved, Rejected
)

@Entity(tableName = "study_groups")
data class StudyGroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val creatorEmail: String = "",
    val name: String,
    val courseName: String,
    val memberCount: Int,
    val topicSummary: String,
    val isJoined: Boolean = false,
    val approvalStatus: String = "Approved" // Pending, Approved, Rejected
)

@Entity(tableName = "marketplace_listings")
data class MarketplaceListingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sellerEmail: String = "",
    val sellerName: String,
    val title: String,
    val description: String,
    val priceKes: Double,
    val category: String,
    val location: String,
    val condition: String = "Used",
    val status: String = "Available",
    val listingType: String = "For Sale" // "For Sale", "Wanted"
)

@Entity(tableName = "private_conversations")
data class PrivateConversationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val listingId: Long = 0,
    val listingTitle: String = "",
    val participant1Email: String, // buyer / inquirer
    val participant2Email: String, // seller / poster
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "private_messages")
data class PrivateMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: Long,
    val senderEmail: String,
    val senderName: String,
    val messageText: String,
    val timestampMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "",
    val category: String,
    val targetAmount: Double,
    val spentAmount: Double,
    val isIncome: Boolean = false
)

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "",
    val title: String,
    val targetKes: Double,
    val savedKes: Double
)

@Entity(tableName = "attendance_records")
data class AttendanceRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "",
    val courseId: Long = 0,
    val courseName: String,
    val attendedSessions: Int,
    val totalSessions: Int
)

@Entity(tableName = "forum_messages")
data class ForumMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderName: String,
    val senderEmail: String,
    val category: String = "General",
    val message: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val isAnonymous: Boolean = false
)

@Entity(tableName = "forum_replies")
data class ForumReplyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Long,
    val senderName: String,
    val senderEmail: String,
    val replyText: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val isAnonymous: Boolean = false
)
