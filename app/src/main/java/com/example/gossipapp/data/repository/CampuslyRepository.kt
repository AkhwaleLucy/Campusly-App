package com.example.gossipapp.data.repository

import com.example.gossipapp.data.local.dao.CampuslyDao
import com.example.gossipapp.data.local.entity.*
import com.example.gossipapp.data.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow

class CampuslyRepository(
    private val dao: CampuslyDao,
    private val preferences: UserPreferences
) {

    // User & Onboarding
    val user: Flow<UserEntity?> = dao.getUser()
    val isOnboarded: Flow<Boolean> = preferences.isOnboarded
    val isLoggedIn: Flow<Boolean> = preferences.isLoggedIn
    val currentUserEmailPref: Flow<String> = preferences.currentUserEmail
    val themeMode: Flow<String> = preferences.themeMode
    val selectedUniversity: Flow<String> = preferences.selectedUniversity
    val notificationsEnabled: Flow<Boolean> = preferences.notificationsEnabled
    val quietHoursEnabled: Flow<Boolean> = preferences.quietHoursEnabled
    val quietHoursStart: Flow<String> = preferences.quietHoursStart
    val quietHoursEnd: Flow<String> = preferences.quietHoursEnd
    val isAnonymousMode: Flow<Boolean> = preferences.isAnonymousMode

    suspend fun saveUser(user: UserEntity) = dao.insertUser(user)
    suspend fun clearUser() = dao.clearUser()
    suspend fun setOnboarded(completed: Boolean) = preferences.setOnboarded(completed)
    suspend fun setLoggedIn(loggedIn: Boolean) = preferences.setLoggedIn(loggedIn)
    suspend fun setCurrentUserEmailPref(email: String) = preferences.setCurrentUserEmail(email)
    suspend fun setThemeMode(mode: String) = preferences.setThemeMode(mode)
    suspend fun setSelectedUniversity(uni: String) = preferences.setSelectedUniversity(uni)
    suspend fun setNotificationsEnabled(enabled: Boolean) = preferences.setNotificationsEnabled(enabled)
    suspend fun setQuietHoursEnabled(enabled: Boolean) = preferences.setQuietHoursEnabled(enabled)
    suspend fun setQuietHoursStart(time: String) = preferences.setQuietHoursStart(time)
    suspend fun setQuietHoursEnd(time: String) = preferences.setQuietHoursEnd(time)
    suspend fun setAnonymousMode(enabled: Boolean) = preferences.setAnonymousMode(enabled)

    // Courses & Timetable
    val allCourses: Flow<List<CourseEntity>> = dao.getAllCourses()
    val allTimetable: Flow<List<TimetableEntity>> = dao.getAllTimetable()
    fun getTimetableForUser(email: String): Flow<List<TimetableEntity>> = dao.getTimetableForUser(email)
    fun getTimetableForDay(dayOfWeek: Int): Flow<List<TimetableEntity>> = dao.getTimetableForDay(dayOfWeek)

    suspend fun addCourse(course: CourseEntity): Long = dao.insertCourse(course)
    suspend fun deleteCourse(course: CourseEntity) = dao.deleteCourse(course)

    suspend fun addTimetableEntry(timetable: TimetableEntity): Long = dao.insertTimetable(timetable)
    suspend fun deleteTimetableEntry(timetable: TimetableEntity) = dao.deleteTimetable(timetable)

    // Assignments
    val allAssignments: Flow<List<AssignmentEntity>> = dao.getAllAssignments()
    fun getAssignmentsForUser(email: String): Flow<List<AssignmentEntity>> = dao.getAssignmentsForUser(email)
    suspend fun addAssignment(assignment: AssignmentEntity): Long = dao.insertAssignment(assignment)
    suspend fun updateAssignment(assignment: AssignmentEntity) = dao.updateAssignment(assignment)
    suspend fun deleteAssignment(assignment: AssignmentEntity) = dao.deleteAssignment(assignment)

    // Exams
    val allExams: Flow<List<ExamEntity>> = dao.getAllExams()
    fun getExamsForUser(email: String): Flow<List<ExamEntity>> = dao.getExamsForUser(email)
    suspend fun addExam(exam: ExamEntity): Long = dao.insertExam(exam)
    suspend fun updateExam(exam: ExamEntity) = dao.updateExam(exam)
    suspend fun deleteExam(exam: ExamEntity) = dao.deleteExam(exam)

    // Tasks & Smart Study Plans
    val allTasks: Flow<List<TaskEntity>> = dao.getAllTasks()
    fun getTasksForUser(email: String): Flow<List<TaskEntity>> = dao.getTasksForUser(email)
    suspend fun addTask(task: TaskEntity): Long = dao.insertTask(task)
    suspend fun updateTask(task: TaskEntity) = dao.updateTask(task)
    suspend fun deleteTask(task: TaskEntity) = dao.deleteTask(task)

    // Notes
    val allNotes: Flow<List<NoteEntity>> = dao.getAllNotes()
    fun getNotesForUser(email: String): Flow<List<NoteEntity>> = dao.getNotesForUser(email)
    suspend fun addNote(note: NoteEntity): Long = dao.insertNote(note)
    suspend fun updateNote(note: NoteEntity) = dao.updateNote(note)
    suspend fun deleteNote(note: NoteEntity) = dao.deleteNote(note)

    // Study Sessions
    val allStudySessions: Flow<List<StudySessionEntity>> = dao.getAllStudySessions()
    fun getStudySessionsForUser(email: String): Flow<List<StudySessionEntity>> = dao.getStudySessionsForUser(email)
    suspend fun addStudySession(session: StudySessionEntity): Long = dao.insertStudySession(session)

    // Flashcards
    val allFlashcards: Flow<List<FlashcardEntity>> = dao.getAllFlashcards()
    fun getFlashcardsForUser(email: String): Flow<List<FlashcardEntity>> = dao.getFlashcardsForUser(email)
    suspend fun addFlashcard(flashcard: FlashcardEntity): Long = dao.insertFlashcard(flashcard)

    // GPA
    val allGpaRecords: Flow<List<GpaRecordEntity>> = dao.getAllGpaRecords()
    suspend fun addGpaRecord(record: GpaRecordEntity): Long = dao.insertGpaRecord(record)

    // Discovery Features (Approved, Pending, User)
    val approvedEvents: Flow<List<EventEntity>> = dao.getApprovedEvents()
    val pendingEvents: Flow<List<EventEntity>> = dao.getPendingEvents()
    fun getUserEvents(email: String): Flow<List<EventEntity>> = dao.getUserEvents(email)
    suspend fun addEvent(event: EventEntity): Long = dao.insertEvent(event)
    suspend fun updateEvent(event: EventEntity) = dao.updateEvent(event)
    suspend fun deleteEvent(event: EventEntity) = dao.deleteEvent(event)

    val approvedOpportunities: Flow<List<OpportunityEntity>> = dao.getApprovedOpportunities()
    val pendingOpportunities: Flow<List<OpportunityEntity>> = dao.getPendingOpportunities()
    fun getUserOpportunities(email: String): Flow<List<OpportunityEntity>> = dao.getUserOpportunities(email)
    suspend fun addOpportunity(opportunity: OpportunityEntity): Long = dao.insertOpportunity(opportunity)
    suspend fun updateOpportunity(opportunity: OpportunityEntity) = dao.updateOpportunity(opportunity)
    suspend fun deleteOpportunity(opportunity: OpportunityEntity) = dao.deleteOpportunity(opportunity)

    val approvedStudyGroups: Flow<List<StudyGroupEntity>> = dao.getApprovedStudyGroups()
    val pendingStudyGroups: Flow<List<StudyGroupEntity>> = dao.getPendingStudyGroups()
    fun getUserStudyGroups(email: String): Flow<List<StudyGroupEntity>> = dao.getUserStudyGroups(email)
    suspend fun addStudyGroup(group: StudyGroupEntity): Long = dao.insertStudyGroup(group)
    suspend fun updateStudyGroup(group: StudyGroupEntity) = dao.updateStudyGroup(group)
    suspend fun deleteStudyGroup(group: StudyGroupEntity) = dao.deleteStudyGroup(group)

    // Marketplace
    val allMarketplaceListings: Flow<List<MarketplaceListingEntity>> = dao.getAllMarketplaceListings()
    suspend fun addMarketplaceListing(listing: MarketplaceListingEntity): Long = dao.insertMarketplaceListing(listing)
    suspend fun updateMarketplaceListing(listing: MarketplaceListingEntity) = dao.updateMarketplaceListing(listing)
    suspend fun deleteMarketplaceListing(listing: MarketplaceListingEntity) = dao.deleteMarketplaceListing(listing)

    // Private Conversations & Messages
    fun getConversationsForUser(email: String): Flow<List<PrivateConversationEntity>> = dao.getConversationsForUser(email)
    suspend fun getOrCreateConversation(listingId: Long, listingTitle: String, buyerEmail: String, sellerEmail: String): Long {
        val existing = dao.getConversationBetween(listingId, buyerEmail, sellerEmail)
        if (existing != null) return existing.id
        val newConv = PrivateConversationEntity(
            listingId = listingId,
            listingTitle = listingTitle,
            participant1Email = buyerEmail,
            participant2Email = sellerEmail,
            lastMessage = "Started conversation for $listingTitle",
            lastMessageTimestamp = System.currentTimeMillis()
        )
        return dao.insertConversation(newConv)
    }
    fun getMessagesForConversation(convId: Long): Flow<List<PrivateMessageEntity>> = dao.getMessagesForConversation(convId)
    suspend fun sendPrivateMessage(convId: Long, senderEmail: String, senderName: String, text: String) {
        val msg = PrivateMessageEntity(
            conversationId = convId,
            senderEmail = senderEmail,
            senderName = senderName,
            messageText = text,
            timestampMillis = System.currentTimeMillis()
        )
        dao.insertPrivateMessage(msg)
    }

    // Budgets & Savings & Attendance
    val allBudgets: Flow<List<BudgetEntity>> = dao.getAllBudgets()
    suspend fun addBudget(budget: BudgetEntity): Long = dao.insertBudget(budget)

    val allSavingsGoals: Flow<List<SavingsGoalEntity>> = dao.getAllSavingsGoals()
    suspend fun addSavingsGoal(goal: SavingsGoalEntity): Long = dao.insertSavingsGoal(goal)

    val allAttendance: Flow<List<AttendanceRecordEntity>> = dao.getAllAttendance()
    suspend fun addAttendance(record: AttendanceRecordEntity): Long = dao.insertAttendance(record)
    suspend fun updateAttendance(record: AttendanceRecordEntity) = dao.updateAttendance(record)

    // Forum Broadcast Messages & Replies
    val allForumMessages: Flow<List<ForumMessageEntity>> = dao.getAllForumMessages()
    suspend fun addForumMessage(message: ForumMessageEntity): Long = dao.insertForumMessage(message)
    suspend fun updateForumMessage(message: ForumMessageEntity) = dao.updateForumMessage(message)
    suspend fun deleteForumMessage(message: ForumMessageEntity) = dao.deleteForumMessage(message)

    fun getRepliesForPost(postId: Long): Flow<List<ForumReplyEntity>> = dao.getRepliesForPost(postId)
    suspend fun addForumReply(reply: ForumReplyEntity): Long = dao.insertForumReply(reply)
    suspend fun deleteForumReply(reply: ForumReplyEntity) = dao.deleteForumReply(reply)
}
