package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Education(
    val institution: String = "",
    val degree: String = "",
    val fieldOfStudy: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val grade: String = ""
)

@JsonClass(generateAdapter = true)
data class Experience(
    val company: String = "",
    val role: String = "",
    val location: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val description: String = "",
    val isCurrent: Boolean = false
)

@JsonClass(generateAdapter = true)
data class Project(
    val title: String = "",
    val technologies: String = "",
    val description: String = "",
    val role: String = "",
    val link: String = "",
    val timeline: String = ""
)

@JsonClass(generateAdapter = true)
data class Skill(
    val name: String = "",
    val level: String = "Intermediate" // Beginner, Intermediate, Expert
)

@JsonClass(generateAdapter = true)
data class Certificate(
    val title: String = "",
    val issuer: String = "",
    val date: String = "",
    val url: String = ""
)

@JsonClass(generateAdapter = true)
data class Language(
    val name: String = "",
    val proficiency: String = "Fluent" // Basic, Conversational, Fluent, Native
)

@JsonClass(generateAdapter = true)
data class Reference(
    val name: String = "",
    val company: String = "",
    val email: String = "",
    val phone: String = "",
    val relation: String = ""
)
