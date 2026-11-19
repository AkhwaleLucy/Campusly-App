package com.example.gossipapp.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gossipapp.R
import com.example.gossipapp.data.local.entity.AssignmentEntity
import com.example.gossipapp.data.local.entity.ExamEntity
import com.example.gossipapp.data.local.entity.TaskEntity
import com.example.gossipapp.data.local.entity.TimetableEntity
import com.example.gossipapp.presentation.viewmodel.ActiveTimerState
import com.example.gossipapp.presentation.viewmodel.CampuslyUiState
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

sealed class TopPriorityItem(
    val typeName: String,
    val title: String,
    val subtitle: String,
    val targetMillis: Long,
    val badgeLabel: String
) {
    class HighTask(task: TaskEntity, now: Long) : TopPriorityItem(
        typeName = "Task",
        title = task.title,
        subtitle = "${task.subject} • ${task.description}",
        targetMillis = getTaskDeadlineMillis(task),
        badgeLabel = "Task · Due in ${formatRemainingTimeLabel(getTaskDeadlineMillis(task), now)}"
    )

    class HighAssignment(assignment: AssignmentEntity, deadline: Long, now: Long) : TopPriorityItem(
        typeName = "Assignment",
        title = assignment.title,
        subtitle = assignment.courseName,
        targetMillis = deadline,
        badgeLabel = "Assignment · Due in ${formatRemainingTimeLabel(deadline, now)}"
    )

    class UpcomingExam(exam: ExamEntity, examStart: Long, now: Long) : TopPriorityItem(
        typeName = "Exam",
        title = exam.courseName,
        subtitle = "Venue: ${exam.venue} • ${exam.topics}",
        targetMillis = examStart,
        badgeLabel = "Exam · Starts in ${formatRemainingTimeLabel(examStart, now)}"
    )

    class NextClass(entry: TimetableEntity, classStart: Long, now: Long) : TopPriorityItem(
        typeName = "Class",
        title = "${entry.courseCode} - ${entry.courseName}",
        subtitle = "Room: ${entry.room} • Instructor: ${entry.instructor}",
        targetMillis = classStart,
        badgeLabel = "Class · Starts in ${formatRemainingTimeLabel(classStart, now)}"
    )
}

fun getTaskDeadlineMillis(task: TaskEntity): Long {
    if (task.deadlineMillis > 0) return task.deadlineMillis
    return System.currentTimeMillis() + 86400000L
}

