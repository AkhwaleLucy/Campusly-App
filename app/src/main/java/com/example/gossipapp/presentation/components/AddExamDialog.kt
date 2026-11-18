package com.example.gossipapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gossipapp.data.local.entity.CourseEntity
import com.example.gossipapp.data.local.entity.ExamEntity

@Composable
fun AddExamDialog(
    courses: List<CourseEntity>,
    onDismiss: () -> Unit,
    onAddExam: (ExamEntity) -> Unit
) {
    var courseName by remember { mutableStateOf(courses.firstOrNull()?.name ?: "Data Structures & Algorithms") }
    var timeString by remember { mutableStateOf("09:00 AM") }
    var venue by remember { mutableStateOf("Main Examination Hall") }
    var topics by remember { mutableStateOf("Binary Trees, Graphs & Sorting") }
    var daysAhead by remember { mutableStateOf("14") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Exam Schedule") },
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
                    label = { Text("Time (e.g. 09:00 AM)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = { Text("Venue / Room") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = topics,
                    onValueChange = { topics = it },
                    label = { Text("Topics / Exam Syllabus") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = daysAhead,
                    onValueChange = { daysAhead = it },
                    label = { Text("Exam Date in (Days from today)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (courseName.isNotBlank()) {
                        val days = daysAhead.toLongOrNull() ?: 14L
                        val dateMs = System.currentTimeMillis() + (days * 86400000L)
                        onAddExam(
                            ExamEntity(
                                courseName = courseName.trim(),
                                examDateMillis = dateMs,
                                timeString = timeString.trim(),
                                venue = venue.trim(),
                                topics = topics.trim()
                            )
                        )
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save Exam")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
