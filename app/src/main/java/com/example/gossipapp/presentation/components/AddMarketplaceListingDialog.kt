package com.example.gossipapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddMarketplaceListingDialog(
    currentUserName: String = "Student",
    onDismiss: () -> Unit,
    onAddListing: (title: String, description: String, priceKes: Double, category: String, sellerName: String, location: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceInput by remember { mutableStateOf("2500") }
    var category by remember { mutableStateOf("Textbooks") }
    var location by remember { mutableStateOf("Campus Library") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("List Item on Campus Marketplace") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Item Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Item Description & Condition") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = priceInput,
                    onValueChange = { priceInput = it },
                    label = { Text("Price (KSh)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (Textbooks, Electronics, etc.)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Campus Meetup Location") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val price = priceInput.toDoubleOrNull() ?: 2500.0
                        onAddListing(title, description, price, category, currentUserName, location)
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Post Listing")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
