package com.careflow.app.feature.careplan

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.careflow.app.CareFlowApplication
import com.careflow.app.core.ui.theme.Success
import com.careflow.app.core.ui.theme.TextSecondary
import com.careflow.app.core.utils.DateTimeUtils
import com.careflow.app.domain.model.CareTask
import com.careflow.app.domain.model.TaskType

@Composable
fun CarePlanScreen() {
    val context = LocalContext.current
    val repository = (context.applicationContext as CareFlowApplication).repository
    val viewModel = remember { CarePlanViewModel(repository) }
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // ── Header ──────────────────────────────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "Care Plan",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your schedule and activities",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // ── Morning ──────────────────────────────────────────────────────────
        if (uiState.morningTasks.isNotEmpty()) {
            item { SectionHeader("Morning") }
            items(uiState.morningTasks) { task ->
                CarePlanTaskCard(task) { viewModel.toggleTaskCompletion(task.id, !task.isCompleted) }
                Spacer(modifier = Modifier.height(12.dp))
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        // ── Afternoon ────────────────────────────────────────────────────────
        if (uiState.afternoonTasks.isNotEmpty()) {
            item { SectionHeader("Afternoon") }
            items(uiState.afternoonTasks) { task ->
                CarePlanTaskCard(task) { viewModel.toggleTaskCompletion(task.id, !task.isCompleted) }
                Spacer(modifier = Modifier.height(12.dp))
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        // ── Evening ──────────────────────────────────────────────────────────
        if (uiState.eveningTasks.isNotEmpty()) {
            item { SectionHeader("Evening") }
            items(uiState.eveningTasks) { task ->
                CarePlanTaskCard(task) { viewModel.toggleTaskCompletion(task.id, !task.isCompleted) }
                Spacer(modifier = Modifier.height(12.dp))
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        // ── Upcoming ─────────────────────────────────────────────────────────
        if (uiState.upcomingTasks.isNotEmpty()) {
            item { SectionHeader("Upcoming") }
            items(uiState.upcomingTasks) { task ->
                CarePlanTaskCard(task) { viewModel.toggleTaskCompletion(task.id, !task.isCompleted) }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Empty state
        if (uiState.morningTasks.isEmpty() &&
            uiState.afternoonTasks.isEmpty() &&
            uiState.eveningTasks.isEmpty() &&
            uiState.upcomingTasks.isEmpty()
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tasks scheduled",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
fun CarePlanTaskCard(
    task: CareTask,
    onToggleComplete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleComplete)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Completion toggle
            if (task.isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = Success,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Mark complete",
                    tint = TextSecondary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Task details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = DateTimeUtils.formatTime(task.scheduledTime),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            // Task-type indicator icon — using only core Material icons
            Icon(
                imageVector = when (task.taskType) {
                    TaskType.MEDICATION   -> Icons.Default.LocalPharmacy
                    TaskType.MEASUREMENT  -> Icons.Default.Favorite
                    TaskType.CHECKIN      -> Icons.Default.Chat
                    TaskType.APPOINTMENT  -> Icons.Default.CalendarMonth
                    else                  -> Icons.Default.Circle
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
