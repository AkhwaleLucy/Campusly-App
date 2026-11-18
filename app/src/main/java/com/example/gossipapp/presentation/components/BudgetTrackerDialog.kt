package com.example.gossipapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BudgetTrackerDialog(
    onDismiss: () -> Unit,
    onAddBudget: (category: String, targetAmount: Double, spentAmount: Double) -> Unit,
    onAddSavingsGoal: (title: String, targetKes: Double, savedKes: Double) -> Unit
) {
    var isSavingsGoal by remember { mutableStateOf(false) }
    var titleCategory by remember { mutableStateOf("") }
    var targetInput by remember { mutableStateOf("10000") }
    var currentInput by remember { mutableStateOf("4500") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isSavingsGoal) "New Savings Goal" else "New Budget Category") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !isSavingsGoal,
                        onClick = { isSavingsGoal = false },
                        label = { Text("Expense Category") }
                    )
                    FilterChip(
                        selected = isSavingsGoal,
                        onClick = { isSavingsGoal = true },
                        label = { Text("Savings Goal") }
                    )
                }

                OutlinedTextField(
                    value = titleCategory,
                    onValueChange = { titleCategory = it },
                    label = { Text(if (isSavingsGoal) "Goal Title (e.g. Laptop)" else "Category (e.g. Food)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = targetInput,
                    onValueChange = { targetInput = it },
                    label = { Text("Target Amount (KSh)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = currentInput,
                    onValueChange = { currentInput = it },
                    label = { Text(if (isSavingsGoal) "Currently Saved (KSh)" else "Currently Spent (KSh)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titleCategory.isNotBlank()) {
                        val target = targetInput.toDoubleOrNull() ?: 10000.0
                        val current = currentInput.toDoubleOrNull() ?: 0.0
                        if (isSavingsGoal) {
                            onAddSavingsGoal(titleCategory, target, current)
                        } else {
                            onAddBudget(titleCategory, target, current)
                        }
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
