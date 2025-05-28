package com.example.flashcards.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.flashcards.data.model.Card
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Insert
    suspend fun insert(card: Card): Long

    @Update
    suspend fun update(card: Card)

    @Delete
    suspend fun delete(card: Card)

    @Query("SELECT * FROM cards WHERE deckId = :deckId ORDER BY createdAt DESC")
    fun getCardsByDeckId(deckId: Long): Flow<List<Card>>

    @Query("SELECT * FROM cards WHERE deckId = :deckId ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomCards(deckId: Long, limit: Int): List<Card>

    @Query("UPDATE cards SET isKnown = :isKnown, lastReviewedAt = :timestamp WHERE id = :cardId")
    suspend fun updateCardKnownStatus(cardId: Long, isKnown: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM cards WHERE deckId = :deckId AND isKnown = 0")
    fun getUnknownCards(deckId: Long): Flow<List<Card>>

    @Query("SELECT * FROM cards WHERE id != :excludedCardId AND deckId = :deckId ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomCardsExcluding(deckId: Long, excludedCardId: Long, limit: Int): List<Card>
} 