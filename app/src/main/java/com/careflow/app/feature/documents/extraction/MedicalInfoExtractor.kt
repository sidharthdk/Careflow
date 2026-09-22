package com.careflow.app.feature.documents.extraction

data class ExtractedMedication(
    val name: String,
    val dosage: String?,
    val frequency: String?,
    val timing: String?,
    val durationDays: Int?
)

data class ExtractionResult(
    val medications: List<ExtractedMedication>
)

interface MedicalInfoExtractor {
    fun extract(text: String): ExtractionResult
}
