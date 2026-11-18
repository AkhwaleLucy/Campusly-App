package com.example.gossipapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gossipapp.data.local.entity.AssignmentEntity
import com.example.gossipapp.data.local.entity.CourseEntity

@Composable
fun AddAssignmentDialog(
    courses: List<CourseEntity>,
    onDismiss: () -> Unit,
    onAddAssignment: (AssignmentEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCourse by remember { mutableStateOf(courses.firstOrNull()?.name ?: "General") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    var dueDays by remember { mutableStateOf("2") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Assignment") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Assignment Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = selectedCourse,
                    onValueChange = { selectedCourse = it },
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
                        label = { Text("High Priority") }
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

                OutlinedTextField(
                    value = dueDays,
                    onValueChange = { dueDays = it },
                    label = { Text("Due in (Days)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val days = dueDays.toLongOrNull() ?: 2L
                        val dueMs = System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000L)
                        onAddAssignment(
                            AssignmentEntity(
                                courseName = selectedCourse,
                                title = title,
                                description = description,
                                dueDateMillis = dueMs,
                                priority = priority,
                                status = "Not started"
                            )
                        )
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Add Assignment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
