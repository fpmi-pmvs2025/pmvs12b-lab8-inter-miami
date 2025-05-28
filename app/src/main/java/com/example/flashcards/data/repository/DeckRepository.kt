package com.example.flashcards.data.repository

import com.example.flashcards.data.dao.DeckDao
import com.example.flashcards.data.model.Deck
import kotlinx.coroutines.flow.Flow

class DeckRepository(private val deckDao: DeckDao) {
    fun getAllDecks(): Flow<List<Deck>> = deckDao.getAllDecks()

    suspend fun getDeckById(id: Long): Deck? = deckDao.getDeckById(id)

    suspend fun insertDeck(deck: Deck): Long = deckDao.insert(deck)

    suspend fun updateDeck(deck: Deck) = deckDao.update(deck)

    suspend fun deleteDeck(deck: Deck) = deckDao.delete(deck)

    suspend fun getCardCount(deckId: Long): Int = deckDao.getCardCount(deckId)

    suspend fun getKnownCardCount(deckId: Long): Int = deckDao.getKnownCardCount(deckId)
} 