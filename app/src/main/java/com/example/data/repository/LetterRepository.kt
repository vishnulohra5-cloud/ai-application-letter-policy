package com.example.data.repository

import com.example.data.local.LetterDao
import com.example.data.local.LetterEntity
import kotlinx.coroutines.flow.Flow

class LetterRepository(private val letterDao: LetterDao) {

    val allLetters: Flow<List<LetterEntity>> = letterDao.getAllLetters()

    fun getLettersByCategory(category: String): Flow<List<LetterEntity>> {
        return letterDao.getLettersByCategory(category)
    }

    fun getLettersByStatus(status: String): Flow<List<LetterEntity>> {
        return letterDao.getLettersByStatus(status)
    }

    suspend fun getLetterById(id: Long): LetterEntity? {
        return letterDao.getLetterById(id)
    }

    suspend fun insertLetter(letter: LetterEntity): Long {
        return letterDao.insertLetter(letter)
    }

    suspend fun updateLetter(letter: LetterEntity) {
        letterDao.updateLetter(letter)
    }

    suspend fun deleteLetter(letter: LetterEntity) {
        letterDao.deleteLetter(letter)
    }

    suspend fun deleteById(id: Long) {
        letterDao.deleteById(id)
    }
}
