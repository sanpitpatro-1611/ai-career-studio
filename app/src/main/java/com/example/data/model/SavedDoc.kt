package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_docs")
data class SavedDoc(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "cover_letter", "linkedin", "interview", "career"
    val title: String,
    val content: String,
    val meta: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
