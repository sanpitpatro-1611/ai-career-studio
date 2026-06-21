package com.example.data.database

import androidx.room.*
import com.example.data.model.SavedDoc
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedDocDao {
    @Query("SELECT * FROM saved_docs ORDER BY timestamp DESC")
    fun getAllSavedDocs(): Flow<List<SavedDoc>>

    @Query("SELECT * FROM saved_docs WHERE type = :type ORDER BY timestamp DESC")
    fun getSavedDocsByType(type: String): Flow<List<SavedDoc>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedDoc(doc: SavedDoc): Long

    @Delete
    suspend fun deleteSavedDoc(doc: SavedDoc)

    @Query("DELETE FROM saved_docs WHERE id = :id")
    suspend fun deleteSavedDocById(id: Long)
}
