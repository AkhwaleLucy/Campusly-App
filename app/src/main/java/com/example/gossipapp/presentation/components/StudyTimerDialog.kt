package com.example.gossipapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gossipapp.data.local.entity.CourseEntity
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun StudyTimerDialog(
    courses: List<CourseEntity>,
    onDismiss: () -> Unit,
    onRecordSession: (courseName: String, topic: String, durationMinutes: Int) -> Unit
) {
    var selectedCourse by remember { mutableStateOf(courses.firstOrNull()?.name ?: "Data Structures & Algorithms") }
    var topic by remember { mutableStateOf("Linked Lists Practice") }
    var totalSeconds by remember { mutableIntStateOf(25 * 60) } // 25 min Pomodoro default
    var secondsLeft by remember { mutableIntStateOf(totalSeconds) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning && secondsLeft > 0) {
            delay(1000L)
            secondsLeft -= 1
        }
        if (secondsLeft == 0 && isRunning) {
            isRunning = false
        }
    }

    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60
    val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Campusly Study Timer", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCourse,
                    onValueChange = { selectedCourse = it },
                    label = { Text("Course") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text("Topic") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = totalSeconds == 25 * 60,
                        onClick = {
                            totalSeconds = 25 * 60
                            secondsLeft = totalSeconds
                            isRunning = false
                        },
                        label = { Text("25 min Pomodoro") }
                    )
                    FilterChip(
                        selected = totalSeconds == 50 * 60,
                        onClick = {
                            totalSeconds = 50 * 60
                            secondsLeft = totalSeconds
                            isRunning = false
                        },
                        label = { Text("50 min Session") }
                    )
                }

                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = timeFormatted,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { isRunning = !isRunning },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    IconButton(
                        onClick = {
                            isRunning = false
                            secondsLeft = totalSeconds
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val completedMinutes = (totalSeconds - secondsLeft) / 60
                    if (completedMinutes > 0) {
                        onRecordSession(selectedCourse, topic, completedMinutes)
                    }
                    onDismiss()
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Finish Session & Record")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
