package com.example.gossipapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gossipapp.data.local.entity.CourseEntity
import com.example.gossipapp.data.local.entity.NoteEntity

@Composable
fun AddNoteDialog(
    courses: List<CourseEntity>,
    onDismiss: () -> Unit,
    onAddNote: (NoteEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCourse by remember { mutableStateOf(courses.firstOrNull()?.name ?: "General") }
    var content by remember { mutableStateOf("") }
    var folder by remember { mutableStateOf("General") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Study Note") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Note Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = selectedCourse,
                    onValueChange = { selectedCourse = it },
                    label = { Text("Course Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = folder,
                    onValueChange = { folder = it },
                    label = { Text("Folder / Category") },
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
                        onAddNote(
                            NoteEntity(
                                courseName = selectedCourse,
                                title = title,
                                content = content,
                                folder = folder
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
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
