package com.example.flashcards.data.repository

import com.example.flashcards.data.dao.CardDao
import com.example.flashcards.data.model.Card
import kotlinx.coroutines.flow.Flow

class CardRepository(private val cardDao: CardDao) {
    fun getCardsByDeckId(deckId: Long): Flow<List<Card>> = cardDao.getCardsByDeckId(deckId)

    suspend fun insertCard(card: Card): Long = cardDao.insert(card)

    suspend fun updateCard(card: Card) = cardDao.update(card)

    suspend fun deleteCard(card: Card) = cardDao.delete(card)

    suspend fun getRandomCards(deckId: Long, limit: Int): List<Card> = 
        cardDao.getRandomCards(deckId, limit)

    suspend fun updateCardKnownStatus(cardId: Long, isKnown: Boolean) =
        cardDao.updateCardKnownStatus(cardId, isKnown)

    fun getUnknownCards(deckId: Long): Flow<List<Card>> = cardDao.getUnknownCards(deckId)

    suspend fun getRandomCardsExcluding(deckId: Long, excludedCardId: Long, limit: Int): List<Card> =
        cardDao.getRandomCardsExcluding(deckId, excludedCardId, limit)
} 