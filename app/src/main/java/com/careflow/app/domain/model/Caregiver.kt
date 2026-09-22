package com.careflow.app.domain.model

data class Caregiver(
    val id: String,
    val patientId: String,
    val name: String,
    val relationship: String,
    val phoneNumber: String? = null,
    val email: String? = null
)
