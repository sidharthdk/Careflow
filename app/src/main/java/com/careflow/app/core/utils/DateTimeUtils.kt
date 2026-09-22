package com.careflow.app.core.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object DateTimeUtils {
    
    fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 0..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    fun formatTime(dateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        return dateTime.format(formatter)
    }

    fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM d")
        return date.format(formatter)
    }

    fun formatDateTime(dateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy • hh:mm a")
        return dateTime.format(formatter)
    }

    fun formatDateShort(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMM d")
        return date.format(formatter)
    }

    fun isToday(dateTime: LocalDateTime): Boolean {
        val today = LocalDate.now()
        return dateTime.toLocalDate() == today
    }
}
