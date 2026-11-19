package com.example.gossipapp

import com.example.gossipapp.data.ai.CampuslyAiService
import com.example.gossipapp.data.auth.CampuslyAuthManager
import com.example.gossipapp.data.auth.CampuslyBackendConfig
import com.example.gossipapp.data.local.entity.GpaRecordEntity
import com.example.gossipapp.data.local.entity.NoteEntity
import com.example.gossipapp.data.local.entity.StudySessionEntity
import com.example.gossipapp.data.local.entity.TimetableEntity
import com.example.gossipapp.data.local.entity.TaskEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.UUID

class CampuslyDomainUnitTest {

    @Test
    fun institutionalEmailValidator_acceptsValidAcKeDomainsAndRejectsPersonalOrLookalikeDomains() {
        val dummyDao = object : com.example.gossipapp.data.local.dao.CampuslyDao {
            override fun getUser(): kotlinx.coroutines.flow.Flow<com.example.gossipapp.data.local.entity.UserEntity?> = kotlinx.coroutines.flow.flowOf(null)
            override suspend fun getUserByEmail(email: String): com.example.gossipapp.data.local.entity.UserEntity? = null
            override fun getUserByEmailFlow(email: String): kotlinx.coroutines.flow.Flow<com.example.gossipapp.data.local.entity.UserEntity?> = kotlinx.coroutines.flow.flowOf(null)
            override suspend fun getUserById(id: String): com.example.gossipapp.data.local.entity.UserEntity? = null
            override suspend fun insertUser(user: com.example.gossipapp.data.local.entity.UserEntity) {}
            override suspend fun clearUser() {}
            override fun getAllCourses(): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.CourseEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override suspend fun insertCourse(course: com.example.gossipapp.data.local.entity.CourseEntity): Long = 0
            override suspend fun deleteCourse(course: com.example.gossipapp.data.local.entity.CourseEntity) {}
            override fun getTimetableForUser(userEmail: String): kotlinx.coroutines.flow.Flow<List<TimetableEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override fun getAllTimetable(): kotlinx.coroutines.flow.Flow<List<TimetableEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override fun getTimetableForDay(dayOfWeek: Int): kotlinx.coroutines.flow.Flow<List<TimetableEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override suspend fun insertTimetable(timetable: TimetableEntity): Long = 0
            override suspend fun deleteTimetable(timetable: TimetableEntity) {}
            override fun getAssignmentsForUser(userEmail: String): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.AssignmentEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override fun getAllAssignments(): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.AssignmentEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override suspend fun insertAssignment(assignment: com.example.gossipapp.data.local.entity.AssignmentEntity): Long = 0
            override suspend fun updateAssignment(assignment: com.example.gossipapp.data.local.entity.AssignmentEntity) {}
            override suspend fun deleteAssignment(assignment: com.example.gossipapp.data.local.entity.AssignmentEntity) {}
            override fun getExamsForUser(userEmail: String): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.ExamEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override fun getAllExams(): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.ExamEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override suspend fun insertExam(exam: com.example.gossipapp.data.local.entity.ExamEntity): Long = 0
            override suspend fun updateExam(exam: com.example.gossipapp.data.local.entity.ExamEntity) {}
            override suspend fun deleteExam(exam: com.example.gossipapp.data.local.entity.ExamEntity) {}
            override fun getTasksForUser(userEmail: String): kotlinx.coroutines.flow.Flow<List<TaskEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override fun getAllTasks(): kotlinx.coroutines.flow.Flow<List<TaskEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override suspend fun insertTask(task: TaskEntity): Long = 0
            override suspend fun updateTask(task: TaskEntity) {}
            override suspend fun deleteTask(task: TaskEntity) {}
            override suspend fun deleteTaskScoped(id: Long, userEmail: String): Int = 0
            override suspend fun updateTaskScoped(id: Long, userEmail: String, title: String, subject: String, description: String, priority: String, isCompleted: Boolean): Int = 0
            override fun getNotesForUser(userEmail: String): kotlinx.coroutines.flow.Flow<List<NoteEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override fun getAllNotes(): kotlinx.coroutines.flow.Flow<List<NoteEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override suspend fun insertNote(note: NoteEntity): Long = 0
            override suspend fun updateNote(note: NoteEntity) {}
            override suspend fun deleteNote(note: NoteEntity) {}
            override fun getStudySessionsForUser(userEmail: String): kotlinx.coroutines.flow.Flow<List<StudySessionEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override fun getAllStudySessions(): kotlinx.coroutines.flow.Flow<List<StudySessionEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override suspend fun insertStudySession(session: StudySessionEntity): Long = 0
            override fun getFlashcardsForUser(userEmail: String): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.FlashcardEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override fun getAllFlashcards(): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.FlashcardEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override suspend fun insertFlashcard(flashcard: com.example.gossipapp.data.local.entity.FlashcardEntity): Long = 0
            override suspend fun updateFlashcard(flashcard: com.example.gossipapp.data.local.entity.FlashcardEntity) {}
            override suspend fun deleteFlashcard(flashcard: com.example.gossipapp.data.local.entity.FlashcardEntity) {}
            override suspend fun deleteFlashcardDeck(userEmail: String, deckName: String) {}
            override suspend fun deleteFlashcardScoped(id: Long, userEmail: String) {}
            override suspend fun updateFlashcardScoped(id: Long, userEmail: String, front: String, back: String) {}
            override fun getAllGpaRecords(): kotlinx.coroutines.flow.Flow<List<GpaRecordEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override suspend fun insertGpaRecord(record: GpaRecordEntity): Long = 0
            override fun getApprovedEvents(): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.EventEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override fun getPendingEvents(): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.EventEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override fun getUserEvents(email: String): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.EventEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override fun getAllEvents(): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.EventEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override suspend fun insertEvent(event: com.example.gossipapp.data.local.entity.EventEntity): Long = 0
            override suspend fun updateEvent(event: com.example.gossipapp.data.local.entity.EventEntity) {}
            override suspend fun deleteEvent(event: com.example.gossipapp.data.local.entity.EventEntity) {}
            override fun getApprovedOpportunities(): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.OpportunityEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override fun getPendingOpportunities(): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.OpportunityEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override fun getUserOpportunities(email: String): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.OpportunityEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override fun getAllOpportunities(): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.OpportunityEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override suspend fun insertOpportunity(opportunity: com.example.gossipapp.data.local.entity.OpportunityEntity): Long = 0
            override suspend fun updateOpportunity(opportunity: com.example.gossipapp.data.local.entity.OpportunityEntity) {}
            override suspend fun deleteOpportunity(opportunity: com.example.gossipapp.data.local.entity.OpportunityEntity) {}
            override fun getApprovedStudyGroups(): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.StudyGroupEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override fun getPendingStudyGroups(): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.StudyGroupEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override fun getUserStudyGroups(email: String): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.StudyGroupEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override fun getAllStudyGroups(): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.StudyGroupEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override suspend fun insertStudyGroup(group: com.example.gossipapp.data.local.entity.StudyGroupEntity): Long = 0
            override suspend fun updateStudyGroup(group: com.example.gossipapp.data.local.entity.StudyGroupEntity) {}
            override suspend fun deleteStudyGroup(group: com.example.gossipapp.data.local.entity.StudyGroupEntity) {}
            override fun getAllMarketplaceListings(): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.MarketplaceListingEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override suspend fun insertMarketplaceListing(listing: com.example.gossipapp.data.local.entity.MarketplaceListingEntity): Long = 0
            override suspend fun updateMarketplaceListing(listing: com.example.gossipapp.data.local.entity.MarketplaceListingEntity) {}
            override suspend fun deleteMarketplaceListing(listing: com.example.gossipapp.data.local.entity.MarketplaceListingEntity) {}
            override fun getConversationsForUser(email: String): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.PrivateConversationEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override suspend fun getConversationBetween(listingId: Long, e1: String, e2: String): com.example.gossipapp.data.local.entity.PrivateConversationEntity? = null
            override suspend fun insertConversation(conversation: com.example.gossipapp.data.local.entity.PrivateConversationEntity): Long = 0
            override suspend fun updateConversation(conversation: com.example.gossipapp.data.local.entity.PrivateConversationEntity) {}
            override fun getMessagesForConversation(convId: Long): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.PrivateMessageEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override suspend fun insertPrivateMessage(message: com.example.gossipapp.data.local.entity.PrivateMessageEntity): Long = 0
            override fun getAllBudgets(): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.BudgetEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override suspend fun insertBudget(budget: com.example.gossipapp.data.local.entity.BudgetEntity): Long = 0
            override fun getAllSavingsGoals(): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.SavingsGoalEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override suspend fun insertSavingsGoal(goal: com.example.gossipapp.data.local.entity.SavingsGoalEntity): Long = 0
            override fun getAllAttendance(): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.AttendanceRecordEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override suspend fun insertAttendance(record: com.example.gossipapp.data.local.entity.AttendanceRecordEntity): Long = 0
            override suspend fun updateAttendance(record: com.example.gossipapp.data.local.entity.AttendanceRecordEntity) {}
            override fun getAllForumMessages(): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.ForumMessageEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override suspend fun insertForumMessage(message: com.example.gossipapp.data.local.entity.ForumMessageEntity): Long = 0
            override suspend fun updateForumMessage(message: com.example.gossipapp.data.local.entity.ForumMessageEntity) {}
            override suspend fun deleteForumMessage(message: com.example.gossipapp.data.local.entity.ForumMessageEntity) {}
            override fun getRepliesForPost(postId: Long): kotlinx.coroutines.flow.Flow<List<com.example.gossipapp.data.local.entity.ForumReplyEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
            override suspend fun insertForumReply(reply: com.example.gossipapp.data.local.entity.ForumReplyEntity): Long = 0
            override suspend fun deleteForumReply(reply: com.example.gossipapp.data.local.entity.ForumReplyEntity) {}
        }

        val authManager = CampuslyAuthManager(dummyDao)

        assertTrue(authManager.isValidInstitutionalEmail("student@usiu.ac.ke"))
        assertTrue(authManager.isValidInstitutionalEmail("LUCY.VANCE@STUDENTS.UONBI.AC.KE"))
        assertFalse(authManager.isValidInstitutionalEmail("user@gmail.com"))
        assertFalse(authManager.isValidInstitutionalEmail("usiu.ac.ke.example.com"))
        assertFalse(authManager.isValidInstitutionalEmail("user@usiu.ac.ke.fake.org"))
        assertFalse(authManager.isValidInstitutionalEmail("invalid-email"))
    }

