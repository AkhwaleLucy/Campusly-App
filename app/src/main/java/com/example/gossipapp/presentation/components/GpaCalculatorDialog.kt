package com.example.gossipapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun GpaCalculatorDialog(
    onDismiss: () -> Unit,
    onAddGpaRecord: (semesterName: String, gpaScore: Double, creditUnits: Int) -> Unit
) {
    var semesterName by remember { mutableStateOf("Semester 1") }
    var courseCount by remember { mutableIntStateOf(4) }
    var scoreInput by remember { mutableStateOf("3.52") }
    var creditsInput by remember { mutableStateOf("18") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("GPA Calculator & Semester Record", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = semesterName,
                    onValueChange = { semesterName = it },
                    label = { Text("Semester Name (e.g. Year 2 - Semester 2)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = scoreInput,
                    onValueChange = { scoreInput = it },
                    label = { Text("Calculated Semester GPA (0.00 - 4.00)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = creditsInput,
                    onValueChange = { creditsInput = it },
                    label = { Text("Total Credit Units") },
                    modifier = Modifier.fillMaxWidth()
                )

                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Grading Scale Reference:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "A = 4.0 | A- = 3.7 | B+ = 3.3 | B = 3.0 | C = 2.0 | D = 1.0",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val gpa = scoreInput.toDoubleOrNull() ?: 3.5
                    val credits = creditsInput.toIntOrNull() ?: 18
                    onAddGpaRecord(semesterName, gpa, credits)
                    onDismiss()
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save Semester Record")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
