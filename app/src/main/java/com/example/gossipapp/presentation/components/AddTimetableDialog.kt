package com.example.gossipapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gossipapp.data.local.entity.CourseEntity
import com.example.gossipapp.data.local.entity.TimetableEntity

@Composable
fun AddTimetableDialog(
    courses: List<CourseEntity>,
    conflictMessage: String?,
    onDismiss: () -> Unit,
    onAddTimetable: (TimetableEntity) -> Boolean,
    onClearConflict: () -> Unit
) {
    var selectedCourseCode by remember { mutableStateOf(courses.firstOrNull()?.code ?: "APT2060") }
    var selectedCourseName by remember { mutableStateOf(courses.firstOrNull()?.name ?: "Data Structures & Algorithms") }
    var instructor by remember { mutableStateOf(courses.firstOrNull()?.lecturer ?: "Dr. Lecturer") }
    var room by remember { mutableStateOf("Lab 1") }
    var dayOfWeek by remember { mutableIntStateOf(2) } // 1=Mon, 2=Tue, etc.
    var startTime by remember { mutableStateOf("09:00") }
    var endTime by remember { mutableStateOf("10:30") }

    val daysMap = listOf(
        Pair(1, "Mon"), Pair(2, "Tue"), Pair(3, "Wed"),
        Pair(4, "Thu"), Pair(5, "Fri"), Pair(6, "Sat"), Pair(7, "Sun")
    )

    AlertDialog(
        onDismissRequest = {
            onClearConflict()
            onDismiss()
        },
        title = { Text("Add Timetable Class Entry") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (conflictMessage != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = conflictMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = selectedCourseCode,
                    onValueChange = { selectedCourseCode = it },
                    label = { Text("Course Code (e.g. APT2060)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = selectedCourseName,
                    onValueChange = { selectedCourseName = it },
                    label = { Text("Course Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = instructor,
                    onValueChange = { instructor = it },
                    label = { Text("Instructor / Lecturer") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Room / Laboratory") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Day of Week:", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(daysMap) { day ->
                        FilterChip(
                            selected = dayOfWeek == day.first,
                            onClick = { dayOfWeek = day.first },
                            label = { Text(day.second) }
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start (e.g. 09:00)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End (e.g. 10:30)") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val entry = TimetableEntity(
                        courseCode = selectedCourseCode.trim(),
                        courseName = selectedCourseName.trim(),
                        instructor = instructor.trim(),
                        room = room.trim(),
                        dayOfWeek = dayOfWeek,
                        startTime = startTime.trim(),
                        endTime = endTime.trim()
                    )
                    val success = onAddTimetable(entry)
                    if (success) {
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save Class Schedule")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onClearConflict()
                onDismiss()
            }) { Text("Cancel") }
        }
    )
}
