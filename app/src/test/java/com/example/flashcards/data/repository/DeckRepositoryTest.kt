package com.example.flashcards.data.repository

import com.example.flashcards.data.dao.DeckDao
import com.example.flashcards.data.model.Deck
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat

class DeckRepositoryTest {
    private lateinit var deckDao: DeckDao
    private lateinit var deckRepository: DeckRepository

    @Before
    fun setup() {
        deckDao = mockk(relaxed = true)
        deckRepository = DeckRepository(deckDao)
    }

    @Test
    fun `getAllDecks returns flow of decks`() = runTest {
        val decks = listOf(
            Deck(id = 1, name = "English"),
            Deck(id = 2, name = "Spanish")
        )
        
        coEvery { deckDao.getAllDecks() } returns flowOf(decks)
        
        val result = deckRepository.getAllDecks()
        
        result.collect { resultDecks ->
            assertThat(resultDecks).isEqualTo(decks)
        }
    }

    @Test
    fun `getDeckById returns deck`() = runTest {
        val deck = Deck(id = 1, name = "English")
        
        coEvery { deckDao.getDeckById(1) } returns deck
        
        val result = deckRepository.getDeckById(1)
        
        assertThat(result).isEqualTo(deck)
    }

    @Test
    fun `getDeckById returns null when deck not found`() = runTest {
        coEvery { deckDao.getDeckById(1) } returns null
        
        val result = deckRepository.getDeckById(1)
        
        assertThat(result).isNull()
    }

    @Test
    fun `insertDeck returns deck id`() = runTest {
        val deck = Deck(id = 1, name = "English")
        val expectedId = 1L
        
        coEvery { deckDao.insert(deck) } returns expectedId
        
        val result = deckRepository.insertDeck(deck)
        
        assertThat(result).isEqualTo(expectedId)
        coVerify { deckDao.insert(deck) }
    }

    @Test
    fun `updateDeck calls dao update`() = runTest {
        val deck = Deck(id = 1, name = "English")
        
        deckRepository.updateDeck(deck)
        
        coVerify { deckDao.update(deck) }
    }

    @Test
    fun `deleteDeck calls dao delete`() = runTest {
        val deck = Deck(id = 1, name = "English")
        
        deckRepository.deleteDeck(deck)
        
        coVerify { deckDao.delete(deck) }
    }

    @Test
    fun `getCardCount returns correct count`() = runTest {
        val deckId = 1L
        val expectedCount = 5
        
        coEvery { deckDao.getCardCount(deckId) } returns expectedCount
        
        val result = deckRepository.getCardCount(deckId)
        
        assertThat(result).isEqualTo(expectedCount)
    }

    @Test
    fun `getKnownCardCount returns correct count`() = runTest {
        val deckId = 1L
        val expectedCount = 3
        
        coEvery { deckDao.getKnownCardCount(deckId) } returns expectedCount
        
        val result = deckRepository.getKnownCardCount(deckId)
        
        assertThat(result).isEqualTo(expectedCount)
    }
} 