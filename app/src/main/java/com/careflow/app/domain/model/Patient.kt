package com.careflow.app.domain.model

data class Patient(
    val id: String,
    val name: String,
    val age: Int,
    val preferredLanguage: String = "English"
)
