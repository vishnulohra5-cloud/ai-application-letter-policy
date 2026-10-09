package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "letters")
data class LetterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String,
    val recipient: String,
    val institution: String = "",
    val applicantName: String,
    val subject: String,
    val content: String,
    val language: String = "hi",
    val dateString: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "ड्राफ्ट", // "स्वीकृत", "ड्राफ्ट", "सहेजा गया"
    val isFavorite: Boolean = false,
    val includeMedicalCert: Boolean = false,
    val leaveDays: Int = 2
)
