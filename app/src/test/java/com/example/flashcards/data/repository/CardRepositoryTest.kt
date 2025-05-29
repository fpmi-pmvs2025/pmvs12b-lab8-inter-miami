package com.example.flashcards.data.repository

import com.example.flashcards.data.dao.CardDao
import com.example.flashcards.data.model.Card
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat

class CardRepositoryTest {
    private lateinit var cardDao: CardDao
    private lateinit var cardRepository: CardRepository

    @Before
    fun setup() {
        cardDao = mockk(relaxed = true)
        cardRepository = CardRepository(cardDao)
    }

    @Test
    fun `getCardsByDeckId returns flow of cards`() = runTest {
        val deckId = 1L
        val cards = listOf(
            Card(id = 1, deckId = deckId, word = "Hello", translation = "Привет"),
            Card(id = 2, deckId = deckId, word = "World", translation = "Мир")
        )
        
        coEvery { cardDao.getCardsByDeckId(deckId) } returns flowOf(cards)
        
        val result = cardRepository.getCardsByDeckId(deckId)
        
        result.collect { resultCards ->
            assertThat(resultCards).isEqualTo(cards)
        }
    }

    @Test
    fun `insertCard returns card id`() = runTest {
        val card = Card(id = 1, deckId = 1, word = "Hello", translation = "Привет")
        val expectedId = 1L
        
        coEvery { cardDao.insert(card) } returns expectedId
        
        val result = cardRepository.insertCard(card)
        
        assertThat(result).isEqualTo(expectedId)
        coVerify { cardDao.insert(card) }
    }

    @Test
    fun `updateCard calls dao update`() = runTest {
        val card = Card(id = 1, deckId = 1, word = "Hello", translation = "Привет")
        
        cardRepository.updateCard(card)
        
        coVerify { cardDao.update(card) }
    }

    @Test
    fun `deleteCard calls dao delete`() = runTest {
        val card = Card(id = 1, deckId = 1, word = "Hello", translation = "Привет")
        
        cardRepository.deleteCard(card)
        
        coVerify { cardDao.delete(card) }
    }

    @Test
    fun `getRandomCards returns list of cards`() = runTest {
        val deckId = 1L
        val limit = 5
        val cards = listOf(
            Card(id = 1, deckId = deckId, word = "Hello", translation = "Привет"),
            Card(id = 2, deckId = deckId, word = "World", translation = "Мир")
        )
        
        coEvery { cardDao.getRandomCards(deckId, limit) } returns cards
        
        val result = cardRepository.getRandomCards(deckId, limit)
        
        assertThat(result).isEqualTo(cards)
        coVerify { cardDao.getRandomCards(deckId, limit) }
    }

    @Test
    fun `getUnknownCards returns flow of unknown cards`() = runTest {
        val deckId = 1L
        val unknownCards = listOf(
            Card(id = 1, deckId = deckId, word = "Hello", translation = "Привет", isKnown = false),
            Card(id = 2, deckId = deckId, word = "World", translation = "Мир", isKnown = false)
        )
        
        coEvery { cardDao.getUnknownCards(deckId) } returns flowOf(unknownCards)
        
        val result = cardRepository.getUnknownCards(deckId)
        
        result.collect { resultCards ->
            assertThat(resultCards).isEqualTo(unknownCards)
        }
    }

    @Test
    fun `getRandomCardsExcluding returns list of cards`() = runTest {
        val deckId = 1L
        val excludedCardId = 1L
        val limit = 5
        val cards = listOf(
            Card(id = 2, deckId = deckId, word = "World", translation = "Мир"),
            Card(id = 3, deckId = deckId, word = "Test", translation = "Тест")
        )
        
        coEvery { cardDao.getRandomCardsExcluding(deckId, excludedCardId, limit) } returns cards
        
        val result = cardRepository.getRandomCardsExcluding(deckId, excludedCardId, limit)
        
        assertThat(result).isEqualTo(cards)
        coVerify { cardDao.getRandomCardsExcluding(deckId, excludedCardId, limit) }
    }
} 