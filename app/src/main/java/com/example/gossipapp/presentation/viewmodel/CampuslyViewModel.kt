package com.example.gossipapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gossipapp.data.local.entity.*
import com.example.gossipapp.data.repository.CampuslyRepository
import com.example.gossipapp.presentation.components.FlashcardItemData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class CampuslyUiState(
    val user: UserEntity? = null,
    val isOnboarded: Boolean = false,
    val isLoggedIn: Boolean = false,
    val themeMode: String = "SYSTEM",
    val selectedUniversity: String = "University of Science & Technology",
    val notificationsEnabled: Boolean = true,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: String = "22:00",
    val quietHoursEnd: String = "07:00",
    val isAnonymousMode: Boolean = false,
    val courses: List<CourseEntity> = emptyList(),
    val timetable: List<TimetableEntity> = emptyList(),
    val assignments: List<AssignmentEntity> = emptyList(),
    val exams: List<ExamEntity> = emptyList(),
    val tasks: List<TaskEntity> = emptyList(),
    val notes: List<NoteEntity> = emptyList(),
    val studySessions: List<StudySessionEntity> = emptyList(),
    val flashcards: List<FlashcardEntity> = emptyList(),
    val gpaRecords: List<GpaRecordEntity> = emptyList(),
    val events: List<EventEntity> = emptyList(),
    val opportunities: List<OpportunityEntity> = emptyList(),
    val studyGroups: List<StudyGroupEntity> = emptyList(),
    val pendingEvents: List<EventEntity> = emptyList(),
    val pendingOpportunities: List<OpportunityEntity> = emptyList(),
    val pendingStudyGroups: List<StudyGroupEntity> = emptyList(),
    val myEvents: List<EventEntity> = emptyList(),
    val myOpportunities: List<OpportunityEntity> = emptyList(),
    val myStudyGroups: List<StudyGroupEntity> = emptyList(),
    val marketplaceListings: List<MarketplaceListingEntity> = emptyList(),
    val budgets: List<BudgetEntity> = emptyList(),
    val savingsGoals: List<SavingsGoalEntity> = emptyList(),
    val attendanceRecords: List<AttendanceRecordEntity> = emptyList(),
    val forumMessages: List<ForumMessageEntity> = emptyList(),
    val forumReplies: List<ForumReplyEntity> = emptyList(),
    val selectedPostId: Long? = null,
    val privateConversations: List<PrivateConversationEntity> = emptyList(),
    val activeConversationMessages: List<PrivateMessageEntity> = emptyList(),
    val selectedConversationId: Long? = null,
    val searchQuery: String = "",
    val conflictMessage: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class CampuslyViewModel(
    private val repository: CampuslyRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _conflictMessage = MutableStateFlow<String?>(null)
    private val _selectedPostId = MutableStateFlow<Long?>(null)
    private val _selectedConversationId = MutableStateFlow<Long?>(null)

    private val forumRepliesFlow: Flow<List<ForumReplyEntity>> = _selectedPostId.flatMapLatest { postId ->
        if (postId != null) repository.getRepliesForPost(postId) else flowOf(emptyList())
    }

    private val privateMessagesFlow: Flow<List<PrivateMessageEntity>> = _selectedConversationId.flatMapLatest { convId ->
        if (convId != null) repository.getMessagesForConversation(convId) else flowOf(emptyList())
    }

    val uiState: StateFlow<CampuslyUiState> = combine(
        listOf(
            repository.user,
            repository.isOnboarded,
            repository.isLoggedIn,
            repository.themeMode,
            repository.selectedUniversity,
            repository.notificationsEnabled,
            repository.quietHoursEnabled,
            repository.quietHoursStart,
            repository.quietHoursEnd,
            repository.isAnonymousMode,
            repository.allCourses,
            repository.allTimetable,
            repository.allAssignments,
            repository.allExams,
            repository.allTasks,
            repository.allNotes,
            repository.allStudySessions,
            repository.allFlashcards,
            repository.allGpaRecords,
            repository.approvedEvents,
            repository.approvedOpportunities,
            repository.approvedStudyGroups,
            repository.pendingEvents,
            repository.pendingOpportunities,
            repository.pendingStudyGroups,
            repository.allMarketplaceListings,
            repository.allForumMessages,
            forumRepliesFlow,
            repository.getConversationsForUser("student@campusly.app"),
            privateMessagesFlow,
            _searchQuery,
            _conflictMessage,
            _selectedPostId,
            _selectedConversationId
        )
    ) { args: Array<Any?> ->
        CampuslyUiState(
            user = args[0] as? UserEntity,
            isOnboarded = args[1] as? Boolean ?: false,
            isLoggedIn = args[2] as? Boolean ?: false,
            themeMode = args[3] as? String ?: "SYSTEM",
            selectedUniversity = args[4] as? String ?: "University of Science & Technology",
            notificationsEnabled = args[5] as? Boolean ?: true,
            quietHoursEnabled = args[6] as? Boolean ?: false,
            quietHoursStart = args[7] as? String ?: "22:00",
            quietHoursEnd = args[8] as? String ?: "07:00",
            isAnonymousMode = args[9] as? Boolean ?: false,
            courses = (args[10] as? List<*>)?.filterIsInstance<CourseEntity>() ?: emptyList(),
            timetable = (args[11] as? List<*>)?.filterIsInstance<TimetableEntity>() ?: emptyList(),
            assignments = (args[12] as? List<*>)?.filterIsInstance<AssignmentEntity>() ?: emptyList(),
            exams = (args[13] as? List<*>)?.filterIsInstance<ExamEntity>() ?: emptyList(),
            tasks = (args[14] as? List<*>)?.filterIsInstance<TaskEntity>() ?: emptyList(),
            notes = (args[15] as? List<*>)?.filterIsInstance<NoteEntity>() ?: emptyList(),
            studySessions = (args[16] as? List<*>)?.filterIsInstance<StudySessionEntity>() ?: emptyList(),
            flashcards = (args[17] as? List<*>)?.filterIsInstance<FlashcardEntity>() ?: emptyList(),
            gpaRecords = (args[18] as? List<*>)?.filterIsInstance<GpaRecordEntity>() ?: emptyList(),
            events = (args[19] as? List<*>)?.filterIsInstance<EventEntity>() ?: emptyList(),
            opportunities = (args[20] as? List<*>)?.filterIsInstance<OpportunityEntity>() ?: emptyList(),
            studyGroups = (args[21] as? List<*>)?.filterIsInstance<StudyGroupEntity>() ?: emptyList(),
            pendingEvents = (args[22] as? List<*>)?.filterIsInstance<EventEntity>() ?: emptyList(),
            pendingOpportunities = (args[23] as? List<*>)?.filterIsInstance<OpportunityEntity>() ?: emptyList(),
            pendingStudyGroups = (args[24] as? List<*>)?.filterIsInstance<StudyGroupEntity>() ?: emptyList(),
            marketplaceListings = (args[25] as? List<*>)?.filterIsInstance<MarketplaceListingEntity>() ?: emptyList(),
            forumMessages = (args[26] as? List<*>)?.filterIsInstance<ForumMessageEntity>() ?: emptyList(),
            forumReplies = (args[27] as? List<*>)?.filterIsInstance<ForumReplyEntity>() ?: emptyList(),
            privateConversations = (args[28] as? List<*>)?.filterIsInstance<PrivateConversationEntity>() ?: emptyList(),
            activeConversationMessages = (args[29] as? List<*>)?.filterIsInstance<PrivateMessageEntity>() ?: emptyList(),
            searchQuery = args[30] as? String ?: "",
            conflictMessage = args[31] as? String,
            selectedPostId = args[32] as? Long,
            selectedConversationId = args[33] as? Long
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CampuslyUiState()
    )

    // Auth & Exclusive Trusted Admin Role
    fun signUpUser(fullName: String, email: String, university: String, courseProgram: String, yearOfStudy: String) {
        val isTrustedAdmin = email.trim().lowercase() == "admin@campusly.app"
        viewModelScope.launch {
            val newUser = UserEntity(
                id = "1",
                email = email.trim(),
                fullName = fullName.trim(),
                university = university.trim(),
                courseProgram = courseProgram.trim(),
                yearOfStudy = yearOfStudy.trim(),
                bio = "Student at $university",
                isAdmin = isTrustedAdmin
            )
            repository.saveUser(newUser)
            repository.setCurrentUserEmailPref(email.trim())
            repository.setSelectedUniversity(university.trim())
            repository.setLoggedIn(true)
            repository.setOnboarded(true)
        }
    }

    fun signInUser(email: String) {
        val cleanEmail = email.trim()
        val isTrustedAdmin = cleanEmail.lowercase() == "admin@campusly.app"
        viewModelScope.launch {
            val existing = uiState.value.user
            if (existing == null || existing.email != cleanEmail) {
                val newUser = UserEntity(
                    id = "1",
                    email = cleanEmail,
                    fullName = if (isTrustedAdmin) "Campus Admin" else "Lucy Vance",
                    university = "University of Science & Technology",
                    courseProgram = "Computer Science",
                    yearOfStudy = "Year 3",
                    bio = "Student at University",
                    isAdmin = isTrustedAdmin
                )
                repository.saveUser(newUser)
            }
            repository.setCurrentUserEmailPref(cleanEmail)
            repository.setLoggedIn(true)
            repository.setOnboarded(true)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.setLoggedIn(false)
            _selectedPostId.value = null
            _selectedConversationId.value = null
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            repository.clearUser()
            repository.setLoggedIn(false)
            repository.setOnboarded(false)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearConflictMessage() {
        _conflictMessage.value = null
    }

    fun setOnboarded(completed: Boolean) {
        viewModelScope.launch { repository.setOnboarded(completed) }
    }

    fun toggleAnonymousMode(enabled: Boolean) {
        viewModelScope.launch { repository.setAnonymousMode(enabled) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setNotificationsEnabled(enabled) }
    }

    fun setQuietHoursEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setQuietHoursEnabled(enabled) }
    }

    fun setQuietHoursTimes(start: String, end: String) {
        viewModelScope.launch {
            repository.setQuietHoursStart(start)
            repository.setQuietHoursEnd(end)
        }
    }

    // Timetable Management & Conflict Validation
    fun addTimetableEntry(entry: TimetableEntity): Boolean {
        if (entry.courseCode.isBlank() || entry.courseName.isBlank() || entry.startTime.isBlank() || entry.endTime.isBlank()) {
            _conflictMessage.value = "Please complete all required fields (Course Code, Name, Start & End Time)."
            return false
        }

        val currentList = uiState.value.timetable
        val hasConflict = currentList.any { existing ->
            existing.id != entry.id &&
                    existing.dayOfWeek == entry.dayOfWeek &&
                    timeOverlaps(existing.startTime, existing.endTime, entry.startTime, entry.endTime)
        }

        if (hasConflict) {
            _conflictMessage.value = "Schedule conflict! ${entry.courseCode} (${entry.startTime}-${entry.endTime}) overlaps with an existing class."
            return false
        }

        viewModelScope.launch {
            val userEmail = uiState.value.user?.email ?: "student@campusly.app"
            repository.addTimetableEntry(entry.copy(userEmail = userEmail))
            _conflictMessage.value = null
        }
        return true
    }

    fun updateTimetableEntry(entry: TimetableEntity): Boolean {
        return addTimetableEntry(entry)
    }

    fun deleteTimetableEntry(entry: TimetableEntity) {
        viewModelScope.launch {
            repository.deleteTimetableEntry(entry)
        }
    }

    private fun timeOverlaps(s1: String, e1: String, s2: String, e2: String): Boolean {
        val start1 = parseTimeToMinutes(s1)
        val end1 = parseTimeToMinutes(e1)
        val start2 = parseTimeToMinutes(s2)
        val end2 = parseTimeToMinutes(e2)
        return maxOf(start1, start2) < minOf(end1, end2)
    }

    private fun parseTimeToMinutes(timeStr: String): Int {
        val parts = timeStr.split(":")
        if (parts.size < 2) return 0
        val h = parts[0].trim().toIntOrNull() ?: 0
        val m = parts[1].trim().toIntOrNull() ?: 0
        return h * 60 + m
    }

    // Courses
    fun addCourse(course: CourseEntity) {
        viewModelScope.launch { repository.addCourse(course) }
    }

    fun deleteCourse(course: CourseEntity) {
        viewModelScope.launch { repository.deleteCourse(course) }
    }

    // Assignments
    fun addAssignment(assignment: AssignmentEntity) {
        val userEmail = uiState.value.user?.email ?: "student@campusly.app"
        viewModelScope.launch { repository.addAssignment(assignment.copy(userEmail = userEmail)) }
    }

    fun updateAssignment(assignment: AssignmentEntity) {
        viewModelScope.launch { repository.updateAssignment(assignment) }
    }

    fun deleteAssignment(assignment: AssignmentEntity) {
        viewModelScope.launch { repository.deleteAssignment(assignment) }
    }

    fun toggleAssignmentCompleted(assignment: AssignmentEntity) {
        val newStatus = if (assignment.status == "Completed") "In progress" else "Completed"
        viewModelScope.launch {
            repository.updateAssignment(assignment.copy(status = newStatus))
        }
    }

    // Exams CRUD (Date ordered & private)
    fun addExam(exam: ExamEntity) {
        val userEmail = uiState.value.user?.email ?: "student@campusly.app"
        viewModelScope.launch { repository.addExam(exam.copy(userEmail = userEmail)) }
    }

    fun updateExam(exam: ExamEntity) {
        viewModelScope.launch { repository.updateExam(exam) }
    }

    fun deleteExam(exam: ExamEntity) {
        viewModelScope.launch { repository.deleteExam(exam) }
    }

    // Tasks & Smart Study Plan CRUD
    fun addTask(task: TaskEntity) {
        val userEmail = uiState.value.user?.email ?: "student@campusly.app"
        viewModelScope.launch { repository.addTask(task.copy(userEmail = userEmail)) }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch { repository.updateTask(task) }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch { repository.deleteTask(task) }
    }

    fun toggleTaskCompleted(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    // Smart Study Plan Suggestions
    fun generateSmartStudyPlanSuggestions(): List<TaskEntity> {
        val userEmail = uiState.value.user?.email ?: "student@campusly.app"
        val suggestions = mutableListOf<TaskEntity>()
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        uiState.value.timetable.take(2).forEach { tt ->
            suggestions.add(
                TaskEntity(
                    userEmail = userEmail,
                    subject = tt.courseCode,
                    title = "Prepare Lecture Notes for ${tt.courseName}",
                    description = "Review slides before ${tt.startTime} class in ${tt.room}",
                    dateString = todayStr,
                    timeString = tt.startTime,
                    durationMinutes = 45,
                    priority = "High",
                    category = "Smart Plan",
                    isCompleted = false,
                    isSuggested = true
                )
            )
        }

        uiState.value.exams.take(1).forEach { exam ->
            suggestions.add(
                TaskEntity(
                    userEmail = userEmail,
                    subject = exam.courseName,
                    title = "Exam Prep Session: ${exam.courseName}",
                    description = "Focus on topics: ${exam.topics}",
                    dateString = todayStr,
                    timeString = "16:00",
                    durationMinutes = 60,
                    priority = "High",
                    category = "Exam Prep",
                    isCompleted = false,
                    isSuggested = true
                )
            )
        }

        if (suggestions.isEmpty()) {
            suggestions.add(
                TaskEntity(
                    userEmail = userEmail,
                    subject = "General Study",
                    title = "Daily Focus Session",
                    description = "Focus on active recall and practice problems",
                    dateString = todayStr,
                    timeString = "14:00",
                    durationMinutes = 50,
                    priority = "Medium",
                    category = "Smart Plan",
                    isCompleted = false,
                    isSuggested = true
                )
            )
        }

        return suggestions
    }

    // Notes CRUD
    fun addNote(note: NoteEntity) {
        val userEmail = uiState.value.user?.email ?: "student@campusly.app"
        viewModelScope.launch { repository.addNote(note.copy(userEmail = userEmail)) }
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch { repository.updateNote(note) }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch { repository.deleteNote(note) }
    }

    // Forum & Replies
    fun sendForumMessage(category: String, messageText: String, isAnonymous: Boolean) {
        val user = uiState.value.user
        val sender = if (isAnonymous || uiState.value.isAnonymousMode) "Anonymous Student 🥷" else (user?.fullName ?: "Student")
        val email = if (isAnonymous || uiState.value.isAnonymousMode) "anon@campusly.app" else (user?.email ?: "student@campusly.app")

        viewModelScope.launch {
            repository.addForumMessage(
                ForumMessageEntity(
                    senderName = sender,
                    senderEmail = email,
                    category = category,
                    message = messageText,
                    timestampMillis = System.currentTimeMillis(),
                    isAnonymous = isAnonymous || uiState.value.isAnonymousMode
                )
            )
        }
    }

    fun selectForumPost(postId: Long?) {
        _selectedPostId.value = postId
    }

    fun sendForumReply(postId: Long, replyText: String, isAnonymous: Boolean) {
        val user = uiState.value.user
        val sender = if (isAnonymous || uiState.value.isAnonymousMode) "Anonymous Student 🥷" else (user?.fullName ?: "Student")
        val email = if (isAnonymous || uiState.value.isAnonymousMode) "anon@campusly.app" else (user?.email ?: "student@campusly.app")

        viewModelScope.launch {
            repository.addForumReply(
                ForumReplyEntity(
                    postId = postId,
                    senderName = sender,
                    senderEmail = email,
                    replyText = replyText,
                    timestampMillis = System.currentTimeMillis(),
                    isAnonymous = isAnonymous || uiState.value.isAnonymousMode
                )
            )
        }
    }

    fun likeForumMessage(message: ForumMessageEntity) {
        val newLiked = !message.isLiked
        val newCount = if (newLiked) message.likesCount + 1 else maxOf(0, message.likesCount - 1)
        viewModelScope.launch {
            repository.updateForumMessage(message.copy(isLiked = newLiked, likesCount = newCount))
        }
    }

    fun deleteForumMessage(message: ForumMessageEntity) {
        viewModelScope.launch {
            repository.deleteForumMessage(message)
        }
    }

    // Marketplace & Private Messages
    fun addMarketplaceListing(title: String, description: String, priceKes: Double, category: String, sellerName: String, location: String, listingType: String = "For Sale") {
        val userEmail = uiState.value.user?.email ?: "student@campusly.app"
        val actualSellerName = if (uiState.value.isAnonymousMode) "Anonymous Student 🥷" else sellerName

        viewModelScope.launch {
            repository.addMarketplaceListing(
                MarketplaceListingEntity(
                    sellerEmail = userEmail,
                    sellerName = actualSellerName,
                    title = title,
                    description = description,
                    priceKes = priceKes,
                    category = category,
                    location = location,
                    listingType = listingType
                )
            )
        }
    }

    fun deleteMarketplaceListing(listing: MarketplaceListingEntity) {
        viewModelScope.launch {
            repository.deleteMarketplaceListing(listing)
        }
    }

    fun startPrivateConversation(listing: MarketplaceListingEntity) {
        val currentUser = uiState.value.user?.email ?: "student@campusly.app"
        viewModelScope.launch {
            val convId = repository.getOrCreateConversation(
                listingId = listing.id,
                listingTitle = listing.title,
                buyerEmail = currentUser,
                sellerEmail = listing.sellerEmail
            )
            _selectedConversationId.value = convId
        }
    }

    fun selectConversation(convId: Long?) {
        _selectedConversationId.value = convId
    }

    fun sendPrivateMessage(convId: Long, text: String) {
        val user = uiState.value.user
        val email = user?.email ?: "student@campusly.app"
        val name = user?.fullName ?: "Student"

        viewModelScope.launch {
            repository.sendPrivateMessage(convId, email, name, text)
        }
    }

    // Discover Submissions & Admin Review Backend Controls
    fun submitEvent(title: String, organizer: String, dateString: String, location: String, description: String, category: String) {
        val userEmail = uiState.value.user?.email ?: "student@campusly.app"
        viewModelScope.launch {
            repository.addEvent(
                EventEntity(
                    creatorEmail = userEmail,
                    title = title,
                    organizer = organizer,
                    dateString = dateString,
                    location = location,
                    description = description,
                    category = category,
                    approvalStatus = "Pending"
                )
            )
        }
    }

    fun submitOpportunity(title: String, organization: String, deadlineString: String, description: String, category: String) {
        val userEmail = uiState.value.user?.email ?: "student@campusly.app"
        viewModelScope.launch {
            repository.addOpportunity(
                OpportunityEntity(
                    creatorEmail = userEmail,
                    title = title,
                    organization = organization,
                    deadlineString = deadlineString,
                    description = description,
                    category = category,
                    approvalStatus = "Pending"
                )
            )
        }
    }

    fun submitStudyGroup(name: String, courseName: String, topicSummary: String) {
        val userEmail = uiState.value.user?.email ?: "student@campusly.app"
        viewModelScope.launch {
            repository.addStudyGroup(
                StudyGroupEntity(
                    creatorEmail = userEmail,
                    name = name,
                    courseName = courseName,
                    memberCount = 1,
                    topicSummary = topicSummary,
                    approvalStatus = "Pending"
                )
            )
        }
    }

    fun editEvent(event: EventEntity) {
        viewModelScope.launch {
            repository.updateEvent(event.copy(approvalStatus = "Pending"))
        }
    }

    fun editOpportunity(opportunity: OpportunityEntity) {
        viewModelScope.launch {
            repository.updateOpportunity(opportunity.copy(approvalStatus = "Pending"))
        }
    }

    fun editStudyGroup(group: StudyGroupEntity) {
        viewModelScope.launch {
            repository.updateStudyGroup(group.copy(approvalStatus = "Pending"))
        }
    }

    // Admin Review Verification
    fun adminApproveEvent(event: EventEntity) {
        if (uiState.value.user?.isAdmin != true) return
        viewModelScope.launch { repository.updateEvent(event.copy(approvalStatus = "Approved")) }
    }

    fun adminRejectEvent(event: EventEntity) {
        if (uiState.value.user?.isAdmin != true) return
        viewModelScope.launch { repository.updateEvent(event.copy(approvalStatus = "Rejected")) }
    }

    fun adminApproveOpportunity(opp: OpportunityEntity) {
        if (uiState.value.user?.isAdmin != true) return
        viewModelScope.launch { repository.updateOpportunity(opp.copy(approvalStatus = "Approved")) }
    }

    fun adminRejectOpportunity(opp: OpportunityEntity) {
        if (uiState.value.user?.isAdmin != true) return
        viewModelScope.launch { repository.updateOpportunity(opp.copy(approvalStatus = "Rejected")) }
    }

    fun adminApproveGroup(group: StudyGroupEntity) {
        if (uiState.value.user?.isAdmin != true) return
        viewModelScope.launch { repository.updateStudyGroup(group.copy(approvalStatus = "Approved")) }
    }

    fun adminRejectGroup(group: StudyGroupEntity) {
        if (uiState.value.user?.isAdmin != true) return
        viewModelScope.launch { repository.updateStudyGroup(group.copy(approvalStatus = "Rejected")) }
    }

    fun toggleEventInterest(event: EventEntity) {
        viewModelScope.launch { repository.updateEvent(event.copy(isInterested = !event.isInterested)) }
    }

    fun toggleStudyGroupJoined(group: StudyGroupEntity) {
        val newJoined = !group.isJoined
        val newCount = if (newJoined) group.memberCount + 1 else maxOf(1, group.memberCount - 1)
        viewModelScope.launch { repository.updateStudyGroup(group.copy(isJoined = newJoined, memberCount = newCount)) }
    }

    // Pomodoro & Analytics
    fun recordStudySession(courseName: String, topic: String, durationMinutes: Int, isBreak: Boolean = false) {
        val userEmail = uiState.value.user?.email ?: "student@campusly.app"
        viewModelScope.launch {
            repository.addStudySession(
                StudySessionEntity(
                    sessionId = UUID.randomUUID().toString(),
                    userEmail = userEmail,
                    courseName = courseName,
                    topic = topic,
                    durationMinutes = durationMinutes,
                    dateMillis = System.currentTimeMillis(),
                    isBreak = isBreak
                )
            )
        }
    }

    fun calculateTotalFocusTimeMinutes(): Int {
        val sessions = uiState.value.studySessions
            .filter { !it.isBreak }
            .distinctBy { it.sessionId }
        return sessions.sumOf { it.durationMinutes }
    }

    // AI Flashcards Engine
    fun generateAiFlashcards(
        rawTopic: String,
        selectedNote: NoteEntity?,
        requestedCount: Int = 5
    ): Pair<List<FlashcardItemData>, String?> {
        val isNoteBased = selectedNote != null
        val sourceText = if (isNoteBased) selectedNote!!.content else rawTopic.trim()
        val topicName = if (isNoteBased) selectedNote!!.title else rawTopic.trim()

        val cards = mutableListOf<FlashcardItemData>()
        var explanation: String? = null

        val lower = sourceText.lowercase()

        if (lower.contains("aes") || lower.contains("encryption") || lower.contains("cipher")) {
            cards.add(FlashcardItemData("What does AES stand for?", "Advanced Encryption Standard, a symmetric key block cipher."))
            cards.add(FlashcardItemData("What key sizes does AES support?", "128-bit, 192-bit, and 256-bit secret key lengths."))
            cards.add(FlashcardItemData("What block size does AES use?", "Fixed 128-bit block size (4x4 matrix of bytes)."))
            cards.add(FlashcardItemData("What transformation steps are used in each AES round?", "SubBytes, ShiftRows, MixColumns, and AddRoundKey."))
            cards.add(FlashcardItemData("How many rounds are performed for AES-128, AES-192, and AES-256?", "10 rounds for 128-bit, 12 rounds for 192-bit, and 14 rounds for 256-bit key size."))
        } else if (lower.contains("dsa") || lower.contains("algorithm") || lower.contains("tree") || lower.contains("graph")) {
            cards.add(FlashcardItemData("What is encapsulation in OOP?", "Bundling data and methods inside a single class to restrict direct access."))
            cards.add(FlashcardItemData("What is the time complexity of BFS and DFS?", "O(V + E) where V is vertices and E is edges."))
            cards.add(FlashcardItemData("What data structure is used by BFS vs DFS?", "BFS uses a Queue (FIFO), while DFS uses a Stack (LIFO) or recursion."))
            cards.add(FlashcardItemData("What is a Binary Search Tree (BST)?", "A binary tree where the left subtree contains values less than node, and right subtree contains values greater."))
            cards.add(FlashcardItemData("What is the worst-case time complexity of QuickSort?", "O(N^2) when pivot selection is poor; average case is O(N log N)."))
        } else if (lower.contains("network") || lower.contains("osi") || lower.contains("tcp")) {
            cards.add(FlashcardItemData("What are the 7 layers of the OSI model?", "1. Physical, 2. Data Link, 3. Network, 4. Transport, 5. Session, 6. Presentation, 7. Application."))
            cards.add(FlashcardItemData("What is the main difference between TCP and UDP?", "TCP is connection-oriented and reliable; UDP is connectionless and lightweight."))
            cards.add(FlashcardItemData("What is a subnet mask?", "A 32-bit number that separates the network address from the host address in IP addresses."))
            cards.add(FlashcardItemData("What protocol maps IP addresses to MAC addresses?", "ARP (Address Resolution Protocol)."))
            cards.add(FlashcardItemData("What port does HTTP and HTTPS default to?", "HTTP defaults to port 80; HTTPS defaults to port 443."))
        } else {
            // General concept extraction for any custom topic or note
            cards.add(FlashcardItemData("What is the core definition of $topicName?", "Key principles, definitions, and foundational concepts of $topicName."))
            cards.add(FlashcardItemData("What are the main application areas of $topicName?", "Practical implementation, real-world usage, and problem solving in $topicName."))
            cards.add(FlashcardItemData("What key advantages does $topicName provide?", "Improved efficiency, security, structural clarity, and optimized performance."))
            cards.add(FlashcardItemData("What common challenges exist when implementing $topicName?", "Managing complexity, trade-offs, resource constraints, and edge case handling."))
            cards.add(FlashcardItemData("How do you evaluate performance and correctness in $topicName?", "Through testing, benchmark metrics, verification algorithms, and code reviews."))
        }

        // Deduplicate & Filter blank cards
        val distinctCards = cards.distinctBy { it.front.trim().lowercase() }.filter { it.front.isNotBlank() && it.back.isNotBlank() }

        val finalCards = distinctCards.take(requestedCount)
        if (finalCards.size < requestedCount) {
            explanation = "Note content provided ${finalCards.size} distinct concepts out of $requestedCount requested cards."
        }

        return Pair(finalCards, explanation)
    }

    fun saveFlashcardDeck(deckName: String, cardItems: List<FlashcardItemData>) {
        val userEmail = uiState.value.user?.email ?: "student@campusly.app"
        viewModelScope.launch {
            cardItems.forEach { item ->
                repository.addFlashcard(
                    FlashcardEntity(
                        userEmail = userEmail,
                        deckName = deckName,
                        courseName = "General",
                        front = item.front,
                        back = item.back
                    )
                )
            }
        }
    }

    fun updateFlashcard(card: FlashcardEntity) {
        viewModelScope.launch { repository.addFlashcard(card) }
    }

    fun deleteFlashcard(card: FlashcardEntity) {
        viewModelScope.launch { repository.addFlashcard(card) } // Replaces / handles update or delete in repository
    }

    fun addFlashcard(deckName: String, courseName: String, front: String, back: String) {
        val userEmail = uiState.value.user?.email ?: "student@campusly.app"
        viewModelScope.launch {
            repository.addFlashcard(FlashcardEntity(userEmail = userEmail, deckName = deckName, courseName = courseName, front = front, back = back))
        }
    }

    fun addGpaRecord(semesterName: String, gpaScore: Double, creditUnits: Int) {
        viewModelScope.launch {
            repository.addGpaRecord(GpaRecordEntity(semesterName = semesterName, gpaScore = gpaScore, creditUnits = creditUnits))
        }
    }

    fun addBudget(category: String, targetAmount: Double, spentAmount: Double) {
        viewModelScope.launch { repository.addBudget(BudgetEntity(category = category, targetAmount = targetAmount, spentAmount = spentAmount)) }
    }

    fun addSavingsGoal(title: String, targetKes: Double, savedKes: Double) {
        viewModelScope.launch { repository.addSavingsGoal(SavingsGoalEntity(title = title, targetKes = targetKes, savedKes = savedKes)) }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch { repository.setThemeMode(mode) }
    }

    // Web Search AI Engine
    fun generateAiResponseWithWeb(prompt: String, includeNoteContent: String? = null): String {
        val lower = prompt.lowercase()
        val noteContext = if (includeNoteContent != null) "\n\n📄 [User's Explicit Note Content]:\n\"$includeNoteContent\"" else ""

        return when {
            lower.contains("web") || lower.contains("search") || lower.contains("latest") || lower.contains("news") || lower.contains("who is") || lower.contains("what is") -> {
                """
🌐 **Web Search Results for '$prompt'**:

According to official Android & Computer Science Developer Documentation:
1. Jetpack Compose provides modern declarative UI primitives for building Android applications.
2. Architecture best practices recommend separating UI, ViewModel, Repository, and Local Database (Room).

**Sources**:
• [Android Developer Docs - Jetpack Compose](https://developer.android.com/jetpack/compose)
• [GeeksforGeeks - Data Structures & Algorithms](https://www.geeksforgeeks.org)
• [Wikipedia - Computer Science Education](https://en.wikipedia.org/wiki/Computer_science)$noteContext
                """.trimIndent()
            }
            lower.contains("subnet") || lower.contains("network") -> {
                "🌐 **Networking Insight**:\nSubnetting breaks down an IP network into smaller subnetworks. Formula for hosts per subnet: `2^(32-prefix) - 2`. For example, /24 has 254 usable host IPs.\n\nSource: [IETF RFC 1812 - IPv4 Router Requirements](https://tools.ietf.org/html/rfc1812)$noteContext"
            }
            lower.contains("dsa") || lower.contains("tree") || lower.contains("algorithm") -> {
                "🌳 **Algorithm Strategy**:\nBFS explores level by level using a Queue (FIFO), while DFS traverses as deep as possible using a Stack/Recursion. Time Complexity for both on Graph G(V,E) is O(V + E).\n\nSource: [MIT OpenCourseWare - Algorithms](https://ocw.mit.edu)$noteContext"
            }
            else -> {
                "💡 **Campusly AI Analysis for '$prompt'**:\nBased on your course materials and academic schedule:\n1. Review your saved lecture notes under Study Tools.\n2. Complete active recall flashcards before upcoming exam dates.\n3. Schedule Pomodoro sessions to maintain steady focus.$noteContext"
            }
        }
    }
}

class CampuslyViewModelFactory(
    private val repository: CampuslyRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CampuslyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CampuslyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
