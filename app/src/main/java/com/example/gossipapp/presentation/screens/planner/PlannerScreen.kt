package com.example.gossipapp.presentation.screens.planner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gossipapp.R
import com.example.gossipapp.data.local.entity.AssignmentEntity
import com.example.gossipapp.data.local.entity.ExamEntity
import com.example.gossipapp.data.local.entity.TaskEntity
import com.example.gossipapp.data.local.entity.TimetableEntity
import com.example.gossipapp.presentation.screens.home.AssignmentCard
import com.example.gossipapp.presentation.viewmodel.CampuslyUiState

@Composable
fun PlannerScreen(
    uiState: CampuslyUiState,
    onOpenAddTimetable: () -> Unit,
    onOpenAddAssignment: () -> Unit,
    onOpenAddExam: () -> Unit,
    onOpenAddTask: () -> Unit,
    onToggleAssignment: (AssignmentEntity) -> Unit,
    onEditAssignment: (AssignmentEntity) -> Unit,
    onDeleteAssignment: (AssignmentEntity) -> Unit,
    onEditTimetable: (TimetableEntity) -> Unit,
    onDeleteTimetable: (TimetableEntity) -> Unit,
    onEditExam: (ExamEntity) -> Unit,
    onDeleteExam: (ExamEntity) -> Unit,
    onToggleTask: (TaskEntity) -> Unit,
    onEditTask: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit
) {
    var selectedSection by remember { mutableIntStateOf(0) } // 0 = Timetable, 1 = Assignments, 2 = Exams, 3 = Tasks

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.academic_planner),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Section Selector Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedSection,
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.background,
                divider = {}
            ) {
                Tab(
                    selected = selectedSection == 0,
                    onClick = { selectedSection = 0 },
                    text = { Text(stringResource(R.string.tab_timetable)) }
                )
                Tab(
                    selected = selectedSection == 1,
                    onClick = { selectedSection = 1 },
                    text = { Text("${stringResource(R.string.tab_assignments)} (${uiState.assignments.size})") }
                )
                Tab(
                    selected = selectedSection == 2,
                    onClick = { selectedSection = 2 },
                    text = { Text("${stringResource(R.string.tab_exams)} (${uiState.exams.size})") }
                )
                Tab(
                    selected = selectedSection == 3,
                    onClick = { selectedSection = 3 },
                    text = { Text("${stringResource(R.string.tab_tasks)} (${uiState.tasks.size})") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Conflict Message Alert Banner
            if (uiState.conflictMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uiState.conflictMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            when (selectedSection) {
                0 -> TimetableSection(
                    timetable = uiState.timetable,
                    onOpenAddTimetable = onOpenAddTimetable,
                    onEditTimetable = onEditTimetable,
                    onDeleteTimetable = onDeleteTimetable
                )
                1 -> AssignmentSection(
                    assignments = uiState.assignments,
                    onOpenAddAssignment = onOpenAddAssignment,
                    onToggleAssignment = onToggleAssignment,
                    onEditAssignment = onEditAssignment,
                    onDeleteAssignment = onDeleteAssignment
                )
                2 -> ExamSection(
                    exams = uiState.exams,
                    onOpenAddExam = onOpenAddExam,
                    onEditExam = onEditExam,
                    onDeleteExam = onDeleteExam
                )
                3 -> TaskSection(
                    tasks = uiState.tasks,
                    onOpenAddTask = onOpenAddTask,
                    onToggleTask = onToggleTask,
                    onEditTask = onEditTask,
                    onDeleteTask = onDeleteTask
                )
            }
        }
    }
}

@Composable
fun TimetableSection(
    timetable: List<TimetableEntity>,
    onOpenAddTimetable: () -> Unit,
    onEditTimetable: (TimetableEntity) -> Unit,
    onDeleteTimetable: (TimetableEntity) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Weekly Schedule",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Button(onClick = onOpenAddTimetable, shape = RoundedCornerShape(8.dp)) {
                Text("+ Class Entry")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (timetable.isEmpty()) {
            Text(
                text = "No timetable entries added yet. Tap '+ Class Entry' to add your class schedule!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(timetable, key = { it.id }) { entry ->
                    TimetableCard(
                        entry = entry,
                        onEdit = { onEditTimetable(entry) },
                        onDelete = { onDeleteTimetable(entry) }
                    )
                }
            }
        }
    }
}

@Composable
fun TimetableCard(
    entry: TimetableEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val days = listOf("", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val dayName = days.getOrElse(entry.dayOfWeek) { "Weekday" }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "$dayName • ${entry.startTime} - ${entry.endTime}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Room: ${entry.room}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Class", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Class", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${entry.courseCode} - ${entry.courseName}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Lecturer: ${entry.instructor}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AssignmentSection(
    assignments: List<AssignmentEntity>,
    onOpenAddAssignment: () -> Unit,
    onToggleAssignment: (AssignmentEntity) -> Unit,
    onEditAssignment: (AssignmentEntity) -> Unit,
    onDeleteAssignment: (AssignmentEntity) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Assignments & Coursework",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Button(onClick = onOpenAddAssignment, shape = RoundedCornerShape(8.dp)) {
                Text(stringResource(R.string.add_assignment))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (assignments.isEmpty()) {
            Text(
                text = "No assignments added yet. Tap '+ Assignment' to create your first coursework deadline!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(assignments, key = { it.id }) { assignment ->
                    AssignmentCard(
                        assignment = assignment,
                        onToggleCompleted = { onToggleAssignment(assignment) },
                        onEdit = { onEditAssignment(assignment) },
                        onDelete = { onDeleteAssignment(assignment) }
                    )
                }
            }
        }
    }
}

@Composable
fun ExamSection(
    exams: List<ExamEntity>,
    onOpenAddExam: () -> Unit,
    onEditExam: (ExamEntity) -> Unit,
    onDeleteExam: (ExamEntity) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Upcoming Exams",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Button(onClick = onOpenAddExam, shape = RoundedCornerShape(8.dp)) {
                Text(stringResource(R.string.add_exam))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (exams.isEmpty()) {
            Text(
                text = "No exams registered. Tap '+ Exam' to add your examination schedule!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(exams, key = { it.id }) { exam ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = exam.courseName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Row {
                                    IconButton(onClick = { onEditExam(exam) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Exam", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { onDeleteExam(exam) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Exam", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Time: ${exam.timeString} | Venue: ${exam.venue}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Topics: ${exam.topics}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskSection(
    tasks: List<TaskEntity>,
    onOpenAddTask: () -> Unit,
    onToggleTask: (TaskEntity) -> Unit,
    onEditTask: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Personal Tasks & Smart Study Plans",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Button(onClick = onOpenAddTask, shape = RoundedCornerShape(8.dp)) {
                Text(stringResource(R.string.add_task))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (tasks.isEmpty()) {
            Text(
                text = "No study tasks created yet. Tap '+ Task' to create a study plan item!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
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
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${task.subject} • ${task.timeString} • ${task.description}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row {
                                IconButton(onClick = { onEditTask(task) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Task", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                }
                                IconButton(onClick = { onDeleteTask(task) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Task", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
