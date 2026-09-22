package com.careflow.app.feature.documents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.careflow.app.domain.model.CareTask
import com.careflow.app.domain.model.TaskType
import com.careflow.app.feature.documents.extraction.ExtractedMedication
import com.careflow.app.feature.documents.extraction.ExtractionResult
import java.time.LocalDate
import java.util.UUID

private data class ReviewableMedication(
    val original: ExtractedMedication,
    val editedName: String,
    val editedDosage: String,
    val editedFrequency: String,
    val editedTiming: String,
    val editedDuration: String,
    val included: Boolean = true
)

@Composable
fun ExtractionReviewDialog(
    extractionResult: ExtractionResult,
    onConfirm: (tasks: List<CareTask>) -> Unit,
    onSkip: () -> Unit
) {
    val reviewItems = remember(extractionResult) {
        extractionResult.medications.map { med ->
            mutableStateOf(
                ReviewableMedication(
                    original = med,
                    editedName = med.name,
                    editedDosage = med.dosage ?: "",
                    editedFrequency = med.frequency ?: "",
                    editedTiming = med.timing ?: "",
                    editedDuration = med.durationDays?.toString() ?: "",
                    included = true
                )
            )
        }
    }

    AlertDialog(
        onDismissRequest = onSkip,
        title = { Text("Review extracted medications", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "The following medications were found in this document. Review and edit if needed, then confirm to add them to your care plan.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                reviewItems.forEach { itemState ->
                    var item by itemState
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (item.included)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Include in care plan",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Switch(
                                    checked = item.included,
                                    onCheckedChange = { item = item.copy(included = it) }
                                )
                            }
                            if (item.included) {
                                OutlinedTextField(
                                    value = item.editedName,
                                    onValueChange = { item = item.copy(editedName = it) },
                                    label = { Text("Medication name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = item.editedDosage,
                                        onValueChange = { item = item.copy(editedDosage = it) },
                                        label = { Text("Dosage") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                    OutlinedTextField(
                                        value = item.editedFrequency,
                                        onValueChange = { item = item.copy(editedFrequency = it) },
                                        label = { Text("Frequency") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = item.editedTiming,
                                        onValueChange = { item = item.copy(editedTiming = it) },
                                        label = { Text("Timing") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                    OutlinedTextField(
                                        value = item.editedDuration,
                                        onValueChange = { item = item.copy(editedDuration = it) },
                                        label = { Text("Days") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val tasks = reviewItems
                    .map { it.value }
                    .filter { it.included && it.editedName.isNotBlank() }
                    .map { item ->
                        val descParts = listOf(item.editedDosage, item.editedFrequency, item.editedTiming)
                            .filter { it.isNotBlank() }
                        CareTask(
                            id = UUID.randomUUID().toString(),
                            careEpisodeId = "episode_1",
                            title = item.editedName,
                            description = descParts.joinToString(" • ").ifBlank { "Take as directed" },
                            scheduledTime = LocalDate.now().atTime(8, 0),
                            taskType = TaskType.MEDICATION
                        )
                    }
                onConfirm(tasks)
            }) {
                Text("Add to care plan")
            }
        },
        dismissButton = { TextButton(onClick = onSkip) { Text("Skip") } },
        shape = RoundedCornerShape(20.dp)
    )
}
