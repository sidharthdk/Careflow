package com.careflow.app.feature.documents.extraction

class RuleBasedMedicalInfoExtractor : MedicalInfoExtractor {

    override fun extract(text: String): ExtractionResult {
        val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }
        val medications = mutableListOf<ExtractedMedication>()

        for (line in lines) {
            val name = extractMedicationName(line) ?: continue
            medications.add(
                ExtractedMedication(
                    name = name,
                    dosage = extractDosage(line),
                    frequency = extractFrequency(line),
                    timing = extractTiming(line),
                    durationDays = extractDuration(line)
                )
            )
        }

        return ExtractionResult(medications = medications.distinctBy { it.name.lowercase() })
    }

    private fun extractMedicationName(line: String): String? {
        // Lines containing a drug-like name: word(s) followed by a dose unit or form marker
        val medicationPattern = Regex(
            """([A-Za-z][A-Za-z0-9\s\-]{1,40}?)\s*(?:\d+\s*(?:mg|mcg|ml|g\b|iu|units?)|(?:tablet|capsule|syrup|injection|drops?|patch|inhaler))""",
            RegexOption.IGNORE_CASE
        )
        val match = medicationPattern.find(line) ?: return null
        val candidate = match.groupValues[1].trim()
        if (candidate.length < 3) return null
        if (candidate.matches(Regex("""^\d+$"""))) return null
        return candidate.split(Regex("\\s+"))
            .joinToString(" ") { w -> w.replaceFirstChar { it.uppercase() } }
    }

    private fun extractDosage(line: String): String? {
        val dosagePattern = Regex(
            """(\d+(?:\.\d+)?)\s*(mg|mcg|ml|g\b|iu|units?|tablets?|capsules?|drops?)""",
            RegexOption.IGNORE_CASE
        )
        return dosagePattern.find(line)?.value?.trim()
    }

    private fun extractFrequency(line: String): String? {
        val patterns = listOf(
            Regex("""(?i)\bonce\s+(?:a\s+)?daily\b"""),
            Regex("""(?i)\btwice\s+(?:a\s+)?daily\b"""),
            Regex("""(?i)\bthrice\s+(?:a\s+)?daily\b"""),
            Regex("""(?i)\b(?:1|one)\s*x\s*(?:a\s+)?day\b"""),
            Regex("""(?i)\b(?:2|two)\s*x\s*(?:a\s+)?day\b"""),
            Regex("""(?i)\b(?:3|three)\s*x\s*(?:a\s+)?day\b"""),
            Regex("""(?i)\bevery\s+\d+\s+hours?\b"""),
            Regex("""(?i)\bOD\b"""),
            Regex("""(?i)\bBD\b"""),
            Regex("""(?i)\bTDS\b"""),
            Regex("""(?i)\bQID\b"""),
            Regex("""(?i)\bonce\s+a\s+week\b"""),
            Regex("""(?i)\bweekly\b""")
        )
        for (p in patterns) {
            val m = p.find(line)
            if (m != null) return m.value.trim()
        }
        return null
    }

    private fun extractTiming(line: String): String? {
        val patterns = listOf(
            Regex("""(?i)(?:before|after)\s+(?:food|meals?|breakfast|lunch|dinner|eating)"""),
            Regex("""(?i)(?:with|without)\s+food"""),
            Regex("""(?i)\bat\s+bedtime\b"""),
            Regex("""(?i)\bin\s+the\s+morning\b"""),
            Regex("""(?i)\bat\s+night\b"""),
            Regex("""(?i)\bempty\s+stomach\b""")
        )
        for (p in patterns) {
            val m = p.find(line)
            if (m != null) return m.value.trim()
        }
        return null
    }

    private fun extractDuration(line: String): Int? {
        val dayPattern = Regex("""(?i)(?:for\s+)?(\d+)\s+days?""")
        val weekPattern = Regex("""(?i)(?:for\s+)?(\d+)\s+weeks?""")
        dayPattern.find(line)?.let { return it.groupValues[1].toIntOrNull() }
        weekPattern.find(line)?.let { return (it.groupValues[1].toIntOrNull() ?: return null) * 7 }
        return null
    }
}