fun getAssignmentDeadlineMillis(dueDateMillis: Long, timeString: String): Long {
    val cal = Calendar.getInstance().apply { timeInMillis = dueDateMillis }
    val timeParts = timeString.split(":")
    if (timeParts.size >= 2) {
        val h = timeParts[0].trim().toIntOrNull() ?: 23
        val m = timeParts[1].trim().toIntOrNull() ?: 59
        cal.set(Calendar.HOUR_OF_DAY, h)
        cal.set(Calendar.MINUTE, m)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}

fun getExamStartMillis(examDateMillis: Long, timeString: String): Long {
    val cal = Calendar.getInstance().apply { timeInMillis = examDateMillis }
    val timeLower = timeString.lowercase().trim()
    val isPm = timeLower.contains("pm")
    val isAm = timeLower.contains("am")
    val cleanTime = timeLower.replace("am", "").replace("pm", "").trim()
    val timeParts = cleanTime.split(":")
    if (timeParts.size >= 2) {
        var h = timeParts[0].trim().toIntOrNull() ?: 9
        val m = timeParts[1].trim().toIntOrNull() ?: 0
        if (isPm && h < 12) h += 12
        if (isAm && h == 12) h = 0
        cal.set(Calendar.HOUR_OF_DAY, h)
        cal.set(Calendar.MINUTE, m)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}

fun getNextClassOccurrenceMillis(dayOfWeek: Int, startTimeStr: String, nowMillis: Long): Long {
    val cal = Calendar.getInstance().apply { timeInMillis = nowMillis }
    val currentDayOfWeek = when (cal.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> 1
        Calendar.TUESDAY -> 2
        Calendar.WEDNESDAY -> 3
        Calendar.THURSDAY -> 4
        Calendar.FRIDAY -> 5
        Calendar.SATURDAY -> 6
        Calendar.SUNDAY -> 7
        else -> 1
    }

    val timeParts = startTimeStr.split(":")
    val h = timeParts.getOrNull(0)?.trim()?.toIntOrNull() ?: 9
    val m = timeParts.getOrNull(1)?.trim()?.toIntOrNull() ?: 0

    cal.set(Calendar.HOUR_OF_DAY, h)
    cal.set(Calendar.MINUTE, m)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)

    var daysDiff = dayOfWeek - currentDayOfWeek
    if (daysDiff < 0 || (daysDiff == 0 && cal.timeInMillis <= nowMillis)) {
        daysDiff += 7
    }

    cal.add(Calendar.DAY_OF_YEAR, daysDiff)
    return cal.timeInMillis
}

fun formatRemainingTimeLabel(targetMillis: Long, nowMillis: Long): String {
    val diffMs = targetMillis - nowMillis
    if (diffMs <= 0) return "Expired"
    val diffSec = diffMs / 1000
    val diffMins = diffSec / 60
    val diffHours = diffMins / 60
    val diffDays = diffHours / 24

    return when {
        diffDays > 0 -> {
            val remHours = diffHours % 24
            if (remHours > 0) "${diffDays}d ${remHours}h" else "${diffDays}d"
        }
        diffHours > 0 -> {
            val remMins = diffMins % 60
            if (remMins > 0) "${diffHours}h ${remMins}m" else "${diffHours}h"
        }
        diffMins > 0 -> "${diffMins}m"
        else -> "< 1m"
    }
}

@Composable
fun HomeScreen(
    uiState: CampuslyUiState,
    timerState: ActiveTimerState = ActiveTimerState(),
    onOpenAddAssignment: () -> Unit,
    onOpenAddNote: () -> Unit,
    onOpenAddExam: () -> Unit,
    onOpenAddTask: () -> Unit,
    onOpenSmartPlanSuggestions: () -> Unit,
    onOpenStudyTimer: () -> Unit,
    onOpenGpaCalculator: () -> Unit,
    onEditAssignment: (AssignmentEntity) -> Unit,
    onDeleteAssignment: (AssignmentEntity) -> Unit,
    onToggleAssignment: (AssignmentEntity) -> Unit,
    onToggleTask: (TaskEntity) -> Unit,
    onEditTask: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit
) {
    val userName = uiState.user?.fullName ?: "Student"
    val todayDateString = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())

    // Periodic time ticker for live countdowns & auto-expiring cards
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(10000L) // Refresh every 10 seconds
            nowMillis = System.currentTimeMillis()
        }
    }

    // Dynamic Top Priority Item Selection
    val priorityItems = remember(uiState.tasks, uiState.assignments, uiState.exams, uiState.timetable, nowMillis) {
        val list = mutableListOf<TopPriorityItem>()

        uiState.tasks.filter { !it.isCompleted && it.priority == "High" && getTaskDeadlineMillis(it) > nowMillis }.forEach { task ->
            list.add(TopPriorityItem.HighTask(task, nowMillis))
        }

        uiState.assignments.filter { it.status != "Completed" && it.status != "Submitted" && it.priority == "High" }.forEach { assignment ->
            val dl = getAssignmentDeadlineMillis(assignment.dueDateMillis, assignment.dueTimeString)
            if (dl > nowMillis) {
                list.add(TopPriorityItem.HighAssignment(assignment, dl, nowMillis))
            }
        }

        uiState.exams.forEach { exam ->
            val start = getExamStartMillis(exam.examDateMillis, exam.timeString)
            if (start > nowMillis) {
                list.add(TopPriorityItem.UpcomingExam(exam, start, nowMillis))
            }
        }

        uiState.timetable.forEach { tt ->
            val nextStart = getNextClassOccurrenceMillis(tt.dayOfWeek, tt.startTime, nowMillis)
            if (nextStart > nowMillis) {
                list.add(TopPriorityItem.NextClass(tt, nextStart, nowMillis))
            }
        }

        list.sortBy { it.targetMillis }
        list
    }

    val topPriority = priorityItems.firstOrNull()

    // Dynamic Active Upcoming Assignments (filtered & sorted by deadline)
    val activeUpcomingAssignments = remember(uiState.assignments, nowMillis) {
        uiState.assignments
            .filter { assignment ->
                val dl = getAssignmentDeadlineMillis(assignment.dueDateMillis, assignment.dueTimeString)
                assignment.status != "Completed" && assignment.status != "Submitted" && dl > nowMillis
            }
            .sortedBy { getAssignmentDeadlineMillis(it.dueDateMillis, it.dueTimeString) }
    }

    // Dynamic Next Exam
    val nextExam = remember(uiState.exams, nowMillis) {
        uiState.exams
            .filter { getExamStartMillis(it.examDateMillis, it.timeString) > nowMillis }
            .minByOrNull { getExamStartMillis(it.examDateMillis, it.timeString) }
    }

    // Saved Smart Study Plan Tasks
    val savedSmartPlanTasks = remember(uiState.tasks, nowMillis) {
        uiState.tasks.filter { !it.isCompleted && getTaskDeadlineMillis(it) > nowMillis }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Greeting & Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.good_morning, userName),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = todayDateString,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // LIVE RUNNING STUDY TIMER CARD (IF RUNNING)
        if (timerState.isRunning) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenStudyTimer() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Surface(
                                color = MaterialTheme.colorScheme.tertiary,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "ACTIVE STUDY TIMER ⏱️",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${timerState.courseName} • ${timerState.topic}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }

                        val mins = timerState.remainingSeconds / 60
                        val secs = timerState.remainingSeconds % 60
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = String.format(Locale.getDefault(), "%02d:%02d", mins, secs),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // LIVE TOP PRIORITY CARD (REPLACES HARDCODED NEXT CLASS)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "TOP PRIORITY ⚡",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (topPriority != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.tertiary,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = topPriority.badgeLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (topPriority != null) {
                        Text(
                            text = topPriority.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = topPriority.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                    } else {
                        Text(
                            text = "No upcoming priority items.",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "You're all caught up! Add a task or class schedule to get started.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // QUICK ACTIONS
        item {
            Column {
                Text(
                    text = stringResource(R.string.quick_actions),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        QuickActionChip(
                            title = "+ Task",
                            icon = Icons.Default.AddTask,
                            onClick = onOpenAddTask
                        )
                    }
                    item {
                        QuickActionChip(
                            title = "+ Assignment",
                            icon = Icons.Default.Assignment,
                            onClick = onOpenAddAssignment
                        )
                    }
                    item {
                        QuickActionChip(
                            title = "Study Timer",
                            icon = Icons.Default.Timer,
                            onClick = onOpenStudyTimer
                        )
                    }
                    item {
                        QuickActionChip(
                            title = "+ Note",
                            icon = Icons.Default.EditNote,
                            onClick = onOpenAddNote
                        )
                    }
                    item {
                        QuickActionChip(
                            title = "+ Exam",
                            icon = Icons.Default.Event,
                            onClick = onOpenAddExam
                        )
                    }
                }
            }
        }

        // SAVED SMART STUDY PLAN (REAL DATA SOURCE OF TRUTH)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.todays_smart_plan),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        TextButton(onClick = onOpenSmartPlanSuggestions) {
                            Text("Suggest Plan 🤖")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (savedSmartPlanTasks.isEmpty()) {
                        Text(
                            text = "No study tasks scheduled. Tap 'Suggest Plan 🤖' or '+ Task' above to build your study schedule!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            savedSmartPlanTasks.forEach { task ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        IconButton(onClick = { onToggleTask(task) }) {
                                            Icon(
                                                imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                text = "${task.timeString} • ${task.title}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = "${task.subject} • ${task.description}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Row {
                                        IconButton(onClick = { onEditTask(task) }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit Task", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        }
                                        IconButton(onClick = { onDeleteTask(task) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete Task", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // UPCOMING DEADLINES & ASSIGNMENTS (DYNAMIC)
        item {
            Column {
                Text(
                    text = stringResource(R.string.upcoming_deadlines),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (activeUpcomingAssignments.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            text = "No active upcoming assignments.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        activeUpcomingAssignments.forEach { assignment ->
                            val dl = getAssignmentDeadlineMillis(assignment.dueDateMillis, assignment.dueTimeString)
                            val isDueSoon = (dl - nowMillis) <= 24 * 3600 * 1000L
                            val remainingText = formatRemainingTimeLabel(dl, nowMillis)

                            AssignmentCard(
                                assignment = assignment,
                                customBadge = if (isDueSoon) "Due Soon · $remainingText remaining" else "Due in $remainingText",
                                onToggleCompleted = { onToggleAssignment(assignment) },
                                onEdit = { onEditAssignment(assignment) },
                                onDelete = { onDeleteAssignment(assignment) }
                            )
                        }
                    }
                }
            }
        }

        // DYNAMIC EXAM COUNTDOWN
        if (nextExam != null) {
            val examStart = getExamStartMillis(nextExam.examDateMillis, nextExam.timeString)
            val examRemainingText = formatRemainingTimeLabel(examStart, nowMillis)

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = MaterialTheme.colorScheme.tertiary,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.next_exam),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = nextExam.courseName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Venue: ${nextExam.venue} • ${nextExam.timeString}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = examRemainingText,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    text = "Left",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionChip(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AssignmentCard(
    assignment: AssignmentEntity,
    customBadge: String? = null,
    onToggleCompleted: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = if (assignment.priority == "High") MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = customBadge ?: "Due Soon • ${assignment.dueTimeString}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (assignment.priority == "High") MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = assignment.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = assignment.courseName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onEdit != null) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Assignment",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (onDelete != null) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Assignment",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Surface(
                    color = if (assignment.status == "Completed") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape,
                    modifier = Modifier.clickable { onToggleCompleted?.invoke() }
                ) {
                    Icon(
                        imageVector = if (assignment.status == "Completed") Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                    )
                }
            }
        }
    }
}
