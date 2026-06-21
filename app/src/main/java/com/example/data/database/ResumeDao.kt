package com.example.data.database

import androidx.room.*
import com.example.data.model.Resume
import kotlinx.coroutines.flow.Flow

@Dao
interface ResumeDao {
    @Query("SELECT * FROM resumes ORDER BY lastModified DESC")
    fun getAllResumes(): Flow<List<Resume>>

    @Query("SELECT * FROM resumes WHERE id = :id")
    suspend fun getResumeById(id: Long): Resume?

    @Query("SELECT * FROM resumes WHERE id = :id")
    fun getResumeByIdFlow(id: Long): Flow<Resume?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResume(resume: Resume): Long

    @Delete
    suspend fun deleteResume(resume: Resume)
}