    @Test
    fun calculateCumulativeGpa_calculatesWeightedAverageCorrectly() {
        val records = listOf(
            GpaRecordEntity(semesterName = "Sem 1", gpaScore = 3.5, creditUnits = 18),
            GpaRecordEntity(semesterName = "Sem 2", gpaScore = 3.8, creditUnits = 18)
        )
        val expected = (3.5 * 18 + 3.8 * 18) / 36.0
        val actual = calculateGpa(records)
        assertEquals(expected, actual, 0.001)
    }

    @Test
    fun timetableConflictDetector_flagsOverlappingClassesOnSameDay() {
        val existing = TimetableEntity(
            courseCode = "APT2060",
            courseName = "DSA",
            instructor = "Dr. X",
            room = "Lab 1",
            dayOfWeek = 2,
            startTime = "10:00",
            endTime = "12:00"
        )

        val overlapping = TimetableEntity(
            courseCode = "IST2045",
            courseName = "Networks",
            instructor = "Prof. Y",
            room = "Hall A",
            dayOfWeek = 2,
            startTime = "11:30",
            endTime = "13:00"
        )

        val nonOverlapping = TimetableEntity(
            courseCode = "APT3080",
            courseName = "MIS",
            instructor = "Dr. Z",
            room = "Room 3",
            dayOfWeek = 2,
            startTime = "12:30",
            endTime = "14:00"
        )

        assertTrue(checkConflict(existing, overlapping))
        assertFalse(checkConflict(existing, nonOverlapping))
    }

