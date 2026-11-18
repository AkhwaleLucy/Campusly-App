package com.example.gossipapp.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gossipapp.data.local.entity.FlashcardEntity

@Composable
fun FlashcardDeckViewerDialog(
    deckName: String,
    cards: List<FlashcardEntity>,
    onDismiss: () -> Unit,
    onDeleteCard: (FlashcardEntity) -> Unit,
    onSaveEditedCard: (FlashcardEntity) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var editingCard by remember { mutableStateOf<FlashcardEntity?>(null) }

    if (cards.isEmpty()) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(deckName) },
            text = { Text("No flashcards remaining in this deck.") },
            confirmButton = { Button(onClick = onDismiss) { Text("Close") } }
        )
        return
    }

    val currentCard = cards.getOrNull(currentIndex) ?: cards.first()
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 350),
        label = "cardFlip"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(deckName, fontWeight = FontWeight.Bold)
                    Text("Card ${currentIndex + 1} of ${cards.size}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }

                Row {
                    IconButton(onClick = { editingCard = currentCard }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Card", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = {
                        onDeleteCard(currentCard)
                        if (currentIndex >= cards.size - 1) {
                            currentIndex = maxOf(0, cards.size - 2)
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Card", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Animated Card Flip Box
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .graphicsLayer {
                            rotationY = rotation
                            cameraDistance = 12 * density
                        }
                        .clickable { isFlipped = !isFlipped },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isFlipped) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                            .graphicsLayer {
                                if (rotation > 90f) {
                                    rotationY = 180f
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isFlipped) "ANSWER" else "QUESTION",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isFlipped) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isFlipped) currentCard.back else currentCard.front,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Tap to flip 🔄",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Next / Prev Navigation Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            isFlipped = false
                            currentIndex = (currentIndex - 1 + cards.size) % cards.size
                        },
                        enabled = cards.size > 1
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = null)
                        Text("Previous")
                    }

                    Text("${currentIndex + 1} / ${cards.size}", style = MaterialTheme.typography.labelLarge)

                    Button(
                        onClick = {
                            isFlipped = false
                            currentIndex = (currentIndex + 1) % cards.size
                        },
                        enabled = cards.size > 1
                    ) {
                        Text("Next")
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("Done Studying")
            }
        }
    )

    // Edit Single Card Dialog
    editingCard?.let { card ->
        var editFront by remember { mutableStateOf(card.front) }
        var editBack by remember { mutableStateOf(card.back) }

        AlertDialog(
            onDismissRequest = { editingCard = null },
            title = { Text("Edit Flashcard") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editFront,
                        onValueChange = { editFront = it },
                        label = { Text("Front (Question)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editBack,
                        onValueChange = { editBack = it },
                        label = { Text("Back (Answer)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveEditedCard(card.copy(front = editFront.trim(), back = editBack.trim()))
                        editingCard = null
                    }
                ) {
                    Text("Save Card")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingCard = null }) { Text("Cancel") }
            }
        )
    }
}
