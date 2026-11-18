package com.example.gossipapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gossipapp.R
import com.example.gossipapp.data.local.entity.NoteEntity

data class AiChatMessage(
    val sender: String, // "User" or "AI"
    val text: String
)

@Composable
fun AiAssistantDialog(
    notes: List<NoteEntity> = emptyList(),
    onDismiss: () -> Unit,
    onOpenFlashcardGenerator: (initialTopic: String, initialNote: NoteEntity?, count: Int) -> Unit,
    onGenerateAiResponse: (prompt: String, includeNoteContent: String?) -> String
) {
    var prompt by remember { mutableStateOf("") }
    var useWebSearch by remember { mutableStateOf(true) }
    var includeSelectedNote by remember { mutableStateOf(false) }
    var selectedNoteToInclude by remember { mutableStateOf<NoteEntity?>(notes.firstOrNull()) }

    val messages = remember {
        mutableStateListOf(
            AiChatMessage(
                sender = "AI",
                text = "Hello! I'm Campusly AI. Ask me academic questions, request web-backed explanations, or tap 'Generate Flashcards 🎴' to create revision decks!"
            )
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.ai_assistant), fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Controls Row: Web Search & Note Inclusion
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Web Search", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = useWebSearch,
                            onCheckedChange = { useWebSearch = it }
                        )
                    }

                    if (notes.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Include Note 📝", style = MaterialTheme.typography.labelSmall)
                            Checkbox(
                                checked = includeSelectedNote,
                                onCheckedChange = { includeSelectedNote = it }
                            )
                        }
                    }
                }

                // Quick Suggestion Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = false,
                            onClick = {
                                onOpenFlashcardGenerator("Database Normalization", if (includeSelectedNote) selectedNoteToInclude else null, 7)
                            },
                            label = { Text("Generate Flashcards 🎴") },
                            leadingIcon = { Icon(Icons.Default.Style, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                    item {
                        FilterChip(
                            selected = false,
                            onClick = {
                                val userMsg = "Search web for latest Jetpack Compose guidelines"
                                messages.add(AiChatMessage("User", userMsg))
                                val noteText = if (includeSelectedNote) selectedNoteToInclude?.content else null
                                val aiMsg = onGenerateAiResponse(userMsg, noteText)
                                messages.add(AiChatMessage("AI", aiMsg))
                            },
                            label = { Text("Web Search Compose 🌐") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = false,
                            onClick = {
                                val userMsg = "Explain Subnetting like I'm a beginner"
                                messages.add(AiChatMessage("User", userMsg))
                                val noteText = if (includeSelectedNote) selectedNoteToInclude?.content else null
                                val aiMsg = onGenerateAiResponse(userMsg, noteText)
                                messages.add(AiChatMessage("AI", aiMsg))
                            },
                            label = { Text("Subnetting Web Info") }
                        )
                    }
                }

                // Chat Messages Feed
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { msg ->
                        Surface(
                            color = if (msg.sender == "AI") MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (msg.sender == "AI") "Campusly AI 🤖" else "You 👤",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    placeholder = { Text("Ask AI or say 'Make me 5 flashcards about AES'...") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = {
                            if (prompt.isNotBlank()) {
                                val userMsg = prompt.trim()
                                messages.add(AiChatMessage("User", userMsg))

                                val lower = userMsg.lowercase()
                                if (lower.contains("flashcard") || lower.contains("flash cards")) {
                                    val countMatch = Regex("\\b(\\d+)\\b").find(userMsg)
                                    val count = countMatch?.groupValues?.get(1)?.toIntOrNull() ?: 5
                                    val topic = userMsg
                                        .replace(Regex("(?i)make me \\d+ flashcards about"), "")
                                        .replace(Regex("(?i)make me flashcards about"), "")
                                        .replace(Regex("(?i)generate \\d+ flashcards about"), "")
                                        .replace(Regex("(?i)generate flashcards about"), "")
                                        .replace(Regex("(?i)make me \\d+ flashcards on"), "")
                                        .replace(Regex("(?i)make me flashcards on"), "")
                                        .replace(Regex("(?i)flashcards"), "")
                                        .replace(Regex("(?i)flash cards"), "")
                                        .trim()
                                    val finalTopic = if (topic.isNotBlank()) topic.replaceFirstChar { it.uppercase() } else "Database Normalization"
                                    messages.add(AiChatMessage("AI", "Opening AI Flashcard Generator for topic '$finalTopic' ($count cards)..."))
                                    onOpenFlashcardGenerator(finalTopic, if (includeSelectedNote) selectedNoteToInclude else null, count)
                                } else {
                                    val noteText = if (includeSelectedNote) selectedNoteToInclude?.content else null
                                    val response = onGenerateAiResponse(userMsg, noteText)
                                    messages.add(AiChatMessage("AI", response))
                                }
                                prompt = ""
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Prompt")
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
