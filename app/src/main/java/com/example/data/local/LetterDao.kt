package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LetterDao {
    @Query("SELECT * FROM letters ORDER BY timestamp DESC")
    fun getAllLetters(): Flow<List<LetterEntity>>

    @Query("SELECT * FROM letters WHERE category = :category ORDER BY timestamp DESC")
    fun getLettersByCategory(category: String): Flow<List<LetterEntity>>

    @Query("SELECT * FROM letters WHERE status = :status ORDER BY timestamp DESC")
    fun getLettersByStatus(status: String): Flow<List<LetterEntity>>

    @Query("SELECT * FROM letters WHERE id = :id LIMIT 1")
    suspend fun getLetterById(id: Long): LetterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLetter(letter: LetterEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(letters: List<LetterEntity>)

    @Update
    suspend fun updateLetter(letter: LetterEntity)

    @Delete
    suspend fun deleteLetter(letter: LetterEntity)

    @Query("DELETE FROM letters WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM letters")
    suspend fun getCount(): Int
}
