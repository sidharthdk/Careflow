package com.careflow.app.feature.home

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.careflow.app.CareFlowApplication
import com.careflow.app.core.ui.theme.Success
import com.careflow.app.core.ui.theme.TextSecondary
import com.careflow.app.core.utils.DateTimeUtils
import com.careflow.app.domain.model.CareTask
import com.careflow.app.domain.model.MeasurementType
import com.careflow.app.feature.documents.DocumentCaptureDialog

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val repository = (context.applicationContext as CareFlowApplication).repository
    val viewModel = remember { HomeViewModel(repository) }
    val healthUpdateViewModel = remember { HealthUpdateViewModel(repository) }
    val uiState by viewModel.uiState.collectAsState()
    val healthUpdateState by healthUpdateViewModel.uiState.collectAsState()

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
                    text = uiState.greeting,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Here's your care for today",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
        item {
            Text(
                text = "Today's Care",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        if (uiState.totalTasksCount > 0) {
            item {
                ProgressCard(completed = uiState.completedTasksCount, total = uiState.totalTasksCount)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        items(uiState.todayTasks) { task ->
            TaskCard(
                task = task,
                onToggleComplete = { viewModel.toggleTaskCompletion(task.id, !task.isCompleted) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item {
            AddHealthUpdateButton(onClick = { viewModel.showAddHealthUpdateDialog() })
            Spacer(modifier = Modifier.height(24.dp))
        }
        uiState.currentEpisode?.let { episode ->
            item {
                CareEpisodeCard(episode = episode, nextAppointment = uiState.nextAppointment)
            }
        }
    }

    if (uiState.showAddHealthUpdateDialog) {
        AddHealthUpdateDialog(
            onDismiss = { viewModel.hideAddHealthUpdateDialog() },
            onAddSymptom = {
                viewModel.hideAddHealthUpdateDialog()
                healthUpdateViewModel.showSymptomDialog()
            },
            onAddMeasurement = {
                viewModel.hideAddHealthUpdateDialog()
                healthUpdateViewModel.showMeasurementDialog()
            },
            onAddNote = {
                viewModel.hideAddHealthUpdateDialog()
                healthUpdateViewModel.showNoteDialog()
            }
        )
    }

    when (healthUpdateState.activeDialog) {
        is HealthUpdateDialog.Symptom -> {
            AddSymptomDialog(
                isSaving = healthUpdateState.isSaving,
                onDismiss = { healthUpdateViewModel.dismissDialog() },
                onSave = { symptom, severity, notes ->
                    healthUpdateViewModel.saveSymptom(symptom, severity, notes)
                }
            )
        }
        is HealthUpdateDialog.Measurement -> {
            AddMeasurementDialog(
                isSaving = healthUpdateState.isSaving,
                onDismiss = { healthUpdateViewModel.dismissDialog() },
                onSave = { type, value, unit, notes ->
                    healthUpdateViewModel.saveMeasurement(type, value, unit, notes)
                }
            )
        }
        is HealthUpdateDialog.Note -> {
            AddNoteDialog(
                isSaving = healthUpdateState.isSaving,
                onDismiss = { healthUpdateViewModel.dismissDialog() },
                onSave = { content -> healthUpdateViewModel.saveNote(content) }
            )
        }
        is HealthUpdateDialog.None -> {}
    }
}

@Composable
fun ProgressCard(completed: Int, total: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$completed of $total completed",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Success,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { if (total > 0) completed.toFloat() / total else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface,
            )
        }
    }
}

@Composable
fun TaskCard(task: CareTask, onToggleComplete: () -> Unit) {
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
            Column(modifier = Modifier.width(70.dp), horizontalAlignment = Alignment.Start) {
                Text(
                    text = DateTimeUtils.formatTime(task.scheduledTime),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
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
                    contentDescription = "Not completed",
                    tint = TextSecondary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun AddHealthUpdateButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Add health update", style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun CareEpisodeCard(
    episode: com.careflow.app.domain.model.CareEpisode,
    nextAppointment: com.careflow.app.domain.model.Appointment?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = episode.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Day ${episode.currentDay} of ${episode.totalDays}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { episode.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
            )
            nextAppointment?.let { appointment ->
                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Next appointment",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = DateTimeUtils.formatDate(appointment.scheduledTime.toLocalDate()),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

// ── Add Health Update Dialog ──────────────────────────────────────────────────

@Composable
fun AddHealthUpdateDialog(
    onDismiss: () -> Unit,
    onAddSymptom: () -> Unit,
    onAddMeasurement: () -> Unit,
    onAddNote: () -> Unit
) {
    var showDocumentCapture by remember { mutableStateOf(false) }

    if (showDocumentCapture) {
        DocumentCaptureDialog(
            onDismiss = {
                showDocumentCapture = false
                onDismiss()
            },
            onDocumentCaptured = {
                showDocumentCapture = false
                onDismiss()
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Add health update", style = MaterialTheme.typography.headlineSmall) },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    HealthUpdateOption(icon = Icons.Default.Info, text = "Add symptom", onClick = onAddSymptom)
                    HealthUpdateOption(icon = Icons.Default.Favorite, text = "Add measurement", onClick = onAddMeasurement)
                    HealthUpdateOption(icon = Icons.Default.Create, text = "Add note", onClick = onAddNote)
                    HealthUpdateOption(icon = Icons.Default.Description, text = "Add document", onClick = { showDocumentCapture = true })
                }
            },
            confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun HealthUpdateOption(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

// ── Symptom Dialog ────────────────────────────────────────────────────────────

private val symptomOptions = listOf("Pain", "Fatigue", "Dizziness", "Nausea", "Swelling", "Shortness of breath", "Other")
private val nonPainSeverityOptions = listOf("Mild", "Moderate", "Severe")

@Composable
fun AddSymptomDialog(
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (symptom: String, severity: String?, notes: String) -> Unit
) {
    var selectedSymptom by remember { mutableStateOf(symptomOptions[0]) }
    var painSlider by remember { mutableStateOf(5f) }
    var selectedNonPainSeverity by remember { mutableStateOf(nonPainSeverityOptions[0]) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log a symptom", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Symptom", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                symptomOptions.forEach { symptom ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSymptom = symptom }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedSymptom == symptom, onClick = { selectedSymptom = symptom })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(symptom, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                HorizontalDivider()

                if (selectedSymptom == "Pain") {
                    Text("Pain level: ${painSlider.toInt()} / 10", style = MaterialTheme.typography.labelLarge)
                    Slider(
                        value = painSlider,
                        onValueChange = { painSlider = it },
                        valueRange = 0f..10f,
                        steps = 9,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text("Severity", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        nonPainSeverityOptions.forEach { sev ->
                            FilterChip(
                                selected = selectedNonPainSeverity == sev,
                                onClick = { selectedNonPainSeverity = sev },
                                label = { Text(sev) }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val severity = if (selectedSymptom == "Pain") painSlider.toInt().toString()
                    else selectedNonPainSeverity
                    onSave(selectedSymptom, severity, notes)
                },
                enabled = !isSaving
            ) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                else Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        shape = RoundedCornerShape(20.dp)
    )
}

// ── Measurement Dialog ────────────────────────────────────────────────────────

private data class MeasurementConfig(
    val type: MeasurementType,
    val label: String,
    val unit: String,
    val hint: String
)

private val measurementConfigs = listOf(
    MeasurementConfig(MeasurementType.BLOOD_PRESSURE, "Blood Pressure", "mmHg", "120/80"),
    MeasurementConfig(MeasurementType.HEART_RATE, "Heart Rate", "bpm", "72"),
    MeasurementConfig(MeasurementType.TEMPERATURE, "Temperature", "°C", "36.6"),
    MeasurementConfig(MeasurementType.BLOOD_SUGAR, "Blood Glucose", "mg/dL", "100"),
    MeasurementConfig(MeasurementType.OXYGEN_SATURATION, "SpO2", "%", "98"),
    MeasurementConfig(MeasurementType.WEIGHT, "Weight", "kg", "70")
)

@Composable
fun AddMeasurementDialog(
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (type: MeasurementType, value: String, unit: String, notes: String) -> Unit
) {
    var selectedConfig by remember { mutableStateOf(measurementConfigs[0]) }
    var valueText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var valueError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add measurement", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Type", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    measurementConfigs.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { config ->
                                FilterChip(
                                    selected = selectedConfig == config,
                                    onClick = {
                                        selectedConfig = config
                                        valueText = ""
                                        valueError = false
                                    },
                                    label = { Text(config.label, style = MaterialTheme.typography.labelSmall) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                HorizontalDivider()
                OutlinedTextField(
                    value = valueText,
                    onValueChange = { valueText = it; valueError = false },
                    label = { Text("${selectedConfig.label} (${selectedConfig.unit})") },
                    placeholder = { Text(selectedConfig.hint) },
                    isError = valueError,
                    supportingText = if (valueError) { { Text("Please enter a value") } } else null,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (valueText.isBlank()) { valueError = true; return@Button }
                    onSave(selectedConfig.type, valueText.trim(), selectedConfig.unit, notes)
                },
                enabled = !isSaving
            ) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                else Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        shape = RoundedCornerShape(20.dp)
    )
}

// ── Note Dialog ───────────────────────────────────────────────────────────────

@Composable
fun AddNoteDialog(
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (content: String) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var contentError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add note", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it; contentError = false },
                    label = { Text("Your note") },
                    placeholder = { Text("Write anything about your health today...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    isError = contentError,
                    supportingText = if (contentError) { { Text("Note cannot be empty") } } else null
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (content.isBlank()) { contentError = true; return@Button }
                    onSave(content.trim())
                },
                enabled = !isSaving
            ) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                else Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        shape = RoundedCornerShape(20.dp)
    )
}
