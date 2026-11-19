package com.example.gossipapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gossipapp.data.local.entity.*

@Composable
fun EditAssignmentDialog(
    assignment: AssignmentEntity,
    onDismiss: () -> Unit,
    onSaveAssignment: (AssignmentEntity) -> Unit,
    onDeleteAssignment: (AssignmentEntity) -> Unit
) {
    var title by remember { mutableStateOf(assignment.title) }
    var courseName by remember { mutableStateOf(assignment.courseName) }
    var description by remember { mutableStateOf(assignment.description) }
    var priority by remember { mutableStateOf(assignment.priority) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Assignment") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Assignment Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = courseName,
                    onValueChange = { courseName = it },
                    label = { Text("Course Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = priority == "High",
                        onClick = { priority = "High" },
                        label = { Text("High") }
                    )
                    FilterChip(
                        selected = priority == "Medium",
                        onClick = { priority = "Medium" },
                        label = { Text("Medium") }
                    )
                    FilterChip(
                        selected = priority == "Low",
                        onClick = { priority = "Low" },
                        label = { Text("Low") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSaveAssignment(
                            assignment.copy(
                                title = title,
                                courseName = courseName,
                                description = description,
                                priority = priority
                            )
                        )
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(
                    onClick = {
                        onDeleteAssignment(assignment)
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

@Composable
fun EditCourseDialog(
    course: CourseEntity,
    onDismiss: () -> Unit,
    onSaveCourse: (CourseEntity) -> Unit,
    onDeleteCourse: (CourseEntity) -> Unit
) {
    var code by remember { mutableStateOf(course.code) }
    var name by remember { mutableStateOf(course.name) }
    var lecturer by remember { mutableStateOf(course.lecturer) }
    var creditUnits by remember { mutableStateOf(course.creditUnits.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Course") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Course Code") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Course Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = lecturer,
                    onValueChange = { lecturer = it },
                    label = { Text("Lecturer") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = creditUnits,
                    onValueChange = { creditUnits = it },
                    label = { Text("Credit Units") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (code.isNotBlank() && name.isNotBlank()) {
                        onSaveCourse(
                            course.copy(
                                code = code,
                                name = name,
                                lecturer = lecturer,
                                creditUnits = creditUnits.toIntOrNull() ?: course.creditUnits
                            )
                        )
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(
                    onClick = {
                        onDeleteCourse(course)
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

@Composable
fun EditTimetableDialog(
    entry: TimetableEntity,
    onDismiss: () -> Unit,
    onSaveTimetable: (TimetableEntity) -> Boolean,
    onDeleteTimetable: (TimetableEntity) -> Unit
) {
    var courseCode by remember { mutableStateOf(entry.courseCode) }
    var courseName by remember { mutableStateOf(entry.courseName) }
    var instructor by remember { mutableStateOf(entry.instructor) }
    var room by remember { mutableStateOf(entry.room) }
    var startTime by remember { mutableStateOf(entry.startTime) }
    var endTime by remember { mutableStateOf(entry.endTime) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Class Schedule") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = courseCode,
                    onValueChange = { courseCode = it },
                    label = { Text("Course Code") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = courseName,
                    onValueChange = { courseName = it },
                    label = { Text("Course Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = instructor,
                    onValueChange = { instructor = it },
                    label = { Text("Instructor") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Room / Lab") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End Time") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val success = onSaveTimetable(
                        entry.copy(
                            courseCode = courseCode,
                            courseName = courseName,
                            instructor = instructor,
                            room = room,
                            startTime = startTime,
                            endTime = endTime
                        )
                    )
                    if (success) {
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(
                    onClick = {
                        onDeleteTimetable(entry)
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

@Composable
fun EditExamDialog(
    exam: ExamEntity,
    onDismiss: () -> Unit,
    onSaveExam: (ExamEntity) -> Unit,
    onDeleteExam: (ExamEntity) -> Unit
) {
    var courseName by remember { mutableStateOf(exam.courseName) }
    var timeString by remember { mutableStateOf(exam.timeString) }
    var venue by remember { mutableStateOf(exam.venue) }
    var topics by remember { mutableStateOf(exam.topics) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Exam") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = courseName,
                    onValueChange = { courseName = it },
                    label = { Text("Course Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = timeString,
                    onValueChange = { timeString = it },
                    label = { Text("Time String") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = { Text("Venue") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = topics,
                    onValueChange = { topics = it },
                    label = { Text("Topics Covered") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveExam(
                        exam.copy(
                            courseName = courseName,
                            timeString = timeString,
                            venue = venue,
                            topics = topics
                        )
                    )
                    onDismiss()
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(
                    onClick = {
                        onDeleteExam(exam)
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAddTask: (task: TaskEntity, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("General") }
    var priority by remember { mutableStateOf("Medium") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Personal Study Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; errorMessage = null },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it; errorMessage = null },
                    label = { Text("Subject / Course") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it; errorMessage = null },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = priority == "High",
                        onClick = { priority = "High" },
                        label = { Text("High") }
                    )
                    FilterChip(
                        selected = priority == "Medium",
                        onClick = { priority = "Medium" },
                        label = { Text("Medium") }
                    )
                    FilterChip(
                        selected = priority == "Low",
                        onClick = { priority = "Low" },
                        label = { Text("Low") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAddTask(
                            TaskEntity(
                                title = title.trim(),
                                subject = subject.trim(),
                                description = description.trim(),
                                deadlineMillis = System.currentTimeMillis() + 86400000L,
                                priority = priority
                            ),
                            { onDismiss() },
                            { err -> errorMessage = err }
                        )
                    } else {
                        errorMessage = "Please enter a task title."
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Add Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditTaskDialog(
    task: TaskEntity,
    onDismiss: () -> Unit,
    onSaveTask: (task: TaskEntity, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var subject by remember { mutableStateOf(task.subject) }
    var priority by remember { mutableStateOf(task.priority) }
    var isCompleted by remember { mutableStateOf(task.isCompleted) }
    var showConfirmDelete by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (showConfirmDelete) {
        AlertDialog(
            onDismissRequest = { showConfirmDelete = false },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete task '${task.title}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteTask(task)
                        showConfirmDelete = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDelete = false }) { Text("Cancel") }
            }
        )
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Study Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; errorMessage = null },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it; errorMessage = null },
                    label = { Text("Subject") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it; errorMessage = null },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text("Status:")
                    FilterChip(
                        selected = isCompleted,
                        onClick = { isCompleted = !isCompleted },
                        label = { Text(if (isCompleted) "Completed ✓" else "Open") }
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = priority == "High",
                        onClick = { priority = "High" },
                        label = { Text("High") }
                    )
                    FilterChip(
                        selected = priority == "Medium",
                        onClick = { priority = "Medium" },
                        label = { Text("Medium") }
                    )
                    FilterChip(
                        selected = priority == "Low",
                        onClick = { priority = "Low" },
                        label = { Text("Low") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSaveTask(
                            task.copy(
                                title = title.trim(),
                                subject = subject.trim(),
                                description = description.trim(),
                                priority = priority,
                                isCompleted = isCompleted
                            ),
                            { onDismiss() },
                            { err -> errorMessage = err }
                        )
                    } else {
                        errorMessage = "Task title cannot be blank."
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(
                    onClick = { showConfirmDelete = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        }
    )
}

@Composable
fun EditNoteDialog(
    note: NoteEntity,
    onDismiss: () -> Unit,
    onSaveNote: (NoteEntity) -> Unit,
    onDeleteNote: (NoteEntity) -> Unit,
    onGenerateFlashcardsFromNote: (NoteEntity) -> Unit
) {
    var title by remember { mutableStateOf(note.title) }
    var courseName by remember { mutableStateOf(note.courseName) }
    var folder by remember { mutableStateOf(note.folder) }
    var content by remember { mutableStateOf(note.content) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Open & Edit Note") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        onDismiss()
                        onGenerateFlashcardsFromNote(note)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Generate Flashcards from Note 🎴")
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Note Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = courseName,
                    onValueChange = { courseName = it },
                    label = { Text("Course Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = folder,
                    onValueChange = { folder = it },
                    label = { Text("Folder / Tag") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Note Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    maxLines = 6
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSaveNote(
                            note.copy(
                                title = title,
                                courseName = courseName,
                                folder = folder,
                                content = content
                            )
                        )
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save Note")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(
                    onClick = {
                        onDeleteNote(note)
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        }
    )
}
