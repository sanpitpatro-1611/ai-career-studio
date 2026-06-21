package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resumes")
data class Resume(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "My Professional Resume",
    val lastModified: Long = System.currentTimeMillis(),

    // Basic Contact Info
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val linkedin: String = "",
    val github: String = "",
    val portfolio: String = "",

    // Summary Objectives
    val objective: String = "",

    // Section JSON strings
    val educationsJson: String = "[]",
    val experiencesJson: String = "[]",
    val projectsJson: String = "[]",
    val skillsJson: String = "[]",
    val certificatesJson: String = "[]",
    val languagesJson: String = "[]",
    val achievementsJson: String = "[]",
    val hobbiesJson: String = "[]",
    val referencesJson: String = "[]",

    // Analysis results
    val atsScore: Int = 0,
    val atsSuggestionsJson: String = "[]", // Checklist of suggestions
    val atsFeedback: String = ""
)
