package com.example.gossipapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SubmitEventDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, organizer: String, dateString: String, location: String, description: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var organizer by remember { mutableStateOf("") }
    var dateString by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Workshop") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Submit Campus Event")
                Text("Submissions are reviewed by campus admins before publishing", style = MaterialTheme.typography.bodySmall)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Event Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = organizer, onValueChange = { organizer = it }, label = { Text("Organizer Club/Dept") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = dateString, onValueChange = { dateString = it }, label = { Text("Date & Time (e.g. Nov 12, 3 PM)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Venue / Location") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (Career, Hackathon, etc.)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSubmit(title.trim(), organizer.trim(), dateString.trim(), location.trim(), description.trim(), category.trim())
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Submit for Review")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun SubmitOpportunityDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, organization: String, deadlineString: String, description: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var organization by remember { mutableStateOf("") }
    var deadlineString by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Internship") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Submit Opportunity / Internship")
                Text("Submissions are reviewed by campus admins before publishing", style = MaterialTheme.typography.bodySmall)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Opportunity Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = organization, onValueChange = { organization = it }, label = { Text("Company / Organization") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = deadlineString, onValueChange = { deadlineString = it }, label = { Text("Application Deadline") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (Internship, Fellowship, etc.)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description & Requirements") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSubmit(title.trim(), organization.trim(), deadlineString.trim(), description.trim(), category.trim())
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Submit for Review")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun SubmitStudyGroupDialog(
    onDismiss: () -> Unit,
    onSubmit: (name: String, courseName: String, topicSummary: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var courseName by remember { mutableStateOf("") }
    var topicSummary by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Submit Study Group Listing")
                Text("Submissions are reviewed by campus admins before publishing", style = MaterialTheme.typography.bodySmall)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Study Group Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = courseName, onValueChange = { courseName = it }, label = { Text("Course / Subject Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = topicSummary, onValueChange = { topicSummary = it }, label = { Text("Topic Summary / Goals") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSubmit(name.trim(), courseName.trim(), topicSummary.trim())
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Submit for Review")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
