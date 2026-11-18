package com.example.gossipapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gossipapp.data.local.entity.TaskEntity

@Composable
fun SmartPlanDialog(
    suggestions: List<TaskEntity>,
    onDismiss: () -> Unit,
    onSaveSelectedPlan: (List<TaskEntity>) -> Unit
) {
    val editedSuggestions = remember { mutableStateListOf<TaskEntity>().apply { addAll(suggestions) } }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Smart Study Plan Suggestions 🤖", fontWeight = FontWeight.Bold)
                Text("Edit any suggested study task below before saving to your plan", style = MaterialTheme.typography.bodySmall)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(editedSuggestions.indices.toList()) { index ->
                    val task = editedSuggestions[index]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = task.title,
                                onValueChange = { newTitle ->
                                    editedSuggestions[index] = task.copy(title = newTitle)
                                },
                                label = { Text("Task Title") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = task.subject,
                                    onValueChange = { newSubject ->
                                        editedSuggestions[index] = task.copy(subject = newSubject)
                                    },
                                    label = { Text("Subject") },
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = task.timeString,
                                    onValueChange = { newTime ->
                                        editedSuggestions[index] = task.copy(timeString = newTime)
                                    },
                                    label = { Text("Start Time") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveSelectedPlan(editedSuggestions)
                    onDismiss()
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save Suggested Plan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