    @Test
    fun pomodoroFocusTimeCalculator_excludesBreaksAndDeduplicatesSessions() {
        val session1Id = UUID.randomUUID().toString()
        val session2Id = UUID.randomUUID().toString()

        val sessions = listOf(
            StudySessionEntity(sessionId = session1Id, courseName = "DSA", topic = "Trees", durationMinutes = 25, isBreak = false),
            StudySessionEntity(sessionId = session1Id, courseName = "DSA", topic = "Trees", durationMinutes = 25, isBreak = false), // Duplicate sync
            StudySessionEntity(sessionId = session2Id, courseName = "Networks", topic = "Subnetting", durationMinutes = 50, isBreak = false),
            StudySessionEntity(sessionId = UUID.randomUUID().toString(), courseName = "Rest", topic = "Break", durationMinutes = 10, isBreak = true) // Break
        )

        val totalFocusMinutes = sessions.filter { !it.isBreak }.distinctBy { it.sessionId }.sumOf { it.durationMinutes }
        assertEquals(75, totalFocusMinutes)
    }

    @Test
    fun quietHours_evaluatesOvernightRangeCorrectly() {
        val quietStart = "22:00"
        val quietEnd = "07:00"

        val nightCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 30)
        }

        val earlyMorningCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 4)
            set(Calendar.MINUTE, 15)
        }

        val afternoonCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 0)
        }

        assertTrue(isQuietHours(quietStart, quietEnd, nightCal))
        assertTrue(isQuietHours(quietStart, quietEnd, earlyMorningCal))
        assertFalse(isQuietHours(quietStart, quietEnd, afternoonCal))
    }

    @Test
    fun trustedAdminCheck_restrictsAdminRoleExclusiveToConfiguredBackendAccounts() {
        val adminId = "admin-user-id-123"
        val adminEmail = "verified_admin@usiu.ac.ke"
        val studentEmail = "student@usiu.ac.ke"

        CampuslyBackendConfig.configureTrustedAdminAccounts(
            adminIds = setOf(adminId),
            adminEmails = setOf(adminEmail)
        )

        assertTrue(CampuslyBackendConfig.isVerifiedAdmin(adminId, adminEmail))
        assertFalse(CampuslyBackendConfig.isVerifiedAdmin("random-id", studentEmail))
    }

    @Test
    fun aiFlashcardGenerator_generatesStructuredCardsForDatabaseNormalization() {
        val result = CampuslyAiService.generateStructuredCardsLocally("Database Normalization", "database normalization", false, 7)
        val cards = result.first

        assertEquals(7, cards.size)
        assertTrue(cards.any { it.front.contains("1NF", ignoreCase = true) || it.front.contains("Normalization", ignoreCase = true) })
        assertTrue(cards.any { it.back.contains("redundancy", ignoreCase = true) || it.back.contains("atomic", ignoreCase = true) })
    }

    @Test
    fun aiFlashcardGenerator_handlesShortNoteContentWithExplanation() {
        val shortNote = NoteEntity(title = "Brief Note", content = "AES uses 128-bit block size.")
        val result = CampuslyAiService.generateStructuredCardsLocally(shortNote.title, shortNote.content, true, 5)

        assertNotNull(result.first)
        assertTrue(result.first.isNotEmpty())
        assertNotNull(result.second)
        assertTrue(result.second!!.contains("Note content provided"))
    }

    private fun isQuietHours(start: String, end: String, now: Calendar): Boolean {
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val startMinutes = parseTimeToMinutes(start)
        val endMinutes = parseTimeToMinutes(end)

        return if (startMinutes > endMinutes) {
            currentMinutes >= startMinutes || currentMinutes < endMinutes
        } else {
            currentMinutes in startMinutes until endMinutes
        }
    }

    private fun calculateGpa(records: List<GpaRecordEntity>): Double {
        if (records.isEmpty()) return 0.0
        val totalPoints = records.sumOf { it.gpaScore * it.creditUnits }
        val totalCredits = records.sumOf { it.creditUnits }
        if (totalCredits == 0) return 0.0
        return totalPoints / totalCredits
    }

    private fun checkConflict(t1: TimetableEntity, t2: TimetableEntity): Boolean {
        if (t1.dayOfWeek != t2.dayOfWeek) return false
        val s1 = parseTimeToMinutes(t1.startTime)
        val e1 = parseTimeToMinutes(t1.endTime)
        val s2 = parseTimeToMinutes(t2.startTime)
        val e2 = parseTimeToMinutes(t2.endTime)
        return maxOf(s1, s2) < minOf(e1, e2)
    }

    private fun parseTimeToMinutes(timeStr: String): Int {
        val parts = timeStr.split(":")
        val h = parts[0].trim().toInt()
        val m = parts[1].trim().toInt()
        return h * 60 + m
    }
}
