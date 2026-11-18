package com.example.gossipapp.presentation.screens.study

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gossipapp.data.local.entity.FlashcardEntity
import com.example.gossipapp.data.local.entity.NoteEntity
import com.example.gossipapp.presentation.components.FlashcardDeckViewerDialog
import com.example.gossipapp.presentation.viewmodel.CampuslyUiState

@Composable
fun StudyScreen(
    uiState: CampuslyUiState,
    onOpenAddNote: () -> Unit,
    onOpenEditNote: (NoteEntity) -> Unit,
    onDeleteNote: (NoteEntity) -> Unit,
    onOpenStudyTimer: () -> Unit,
    onOpenAiAssistant: () -> Unit,
    onOpenFlashcardGenerator: () -> Unit,
    onDeleteFlashcardCard: (FlashcardEntity) -> Unit,
    onSaveEditedFlashcardCard: (FlashcardEntity) -> Unit,
    onDeleteFlashcardDeck: (deckName: String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Notes, 1 = Flashcards, 2 = Analytics
    var activeStudyDeckName by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Study Tools",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onOpenStudyTimer) {
                        Icon(Icons.Default.Timer, contentDescription = "Study Timer", tint = MaterialTheme.colorScheme.primary)
                    }

                    Button(
                        onClick = onOpenAiAssistant,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI Assistant")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Notes (${uiState.notes.size})") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Flashcards (${uiState.flashcards.size})") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Analytics") })
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> NotesTab(uiState.notes, onOpenAddNote, onOpenEditNote, onDeleteNote)
                1 -> FlashcardsTab(uiState.flashcards, onOpenFlashcardGenerator, onOpenDeck = { activeStudyDeckName = it }, onDeleteDeck = onDeleteFlashcardDeck)
                2 -> AnalyticsTab(uiState)
            }
        }
    }

    // Active Study Deck Viewer Overlay
    activeStudyDeckName?.let { deckName ->
        val deckCards = uiState.flashcards.filter { it.deckName == deckName }
        FlashcardDeckViewerDialog(
            deckName = deckName,
            cards = deckCards,
            onDismiss = { activeStudyDeckName = null },
            onDeleteCard = onDeleteFlashcardCard,
            onSaveEditedCard = onSaveEditedFlashcardCard
        )
    }
}

@Composable
fun NotesTab(
    notes: List<NoteEntity>,
    onOpenAddNote: () -> Unit,
    onOpenEditNote: (NoteEntity) -> Unit,
    onDeleteNote: (NoteEntity) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Course Notes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Button(onClick = onOpenAddNote, shape = RoundedCornerShape(8.dp)) {
                Text("+ Note")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (notes.isEmpty()) {
            Text(
                text = "Your study space is empty. Tap '+ Note' to capture lecture notes!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(notes, key = { it.id }) { note ->
                    NoteCard(
                        note = note,
                        onEdit = { onOpenEditNote(note) },
                        onDelete = { onDeleteNote(note) }
                    )
                }
            }
        }
    }
}

@Composable
fun NoteCard(
    note: NoteEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${note.courseName} • ${note.folder}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Note", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Note", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 4
            )
        }
    }
}

@Composable
fun FlashcardsTab(
    flashcards: List<FlashcardEntity>,
    onOpenFlashcardGenerator: () -> Unit,
    onOpenDeck: (deckName: String) -> Unit,
    onDeleteDeck: (deckName: String) -> Unit
) {
    val groupedDecks = remember(flashcards) {
        flashcards.groupBy { it.deckName }
    }
    var deckToDelete by remember { mutableStateOf<String?>(null) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Revision Flashcard Decks",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Button(onClick = onOpenFlashcardGenerator, shape = RoundedCornerShape(8.dp)) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Generate AI Deck 🎴")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (groupedDecks.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = "No flashcards generated yet. Tap 'Generate AI Deck 🎴' to generate cards from topics or lecture notes!",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(groupedDecks.keys.toList()) { deckName ->
                    val deckCards = groupedDecks[deckName] ?: emptyList()
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenDeck(deckName) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = MaterialTheme.colorScheme.secondary,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${deckCards.size} CARDS",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = deckName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Button(
                                    onClick = { onOpenDeck(deckName) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Study 📖")
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(onClick = { deckToDelete = deckName }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Deck", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirmation Dialog before deleting entire deck
    deckToDelete?.let { name ->
        val cardCount = groupedDecks[name]?.size ?: 0
        AlertDialog(
            onDismissRequest = { deckToDelete = null },
            title = { Text("Delete Flashcard Deck") },
            text = { Text("Are you sure you want to delete deck '$name'? This will permanently remove all $cardCount cards in this deck.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteDeck(name)
                        deckToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Deck")
                }
            },
            dismissButton = {
                TextButton(onClick = { deckToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AnalyticsTab(uiState: CampuslyUiState) {
    val focusSessions = uiState.studySessions
        .filter { !it.isBreak }
        .distinctBy { it.sessionId }

    val totalMinutes = focusSessions.sumOf { it.durationMinutes }
    val hours = totalMinutes / 60
    val mins = totalMinutes % 60
    val totalHoursStr = "${hours}h ${mins}m"

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "POMODORO FOCUS METRICS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Recorded Focus Time", style = MaterialTheme.typography.bodySmall)
                        Text(
                            text = totalHoursStr,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column {
                        Text(text = "Focus Sessions", style = MaterialTheme.typography.bodySmall)
                        Text(
                            text = "${focusSessions.size} Sessions",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Text(
            text = "Recorded Pomodoro Sessions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        if (focusSessions.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = "No study sessions recorded yet. Start a Pomodoro timer session above to track your focus time!",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(focusSessions, key = { it.sessionId }) { session ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = session.courseName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = session.topic,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = "${session.durationMinutes} min focus",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
