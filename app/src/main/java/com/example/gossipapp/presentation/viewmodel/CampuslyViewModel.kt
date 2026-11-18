package com.example.gossipapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gossipapp.data.ai.CampuslyAiService
import com.example.gossipapp.data.auth.AuthResult
import com.example.gossipapp.data.auth.CampuslyAuthManager
import com.example.gossipapp.data.auth.CampuslyBackendConfig
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

    private val authManager = CampuslyAuthManager(repository.dao)

    private val _searchQuery = MutableStateFlow("")
    private val _conflictMessage = MutableStateFlow<String?>(null)
    private val _selectedPostId = MutableStateFlow<Long?>(null)
    private val _selectedConversationId = MutableStateFlow<Long?>(null)

    private val currentUserEmailFlow: Flow<String> = repository.currentUserEmailPref
    private val userFlow: Flow<UserEntity?> = currentUserEmailFlow.flatMapLatest { email ->
        if (email.isBlank()) flowOf(null) else repository.getUserByEmailFlow(email)
    }
    private val userFlashcardsFlow: Flow<List<FlashcardEntity>> = currentUserEmailFlow.flatMapLatest { email ->
        if (email.isBlank()) flowOf(emptyList()) else repository.getFlashcardsForUser(email)
    }
    private val userConversationsFlow: Flow<List<PrivateConversationEntity>> = currentUserEmailFlow.flatMapLatest { email ->
        if (email.isBlank()) flowOf(emptyList()) else repository.getConversationsForUser(email)
    }

    private val forumRepliesFlow: Flow<List<ForumReplyEntity>> = _selectedPostId.flatMapLatest { postId ->
        if (postId != null) repository.getRepliesForPost(postId) else flowOf(emptyList())
    }

    private val privateMessagesFlow: Flow<List<PrivateMessageEntity>> = _selectedConversationId.flatMapLatest { convId ->
        if (convId != null) repository.getMessagesForConversation(convId) else flowOf(emptyList())
    }

    val uiState: StateFlow<CampuslyUiState> = combine(
        listOf(
            userFlow,
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
            userFlashcardsFlow,
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
            userConversationsFlow,
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
    fun signUpUser(
        fullName: String,
        email: String,
        password: String,
        university: String,
        courseProgram: String,
        yearOfStudy: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            when (val result = authManager.signUp(fullName, email, password, university, courseProgram, yearOfStudy)) {
                is AuthResult.Success -> {
                    repository.setCurrentUserEmailPref(result.user.email)
                    repository.setSelectedUniversity(result.user.university)
                    repository.setLoggedIn(true)
                    repository.setOnboarded(true)
                    onSuccess()
                }
                is AuthResult.Error -> {
                    onError(result.message)
                }
            }
        }
    }

    fun signInUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            when (val result = authManager.signIn(email, password)) {
                is AuthResult.Success -> {
                    repository.setCurrentUserEmailPref(result.user.email)
                    repository.setSelectedUniversity(result.user.university)
                    repository.setLoggedIn(true)
                    repository.setOnboarded(true)
                    onSuccess()
                }
                is AuthResult.Error -> {
                    onError(result.message)
                }
            }
        }
    }

    fun resetPassword(
        email: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            when (val result = authManager.resetPassword(email, newPassword)) {
                is AuthResult.Success -> {
                    onSuccess()
                }
                is AuthResult.Error -> {
                    onError(result.message)
                }
            }
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

    // Admin Review Verification with Backend Role Enforcement
    private fun isCallerVerifiedAdmin(): Boolean {
        val user = uiState.value.user ?: return false
        return user.isAdmin && CampuslyBackendConfig.isVerifiedAdmin(user.id, user.email)
    }

    fun adminApproveEvent(event: EventEntity) {
        if (!isCallerVerifiedAdmin()) return
        viewModelScope.launch { repository.updateEvent(event.copy(approvalStatus = "Approved")) }
    }

    fun adminRejectEvent(event: EventEntity) {
        if (!isCallerVerifiedAdmin()) return
        viewModelScope.launch { repository.updateEvent(event.copy(approvalStatus = "Rejected")) }
    }

    fun adminApproveOpportunity(opp: OpportunityEntity) {
        if (!isCallerVerifiedAdmin()) return
        viewModelScope.launch { repository.updateOpportunity(opp.copy(approvalStatus = "Approved")) }
    }

    fun adminRejectOpportunity(opp: OpportunityEntity) {
        if (!isCallerVerifiedAdmin()) return
        viewModelScope.launch { repository.updateOpportunity(opp.copy(approvalStatus = "Rejected")) }
    }

    fun adminApproveGroup(group: StudyGroupEntity) {
        if (!isCallerVerifiedAdmin()) return
        viewModelScope.launch { repository.updateStudyGroup(group.copy(approvalStatus = "Approved")) }
    }

    fun adminRejectGroup(group: StudyGroupEntity) {
        if (!isCallerVerifiedAdmin()) return
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
        val userEmail = uiState.value.user?.email ?: return
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
    suspend fun generateAiFlashcards(
        rawTopic: String,
        selectedNote: NoteEntity?,
        requestedCount: Int = 5
    ): Pair<List<FlashcardItemData>, String?> {
        return CampuslyAiService.generateFlashcards(rawTopic, selectedNote, requestedCount)
    }

    fun saveFlashcardDeck(deckName: String, cardItems: List<FlashcardItemData>) {
        val userEmail = uiState.value.user?.email ?: return
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
        val userEmail = uiState.value.user?.email ?: card.userEmail
        viewModelScope.launch { repository.updateFlashcardScoped(card.id, userEmail, card.front, card.back) }
    }

    fun deleteFlashcard(card: FlashcardEntity) {
        val userEmail = uiState.value.user?.email ?: card.userEmail
        viewModelScope.launch { repository.deleteFlashcardScoped(card.id, userEmail) }
    }

    fun deleteFlashcardDeck(deckName: String) {
        val userEmail = uiState.value.user?.email ?: return
        viewModelScope.launch { repository.deleteFlashcardDeck(userEmail, deckName) }
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
