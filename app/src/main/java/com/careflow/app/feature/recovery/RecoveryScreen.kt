package com.careflow.app.feature.recovery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.careflow.app.CareFlowApplication
import com.careflow.app.core.ui.theme.Success
import com.careflow.app.core.ui.theme.TextSecondary
import com.careflow.app.core.utils.DateTimeUtils
import com.careflow.app.domain.model.FeelingLevel
import com.careflow.app.domain.model.HealthMeasurement
import com.careflow.app.domain.model.NoteEntry
import com.careflow.app.domain.model.RecoveryEntry
import com.careflow.app.domain.model.SymptomEntry

@Composable
fun RecoveryScreen() {
    val context = LocalContext.current
    val repository = (context.applicationContext as CareFlowApplication).repository
    val viewModel = remember { RecoveryViewModel(repository) }
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
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "Recovery",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your progress since your last visit",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        uiState.currentEpisode?.let { episode ->
            item {
                RecoveryOverviewCard(
                    currentDay = episode.currentDay,
                    totalDays = episode.totalDays,
                    overallFeeling = uiState.overallFeeling
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        item {
            Button(
                onClick = { viewModel.showLogRecoveryDialog() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Log today's recovery", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                text = "Recovery Timeline",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(uiState.timelineItems) { item ->
            when (item) {
                is RecoveryTimelineItem.CheckIn -> RecoveryCheckInCard(entry = item.entry)
                is RecoveryTimelineItem.Symptom -> RecoverySymptomCard(entry = item.entry)
                is RecoveryTimelineItem.Measurement -> RecoveryMeasurementCard(entry = item.entry)
                is RecoveryTimelineItem.Note -> RecoveryNoteCard(entry = item.entry)
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    if (uiState.showLogDialog) {
        LogRecoveryDialog(
            onDismiss = { viewModel.hideLogRecoveryDialog() },
            onSave = { feeling, symptoms, notes ->
                viewModel.saveRecoveryEntry(feeling, symptoms, notes)
            }
        )
    }
}

@Composable
fun RecoveryOverviewCard(currentDay: Int, totalDays: Int, overallFeeling: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Recovery day", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentDay.toString(),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            VerticalDivider(modifier = Modifier.height(60.dp), color = MaterialTheme.colorScheme.outline)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Overall check-in", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = overallFeeling,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Success
                )
            }
        }
    }
}

@Composable
fun RecoveryCheckInCard(entry: RecoveryEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DAY ${getDayFromTimestamp(entry.timestamp)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = entry.feeling.displayName,
                    style = MaterialTheme.typography.labelLarge,
                    color = Success,
                    fontWeight = FontWeight.SemiBold
                )
            }
            entry.notes?.let { notes ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = notes, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            }
            if (entry.symptoms.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    entry.symptoms.forEach { symptom ->
                        SuggestionChip(onClick = {}, label = { Text(symptom, style = MaterialTheme.typography.labelSmall) })
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = DateTimeUtils.formatDateTime(entry.timestamp), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
fun RecoverySymptomCard(entry: SymptomEntry) {
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
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Symptom", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Text(
                    text = entry.symptom,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                entry.severity?.let { sev ->
                    Spacer(modifier = Modifier.height(4.dp))
                    val severityLabel = if (entry.symptom == "Pain") "Pain level: $sev/10" else "Severity: $sev"
                    Text(text = severityLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                entry.notes?.let { notes ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = notes, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = DateTimeUtils.formatDateTime(entry.timestamp), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}

@Composable
fun RecoveryMeasurementCard(entry: HealthMeasurement) {
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
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = entry.measurementType.displayName, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Text(
                    text = "${entry.value} ${entry.unit}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                entry.notes?.let { notes ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = notes, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = DateTimeUtils.formatDateTime(entry.timestamp), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}

@Composable
fun RecoveryNoteCard(entry: NoteEntry) {
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
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Create,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Note", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Text(text = entry.content, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = DateTimeUtils.formatDateTime(entry.timestamp), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}

@Composable
fun LogRecoveryDialog(
    onDismiss: () -> Unit,
    onSave: (FeelingLevel, List<String>, String) -> Unit
) {
    var selectedFeeling by remember { mutableStateOf<FeelingLevel?>(null) }
    var selectedSymptoms by remember { mutableStateOf<Set<String>>(emptySet()) }
    var notes by remember { mutableStateOf("") }

    val feelingOptions = listOf(
        FeelingLevel.GREAT,
        FeelingLevel.GOOD,
        FeelingLevel.OKAY,
        FeelingLevel.NOT_GREAT,
        FeelingLevel.POOR
    )
    val symptomOptions = listOf("Pain", "Fatigue", "Dizziness", "Nausea", "Other")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text(
                    text = "Log today's recovery",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "How are you feeling?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(12.dp))
                feelingOptions.forEach { feeling ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(selected = selectedFeeling == feeling, onClick = { selectedFeeling = feeling })
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedFeeling == feeling, onClick = { selectedFeeling = feeling })
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = feeling.displayName, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "Any symptoms?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(12.dp))
                symptomOptions.forEach { symptom ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedSymptoms.contains(symptom),
                            onCheckedChange = { checked ->
                                selectedSymptoms = if (checked) selectedSymptoms + symptom else selectedSymptoms - symptom
                            }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = symptom, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "Additional notes (optional)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("How do you feel today?") },
                    minLines = 3,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(
                        onClick = {
                            selectedFeeling?.let { feeling ->
                                onSave(feeling, selectedSymptoms.toList(), notes)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = selectedFeeling != null
                    ) { Text("Save") }
                }
            }
        }
    }
}

private fun getDayFromTimestamp(timestamp: java.time.LocalDateTime): Int {
    val startDate = java.time.LocalDate.of(2026, 9, 19)
    val entryDate = timestamp.toLocalDate()
    return java.time.temporal.ChronoUnit.DAYS.between(startDate, entryDate).toInt() + 1
}
