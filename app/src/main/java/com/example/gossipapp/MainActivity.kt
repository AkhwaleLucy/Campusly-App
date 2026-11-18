package com.example.gossipapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gossipapp.data.local.CampuslyDatabase
import com.example.gossipapp.data.local.entity.*
import com.example.gossipapp.data.notifications.CampuslyNotificationManager
import com.example.gossipapp.data.preferences.UserPreferences
import com.example.gossipapp.data.repository.CampuslyRepository
import com.example.gossipapp.presentation.components.*
import com.example.gossipapp.presentation.screens.auth.AuthScreen
import com.example.gossipapp.presentation.screens.discover.DiscoverScreen
import com.example.gossipapp.presentation.screens.forum.ForumScreen
import com.example.gossipapp.presentation.screens.home.HomeScreen
import com.example.gossipapp.presentation.screens.onboarding.OnboardingScreen
import com.example.gossipapp.presentation.screens.planner.PlannerScreen
import com.example.gossipapp.presentation.screens.profile.ProfileScreen
import com.example.gossipapp.presentation.screens.study.StudyScreen
import com.example.gossipapp.presentation.viewmodel.CampuslyViewModel
import com.example.gossipapp.presentation.viewmodel.CampuslyViewModelFactory
import com.example.gossipapp.ui.theme.GossipAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current.applicationContext
            val database = remember { CampuslyDatabase.getDatabase(context) }
            val preferences = remember { UserPreferences(context) }
            val repository = remember { CampuslyRepository(database.dao(), preferences) }
            val viewModelFactory = remember { CampuslyViewModelFactory(repository) }
            val notificationManager = remember { CampuslyNotificationManager(context) }

            val viewModel: CampuslyViewModel = viewModel(factory = viewModelFactory)
            val uiState by viewModel.uiState.collectAsState()

            var currentRoute by remember { mutableStateOf("auth") }

            // Account Authentication Check: Require valid login before opening main app
            LaunchedEffect(uiState.isLoggedIn, uiState.isOnboarded) {
                if (uiState.isLoggedIn && uiState.isOnboarded) {
                    currentRoute = "main"
                } else if (!uiState.isLoggedIn) {
                    currentRoute = "auth"
                }
            }

            GossipAppTheme {
                when (currentRoute) {
                    "onboarding" -> {
                        OnboardingScreen(
                            onFinishOnboarding = {
                                viewModel.setOnboarded(true)
                                currentRoute = "main"
                            }
                        )
                    }
                    "auth" -> {
                        AuthScreen(
                            onSignUp = { name, email, pass, uni, course, year, onError ->
                                viewModel.signUpUser(name, email, pass, uni, course, year, { currentRoute = "main" }, onError)
                            },
                            onSignIn = { email, pass, onError ->
                                viewModel.signInUser(email, pass, { currentRoute = "main" }, onError)
                            },
                            onResetPassword = { email, pass, onSuccess, onError ->
                                viewModel.resetPassword(email, pass, onSuccess, onError)
                            }
                        )
                    }
                    "main" -> {
                        if (uiState.isLoggedIn) {
                            MainAppContent(
                                viewModel = viewModel,
                                notificationManager = notificationManager,
                                onLogout = {
                                    viewModel.logout()
                                    currentRoute = "auth"
                                }
                            )
                        } else {
                            AuthScreen(
                                onSignUp = { name, email, pass, uni, course, year, onError ->
                                    viewModel.signUpUser(name, email, pass, uni, course, year, { currentRoute = "main" }, onError)
                                },
                                onSignIn = { email, pass, onError ->
                                    viewModel.signInUser(email, pass, { currentRoute = "main" }, onError)
                                },
                                onResetPassword = { email, pass, onSuccess, onError ->
                                    viewModel.resetPassword(email, pass, onSuccess, onError)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppContent(
    viewModel: CampuslyViewModel,
    notificationManager: CampuslyNotificationManager,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0=Home, 1=Planner, 2=Forum, 3=Study, 4=Discover, 5=Profile

    // Dialog state controllers
    var showAddAssignment by remember { mutableStateOf(false) }
    var showAddCourse by remember { mutableStateOf(false) }
    var showAddTimetable by remember { mutableStateOf(false) }
    var showAddExam by remember { mutableStateOf(false) }
    var showAddTask by remember { mutableStateOf(false) }
    var showAddNote by remember { mutableStateOf(false) }
    var showSmartPlanSuggestions by remember { mutableStateOf(false) }

    // Flashcard Generator Dialog State
    var showFlashcardGenerator by remember { mutableStateOf(false) }
    var generatorTopic by remember { mutableStateOf("AES Encryption") }
    var generatorNote by remember { mutableStateOf<NoteEntity?>(null) }
    var generatorCount by remember { mutableIntStateOf(5) }

    var showSubmitEvent by remember { mutableStateOf(false) }
    var showSubmitOpportunity by remember { mutableStateOf(false) }
    var showSubmitGroup by remember { mutableStateOf(false) }

    var editingAssignment by remember { mutableStateOf<AssignmentEntity?>(null) }
    var editingCourse by remember { mutableStateOf<CourseEntity?>(null) }
    var editingTimetable by remember { mutableStateOf<TimetableEntity?>(null) }
    var editingExam by remember { mutableStateOf<ExamEntity?>(null) }
    var editingTask by remember { mutableStateOf<TaskEntity?>(null) }
    var editingNote by remember { mutableStateOf<NoteEntity?>(null) }

    var showStudyTimer by remember { mutableStateOf(false) }
    var showGpaCalculator by remember { mutableStateOf(false) }
    var showAiAssistant by remember { mutableStateOf(false) }
    var showAddMarketplace by remember { mutableStateOf(false) }
    var showBudgetTracker by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.nav_home)) },
                    label = { Text(stringResource(R.string.nav_home)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = stringResource(R.string.nav_planner)) },
                    label = { Text(stringResource(R.string.nav_planner)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Campaign, contentDescription = stringResource(R.string.nav_forum)) },
                    label = { Text(stringResource(R.string.nav_forum)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.School, contentDescription = stringResource(R.string.nav_study)) },
                    label = { Text(stringResource(R.string.nav_study)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.CompassCalibration, contentDescription = stringResource(R.string.nav_discover)) },
                    label = { Text(stringResource(R.string.nav_discover)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 5,
                    onClick = { selectedTab = 5 },
                    icon = { Icon(Icons.Default.Person, contentDescription = stringResource(R.string.nav_profile)) },
                    label = { Text(stringResource(R.string.nav_profile)) }
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    uiState = uiState,
                    onOpenAddAssignment = { showAddAssignment = true },
                    onOpenAddNote = { showAddNote = true },
                    onOpenAddExam = { showAddExam = true },
                    onOpenAddTask = { showAddTask = true },
                    onOpenSmartPlanSuggestions = { showSmartPlanSuggestions = true },
                    onOpenStudyTimer = { showStudyTimer = true },
                    onOpenGpaCalculator = { showGpaCalculator = true },
                    onEditAssignment = { editingAssignment = it },
                    onDeleteAssignment = { viewModel.deleteAssignment(it) },
                    onToggleAssignment = { viewModel.toggleAssignmentCompleted(it) },
                    onToggleTask = { viewModel.toggleTaskCompleted(it) },
                    onEditTask = { editingTask = it },
                    onDeleteTask = { viewModel.deleteTask(it) }
                )
                1 -> PlannerScreen(
                    uiState = uiState,
                    onOpenAddTimetable = { showAddTimetable = true },
                    onOpenAddAssignment = { showAddAssignment = true },
                    onOpenAddExam = { showAddExam = true },
                    onOpenAddTask = { showAddTask = true },
                    onToggleAssignment = { viewModel.toggleAssignmentCompleted(it) },
                    onEditAssignment = { editingAssignment = it },
                    onDeleteAssignment = { viewModel.deleteAssignment(it) },
                    onEditTimetable = { editingTimetable = it },
                    onDeleteTimetable = { viewModel.deleteTimetableEntry(it) },
                    onEditExam = { editingExam = it },
                    onDeleteExam = { viewModel.deleteExam(it) },
                    onToggleTask = { viewModel.toggleTaskCompleted(it) },
                    onEditTask = { editingTask = it },
                    onDeleteTask = { viewModel.deleteTask(it) }
                )
                2 -> ForumScreen(
                    uiState = uiState,
                    onSendBroadcast = { category, msg, anon -> viewModel.sendForumMessage(category, msg, anon) },
                    onSelectPost = { viewModel.selectForumPost(it) },
                    onSendReply = { postId, text, anon -> viewModel.sendForumReply(postId, text, anon) },
                    onLikeMessage = { viewModel.likeForumMessage(it) },
                    onDeleteMessage = { viewModel.deleteForumMessage(it) }
                )
                3 -> StudyScreen(
                    uiState = uiState,
                    onOpenAddNote = { showAddNote = true },
                    onOpenEditNote = { editingNote = it },
                    onDeleteNote = { viewModel.deleteNote(it) },
                    onOpenStudyTimer = { showStudyTimer = true },
                    onOpenAiAssistant = { showAiAssistant = true },
                    onOpenFlashcardGenerator = {
                        generatorTopic = "Database Normalization"
                        generatorNote = null
                        generatorCount = 7
                        showFlashcardGenerator = true
                    },
                    onDeleteFlashcardCard = { viewModel.deleteFlashcard(it) },
                    onSaveEditedFlashcardCard = { viewModel.updateFlashcard(it) },
                    onDeleteFlashcardDeck = { viewModel.deleteFlashcardDeck(it) }
                )
                4 -> DiscoverScreen(
                    uiState = uiState,
                    onToggleEventInterest = { viewModel.toggleEventInterest(it) },
                    onToggleGroupJoined = { viewModel.toggleStudyGroupJoined(it) },
                    onOpenAddMarketplaceListing = { showAddMarketplace = true },
                    onDeleteMarketplaceListing = { viewModel.deleteMarketplaceListing(it) },
                    onMessageSeller = { listing -> viewModel.startPrivateConversation(listing) },
                    onOpenSubmitEvent = { showSubmitEvent = true },
                    onOpenSubmitOpportunity = { showSubmitOpportunity = true },
                    onOpenSubmitGroup = { showSubmitGroup = true },
                    onAdminApproveEvent = { viewModel.adminApproveEvent(it) },
                    onAdminRejectEvent = { viewModel.adminRejectEvent(it) },
                    onAdminApproveOpportunity = { viewModel.adminApproveOpportunity(it) },
                    onAdminRejectOpportunity = { viewModel.adminRejectOpportunity(it) },
                    onAdminApproveGroup = { viewModel.adminApproveGroup(it) },
                    onAdminRejectGroup = { viewModel.adminRejectGroup(it) }
                )
                5 -> ProfileScreen(
                    uiState = uiState,
                    onOpenGpaCalculator = { showGpaCalculator = true },
                    onOpenBudgetTracker = { showBudgetTracker = true },
                    onSetThemeMode = { viewModel.setThemeMode(it) },
                    onToggleNotifications = { viewModel.setNotificationsEnabled(it) },
                    onToggleQuietHours = { viewModel.setQuietHoursEnabled(it) },
                    onUpdateQuietHoursTimes = { start, end -> viewModel.setQuietHoursTimes(start, end) },
                    onTestNotification = {
                        notificationManager.sendNotification(
                            title = "Campusly Alert 🔔",
                            body = "Your personal notification settings and quiet hours schedule are active!",
                            isQuietHoursEnabled = uiState.quietHoursEnabled,
                            quietHoursStart = uiState.quietHoursStart,
                            quietHoursEnd = uiState.quietHoursEnd
                        )
                    },
                    onToggleAnonymousMode = { viewModel.toggleAnonymousMode(it) },
                    onDeleteAccount = {
                        viewModel.deleteAccount()
                        onLogout()
                    },
                    onLogout = onLogout
                )
            }
        }
    }

    // Modal Add & Edit Dialog Overlays
    if (showAddAssignment) {
        AddAssignmentDialog(
            courses = uiState.courses,
            onDismiss = { showAddAssignment = false },
            onAddAssignment = { viewModel.addAssignment(it) }
        )
    }

    if (showAddCourse) {
        AddCourseDialog(
            onDismiss = { showAddCourse = false },
            onAddCourse = { viewModel.addCourse(it) }
        )
    }

    if (showAddTimetable) {
        AddTimetableDialog(
            courses = uiState.courses,
            conflictMessage = uiState.conflictMessage,
            onDismiss = { showAddTimetable = false },
            onAddTimetable = { entry -> viewModel.addTimetableEntry(entry) },
            onClearConflict = { viewModel.clearConflictMessage() }
        )
    }

    if (showAddExam) {
        AddExamDialog(
            courses = uiState.courses,
            onDismiss = { showAddExam = false },
            onAddExam = { viewModel.addExam(it) }
        )
    }

    if (showAddTask) {
        AddTaskDialog(
            onDismiss = { showAddTask = false },
            onAddTask = { viewModel.addTask(it) }
        )
    }

    if (showSmartPlanSuggestions) {
        val suggestions = viewModel.generateSmartStudyPlanSuggestions()
        SmartPlanDialog(
            suggestions = suggestions,
            onDismiss = { showSmartPlanSuggestions = false },
            onSaveSelectedPlan = { selectedTasks ->
                selectedTasks.forEach { task -> viewModel.addTask(task) }
            }
        )
    }

    if (showAddNote) {
        AddNoteDialog(
            courses = uiState.courses,
            onDismiss = { showAddNote = false },
            onAddNote = { viewModel.addNote(it) }
        )
    }

    if (showSubmitEvent) {
        SubmitEventDialog(
            onDismiss = { showSubmitEvent = false },
            onSubmit = { title, org, date, loc, desc, cat ->
                viewModel.submitEvent(title, org, date, loc, desc, cat)
            }
        )
    }

    if (showSubmitOpportunity) {
        SubmitOpportunityDialog(
            onDismiss = { showSubmitOpportunity = false },
            onSubmit = { title, org, dl, desc, cat ->
                viewModel.submitOpportunity(title, org, dl, desc, cat)
            }
        )
    }

    if (showSubmitGroup) {
        SubmitStudyGroupDialog(
            onDismiss = { showSubmitGroup = false },
            onSubmit = { name, course, summary ->
                viewModel.submitStudyGroup(name, course, summary)
            }
        )
    }

    // Edit Dialog Overlays
    editingAssignment?.let { assignment ->
        EditAssignmentDialog(
            assignment = assignment,
            onDismiss = { editingAssignment = null },
            onSaveAssignment = { viewModel.updateAssignment(it) },
            onDeleteAssignment = { viewModel.deleteAssignment(it) }
        )
    }

    editingCourse?.let { course ->
        EditCourseDialog(
            course = course,
            onDismiss = { editingCourse = null },
            onSaveCourse = { viewModel.addCourse(it) },
            onDeleteCourse = { viewModel.deleteCourse(it) }
        )
    }

    editingTimetable?.let { timetable ->
        EditTimetableDialog(
            entry = timetable,
            onDismiss = { editingTimetable = null },
            onSaveTimetable = { viewModel.updateTimetableEntry(it) },
            onDeleteTimetable = { viewModel.deleteTimetableEntry(it) }
        )
    }

    editingExam?.let { exam ->
        EditExamDialog(
            exam = exam,
            onDismiss = { editingExam = null },
            onSaveExam = { viewModel.updateExam(it) },
            onDeleteExam = { viewModel.deleteExam(it) }
        )
    }

    editingTask?.let { task ->
        EditTaskDialog(
            task = task,
            onDismiss = { editingTask = null },
            onSaveTask = { viewModel.updateTask(it) },
            onDeleteTask = { viewModel.deleteTask(it) }
        )
    }

    editingNote?.let { note ->
        EditNoteDialog(
            note = note,
            onDismiss = { editingNote = null },
            onSaveNote = { viewModel.updateNote(it) },
            onDeleteNote = { viewModel.deleteNote(it) },
            onGenerateFlashcardsFromNote = { selectedNote ->
                generatorTopic = selectedNote.title
                generatorNote = selectedNote
                generatorCount = 5
                showFlashcardGenerator = true
            }
        )
    }

    // AI Flashcard Generator & Preview Dialog Overlay
    if (showFlashcardGenerator) {
        AiFlashcardGeneratorDialog(
            initialTopic = generatorTopic,
            initialNote = generatorNote,
            notes = uiState.notes,
            onDismiss = { showFlashcardGenerator = false },
            onGenerateCards = { topic, note, count ->
                viewModel.generateAiFlashcards(topic, note, count)
            },
            onSaveDeck = { name, cards ->
                viewModel.saveFlashcardDeck(name, cards)
            }
        )
    }

    // Private Conversation Dialog
    if (uiState.selectedConversationId != null) {
        val conv = uiState.privateConversations.find { it.id == uiState.selectedConversationId }
        PrivateMessageDialog(
            conversation = conv,
            messages = uiState.activeConversationMessages,
            currentUserEmail = uiState.user?.email ?: "",
            onDismiss = { viewModel.selectConversation(null) },
            onSendMessage = { convId, text -> viewModel.sendPrivateMessage(convId, text) }
        )
    }

    if (showStudyTimer) {
        StudyTimerDialog(
            courses = uiState.courses,
            onDismiss = { showStudyTimer = false },
            onRecordSession = { course, topic, duration ->
                viewModel.recordStudySession(course, topic, duration)
            }
        )
    }

    if (showGpaCalculator) {
        GpaCalculatorDialog(
            onDismiss = { showGpaCalculator = false },
            onAddGpaRecord = { semester, score, credits ->
                viewModel.addGpaRecord(semester, score, credits)
            }
        )
    }

    if (showAiAssistant) {
        AiAssistantDialog(
            notes = uiState.notes,
            onDismiss = { showAiAssistant = false },
            onOpenFlashcardGenerator = { topic, note, count ->
                generatorTopic = topic
                generatorNote = note
                generatorCount = count
                showFlashcardGenerator = true
            },
            onGenerateAiResponse = { prompt, noteContent ->
                viewModel.generateAiResponseWithWeb(prompt, noteContent)
            }
        )
    }

    if (showAddMarketplace) {
        AddMarketplaceListingDialog(
            onDismiss = { showAddMarketplace = false },
            onAddListing = { title, desc, price, cat, seller, loc ->
                viewModel.addMarketplaceListing(title, desc, price, cat, seller, loc)
            }
        )
    }

    if (showBudgetTracker) {
        BudgetTrackerDialog(
            onDismiss = { showBudgetTracker = false },
            onAddBudget = { category, target, spent -> viewModel.addBudget(category, target, spent) },
            onAddSavingsGoal = { title, target, saved -> viewModel.addSavingsGoal(title, target, saved) }
        )
    }
}
