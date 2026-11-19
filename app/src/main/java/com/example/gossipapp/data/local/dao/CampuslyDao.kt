package com.example.gossipapp.data.local.dao

import androidx.room.*
import com.example.gossipapp.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CampuslyDao {

    // User
    @Query("SELECT * FROM users LIMIT 1")
    fun getUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    fun getUserByEmailFlow(email: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun clearUser()

    // Courses
    @Query("SELECT * FROM courses ORDER BY name ASC")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity): Long

    @Delete
    suspend fun deleteCourse(course: CourseEntity)

    // Timetable
    @Query("SELECT * FROM timetable WHERE userEmail = :userEmail ORDER BY dayOfWeek ASC, startTime ASC")
    fun getTimetableForUser(userEmail: String): Flow<List<TimetableEntity>>

    @Query("SELECT * FROM timetable ORDER BY dayOfWeek ASC, startTime ASC")
    fun getAllTimetable(): Flow<List<TimetableEntity>>

    @Query("SELECT * FROM timetable WHERE dayOfWeek = :dayOfWeek ORDER BY startTime ASC")
    fun getTimetableForDay(dayOfWeek: Int): Flow<List<TimetableEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetable(timetable: TimetableEntity): Long

    @Delete
    suspend fun deleteTimetable(timetable: TimetableEntity)

    // Assignments
    @Query("SELECT * FROM assignments WHERE userEmail = :userEmail ORDER BY dueDateMillis ASC")
    fun getAssignmentsForUser(userEmail: String): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments ORDER BY dueDateMillis ASC")
    fun getAllAssignments(): Flow<List<AssignmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: AssignmentEntity): Long

    @Update
    suspend fun updateAssignment(assignment: AssignmentEntity)

    @Delete
    suspend fun deleteAssignment(assignment: AssignmentEntity)

    // Exams
    @Query("SELECT * FROM exams WHERE userEmail = :userEmail ORDER BY examDateMillis ASC")
    fun getExamsForUser(userEmail: String): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams ORDER BY examDateMillis ASC")
    fun getAllExams(): Flow<List<ExamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamEntity): Long

    @Update
    suspend fun updateExam(exam: ExamEntity)

    @Delete
    suspend fun deleteExam(exam: ExamEntity)

    // Tasks & Smart Study Plans
    @Query("SELECT * FROM tasks WHERE userEmail = :userEmail ORDER BY isCompleted ASC, deadlineMillis ASC")
    fun getTasksForUser(userEmail: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, deadlineMillis ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id AND userEmail = :userEmail")
    suspend fun deleteTaskScoped(id: Long, userEmail: String): Int

    @Query("UPDATE tasks SET title = :title, subject = :subject, description = :description, priority = :priority, isCompleted = :isCompleted WHERE id = :id AND userEmail = :userEmail")
    suspend fun updateTaskScoped(id: Long, userEmail: String, title: String, subject: String, description: String, priority: String, isCompleted: Boolean): Int

    // Notes
    @Query("SELECT * FROM notes WHERE userEmail = :userEmail ORDER BY isPinned DESC, id DESC")
    fun getNotesForUser(userEmail: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes ORDER BY isPinned DESC, id DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    // Study Sessions
    @Query("SELECT * FROM study_sessions WHERE userEmail = :userEmail ORDER BY dateMillis DESC")
    fun getStudySessionsForUser(userEmail: String): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions ORDER BY dateMillis DESC")
    fun getAllStudySessions(): Flow<List<StudySessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudySession(session: StudySessionEntity): Long

    // Flashcards
    @Query("SELECT * FROM flashcards WHERE userEmail = :userEmail ORDER BY id ASC")
    fun getFlashcardsForUser(userEmail: String): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards ORDER BY id ASC")
    fun getAllFlashcards(): Flow<List<FlashcardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: FlashcardEntity): Long

    @Update
    suspend fun updateFlashcard(flashcard: FlashcardEntity)

    @Delete
    suspend fun deleteFlashcard(flashcard: FlashcardEntity)

    @Query("DELETE FROM flashcards WHERE userEmail = :userEmail AND deckName = :deckName")
    suspend fun deleteFlashcardDeck(userEmail: String, deckName: String)

    @Query("DELETE FROM flashcards WHERE id = :id AND userEmail = :userEmail")
    suspend fun deleteFlashcardScoped(id: Long, userEmail: String)

    @Query("UPDATE flashcards SET front = :front, back = :back WHERE id = :id AND userEmail = :userEmail")
    suspend fun updateFlashcardScoped(id: Long, userEmail: String, front: String, back: String)

    // GPA Records
    @Query("SELECT * FROM gpa_records ORDER BY id ASC")
    fun getAllGpaRecords(): Flow<List<GpaRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGpaRecord(record: GpaRecordEntity): Long

    // Events (Approved vs Pending vs User)
    @Query("SELECT * FROM events WHERE approvalStatus = 'Approved' ORDER BY id ASC")
    fun getApprovedEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE approvalStatus = 'Pending' ORDER BY id ASC")
    fun getPendingEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE creatorEmail = :email ORDER BY id ASC")
    fun getUserEvents(email: String): Flow<List<EventEntity>>

    @Query("SELECT * FROM events ORDER BY id ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity): Long

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    // Opportunities (Approved vs Pending vs User)
    @Query("SELECT * FROM opportunities WHERE approvalStatus = 'Approved' ORDER BY id ASC")
    fun getApprovedOpportunities(): Flow<List<OpportunityEntity>>

    @Query("SELECT * FROM opportunities WHERE approvalStatus = 'Pending' ORDER BY id ASC")
    fun getPendingOpportunities(): Flow<List<OpportunityEntity>>

    @Query("SELECT * FROM opportunities WHERE creatorEmail = :email ORDER BY id ASC")
    fun getUserOpportunities(email: String): Flow<List<OpportunityEntity>>

    @Query("SELECT * FROM opportunities ORDER BY id ASC")
    fun getAllOpportunities(): Flow<List<OpportunityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOpportunity(opportunity: OpportunityEntity): Long

    @Update
    suspend fun updateOpportunity(opportunity: OpportunityEntity)

    @Delete
    suspend fun deleteOpportunity(opportunity: OpportunityEntity)

    // Study Groups (Approved vs Pending vs User)
    @Query("SELECT * FROM study_groups WHERE approvalStatus = 'Approved' ORDER BY id ASC")
    fun getApprovedStudyGroups(): Flow<List<StudyGroupEntity>>

    @Query("SELECT * FROM study_groups WHERE approvalStatus = 'Pending' ORDER BY id ASC")
    fun getPendingStudyGroups(): Flow<List<StudyGroupEntity>>

    @Query("SELECT * FROM study_groups WHERE creatorEmail = :email ORDER BY id ASC")
    fun getUserStudyGroups(email: String): Flow<List<StudyGroupEntity>>

    @Query("SELECT * FROM study_groups ORDER BY id ASC")
    fun getAllStudyGroups(): Flow<List<StudyGroupEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyGroup(group: StudyGroupEntity): Long

    @Update
    suspend fun updateStudyGroup(group: StudyGroupEntity)

    @Delete
    suspend fun deleteStudyGroup(group: StudyGroupEntity)

    // Marketplace Listings
    @Query("SELECT * FROM marketplace_listings ORDER BY id DESC")
    fun getAllMarketplaceListings(): Flow<List<MarketplaceListingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarketplaceListing(listing: MarketplaceListingEntity): Long

    @Update
    suspend fun updateMarketplaceListing(listing: MarketplaceListingEntity)

    @Delete
    suspend fun deleteMarketplaceListing(listing: MarketplaceListingEntity)

    // Private Conversations & Messages
    @Query("SELECT * FROM private_conversations WHERE participant1Email = :email OR participant2Email = :email ORDER BY lastMessageTimestamp DESC")
    fun getConversationsForUser(email: String): Flow<List<PrivateConversationEntity>>

    @Query("SELECT * FROM private_conversations WHERE listingId = :listingId AND ((participant1Email = :e1 AND participant2Email = :e2) OR (participant1Email = :e2 AND participant2Email = :e1)) LIMIT 1")
    suspend fun getConversationBetween(listingId: Long, e1: String, e2: String): PrivateConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: PrivateConversationEntity): Long

    @Update
    suspend fun updateConversation(conversation: PrivateConversationEntity)

    @Query("SELECT * FROM private_messages WHERE conversationId = :convId ORDER BY timestampMillis ASC")
    fun getMessagesForConversation(convId: Long): Flow<List<PrivateMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrivateMessage(message: PrivateMessageEntity): Long

    // Budgets & Savings
    @Query("SELECT * FROM budgets ORDER BY id ASC")
    fun getAllBudgets(): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity): Long

    @Query("SELECT * FROM savings_goals ORDER BY id ASC")
    fun getAllSavingsGoals(): Flow<List<SavingsGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsGoal(goal: SavingsGoalEntity): Long

    // Attendance
    @Query("SELECT * FROM attendance_records ORDER BY id ASC")
    fun getAllAttendance(): Flow<List<AttendanceRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(record: AttendanceRecordEntity): Long

    @Update
    suspend fun updateAttendance(record: AttendanceRecordEntity)

    // Forum Messages & Replies
    @Query("SELECT * FROM forum_messages ORDER BY timestampMillis DESC")
    fun getAllForumMessages(): Flow<List<ForumMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForumMessage(message: ForumMessageEntity): Long

    @Update
    suspend fun updateForumMessage(message: ForumMessageEntity)

    @Delete
    suspend fun deleteForumMessage(message: ForumMessageEntity)

    @Query("SELECT * FROM forum_replies WHERE postId = :postId ORDER BY timestampMillis ASC")
    fun getRepliesForPost(postId: Long): Flow<List<ForumReplyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForumReply(reply: ForumReplyEntity): Long

    @Delete
    suspend fun deleteForumReply(reply: ForumReplyEntity)
}
