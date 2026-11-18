package com.example.gossipapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gossipapp.data.local.entity.NoteEntity
import kotlinx.coroutines.launch

data class FlashcardItemData(
    var front: String,
    var back: String
)

@Composable
fun AiFlashcardGeneratorDialog(
    initialTopic: String = "Database Normalization",
    initialNote: NoteEntity? = null,
    notes: List<NoteEntity> = emptyList(),
    onDismiss: () -> Unit,
    onGenerateCards: suspend (topic: String, selectedNote: NoteEntity?, count: Int) -> Pair<List<FlashcardItemData>, String?>,
    onSaveDeck: (deckName: String, cards: List<FlashcardItemData>) -> Unit
) {
    var topic by remember { mutableStateOf(initialTopic) }
    var selectedNote by remember { mutableStateOf<NoteEntity?>(initialNote) }
    var useNote by remember { mutableStateOf(initialNote != null) }
    var cardCount by remember { mutableIntStateOf(5) }
    var deckName by remember { mutableStateOf(if (useNote && selectedNote != null) "${selectedNote?.title} Deck" else "$topic Deck") }

    var generatedCards = remember { mutableStateListOf<FlashcardItemData>() }
    var explanationMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    // Auto-generate on open
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val result = onGenerateCards(topic, if (useNote) selectedNote else null, cardCount)
            generatedCards.clear()
            generatedCards.addAll(result.first)
            explanationMessage = result.second
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Flashcard generation failed: ${e.localizedMessage ?: "Unknown error"}. Please check your connection and retry."
        } finally {
            isLoading = false
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
                .imePadding(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Flashcard Generator 🎴", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    IconButton(onClick = onDismiss) {
                        Text("✕", style = MaterialTheme.typography.titleMedium)
                    }
                }

                // Configuration Inputs
                OutlinedTextField(
                    value = deckName,
                    onValueChange = { deckName = it },
                    label = { Text("Flashcard Deck Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        label = { Text("Topic") },
                        modifier = Modifier.weight(1f),
                        enabled = !useNote,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Cards", style = MaterialTheme.typography.labelSmall)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilterChip(
                                selected = cardCount == 3,
                                onClick = { cardCount = 3 },
                                label = { Text("3") }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            FilterChip(
                                selected = cardCount == 5,
                                onClick = { cardCount = 5 },
                                label = { Text("5") }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            FilterChip(
                                selected = cardCount == 7,
                                onClick = { cardCount = 7 },
                                label = { Text("7") }
                            )
                        }
                    }
                }

                // Note Selection Switch
                if (notes.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = useNote,
                                onCheckedChange = { useNote = it }
                            )
                            Text("Generate from saved note 📝", style = MaterialTheme.typography.labelMedium)
                        }

                        if (useNote) {
                            var dropdownExpanded by remember { mutableStateOf(false) }
                            Box {
                                OutlinedButton(onClick = { dropdownExpanded = true }, shape = RoundedCornerShape(8.dp)) {
                                    Text(selectedNote?.title ?: "Select Note", maxLines = 1)
                                }
                                DropdownMenu(expanded = dropdownExpanded, onDismissRequest = { dropdownExpanded = false }) {
                                    notes.forEach { note ->
                                        DropdownMenuItem(
                                            text = { Text(note.title) },
                                            onClick = {
                                                selectedNote = note
                                                deckName = "${note.title} Deck"
                                                dropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            try {
                                val result = onGenerateCards(topic, if (useNote) selectedNote else null, cardCount)
                                generatedCards.clear()
                                generatedCards.addAll(result.first)
                                explanationMessage = result.second
                                errorMessage = null
                            } catch (e: Exception) {
                                errorMessage = "Flashcard generation failed: ${e.localizedMessage ?: "Unknown error"}. Please retry."
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isLoading) "Generating Flashcards..." else "Re-Generate Cards 🤖")
                }

                if (explanationMessage != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "ℹ️ $explanationMessage",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                if (errorMessage != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = errorMessage!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.weight(1f))
                            TextButton(onClick = {
                                coroutineScope.launch {
                                    isLoading = true
                                    try {
                                        val result = onGenerateCards(topic, if (useNote) selectedNote else null, cardCount)
                                        generatedCards.clear()
                                        generatedCards.addAll(result.first)
                                        explanationMessage = result.second
                                        errorMessage = null
                                    } catch (e: Exception) {
                                        errorMessage = "Flashcard generation failed: ${e.localizedMessage ?: "Unknown error"}. Please retry."
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }) {
                                Text("Retry")
                            }
                        }
                    }
                }

                Text(
                    text = "Generated Flashcard Preview (${generatedCards.size} cards):",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Scrollable Cards Preview & Editor List
                if (generatedCards.isEmpty() && !isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Text("No flashcards generated yet. Tap 'Re-Generate Cards' above.", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        itemsIndexed(generatedCards) { index, card ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text("Card #${index + 1}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        IconButton(onClick = { generatedCards.removeAt(index) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Remove Card", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                                        }
                                    }

                                    OutlinedTextField(
                                        value = card.front,
                                        onValueChange = { newFront ->
                                            generatedCards[index] = card.copy(front = newFront)
                                        },
                                        label = { Text("Front (Question)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    OutlinedTextField(
                                        value = card.back,
                                        onValueChange = { newBack ->
                                            generatedCards[index] = card.copy(back = newBack)
                                        },
                                        label = { Text("Back (Answer)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        if (deckName.isNotBlank() && generatedCards.isNotEmpty()) {
                            onSaveDeck(deckName.trim(), generatedCards.toList())
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    enabled = deckName.isNotBlank() && generatedCards.isNotEmpty()
                ) {
                    Text("Save Flashcard Deck (${generatedCards.size} Cards) 💾")
                }
            }
        }
    }
}
