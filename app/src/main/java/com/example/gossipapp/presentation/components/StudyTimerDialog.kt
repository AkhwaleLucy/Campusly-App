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
import com.example.gossipapp.presentation.viewmodel.ActiveTimerState
import java.util.Locale

@Composable
fun StudyTimerDialog(
    courses: List<CourseEntity>,
    timerState: ActiveTimerState,
    onDismiss: () -> Unit,
    onStartTimer: (courseName: String, topic: String, durationMinutes: Int) -> Unit,
    onPauseTimer: () -> Unit,
    onResumeTimer: () -> Unit,
    onCancelTimer: () -> Unit,
    onRecordSession: (courseName: String, topic: String, durationMinutes: Int) -> Unit
) {
    var selectedCourse by remember { mutableStateOf(if (timerState.courseName.isNotBlank()) timerState.courseName else (courses.firstOrNull()?.name ?: "General Study")) }
    var topic by remember { mutableStateOf(if (timerState.topic.isNotBlank()) timerState.topic else "Active Recall Session") }
    var durationMinutes by remember { mutableIntStateOf(25) }

    val remainingSecs = if (timerState.isRunning) timerState.remainingSeconds else (durationMinutes * 60)
    val minutes = remainingSecs / 60
    val seconds = remainingSecs % 60
    val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Campusly Focus Timer ⏱️", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!timerState.isRunning) {
                    OutlinedTextField(
                        value = selectedCourse,
                        onValueChange = { selectedCourse = it },
                        label = { Text("Course / Subject") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        label = { Text("Topic Summary") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilterChip(
                            selected = durationMinutes == 25,
                            onClick = { durationMinutes = 25 },
                            label = { Text("25m Pomodoro") }
                        )
                        FilterChip(
                            selected = durationMinutes == 50,
                            onClick = { durationMinutes = 50 },
                            label = { Text("50m Deep Focus") }
                        )
                    }
                } else {
                    Text(
                        text = "Course: ${timerState.courseName} • ${timerState.topic}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(if (timerState.isRunning && !timerState.isPaused) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = timeFormatted,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (!timerState.isRunning) {
                                onStartTimer(selectedCourse, topic, durationMinutes)
                            } else if (timerState.isPaused) {
                                onResumeTimer()
                            } else {
                                onPauseTimer()
                            }
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = if (timerState.isRunning && !timerState.isPaused) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    if (timerState.isRunning) {
                        IconButton(
                            onClick = {
                                onCancelTimer()
                            },
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (timerState.isRunning) {
                Button(
                    onClick = {
                        val doneSecs = timerState.totalSeconds - timerState.remainingSeconds
                        val mins = maxOf(1, doneSecs / 60)
                        onRecordSession(timerState.courseName, timerState.topic, mins)
                        onCancelTimer()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Complete & Save Session")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close Window") }
        }
    )
}
