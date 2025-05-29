package com.example.flashcards.data.model

import com.example.flashcards.data.repository.CardRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class CardTest {

    @Before
    fun setup() {}
    
    @Test
    fun `card creation with default values`() {
        val card = Card(
            id = 1,
            deckId = 1,
            word = "Hello",
            translation = "Привет"
        )
        
        assertThat(card.id).isEqualTo(1)
        assertThat(card.deckId).isEqualTo(1)
        assertThat(card.word).isEqualTo("Hello")
        assertThat(card.translation).isEqualTo("Привет")
        assertThat(card.isKnown).isFalse()
        assertThat(card.lastReviewedAt).isNull()
        assertThat(card.createdAt).isNotEqualTo(0)
        assertThat(card.updatedAt).isNotEqualTo(0)
    }

    @Test
    fun `card creation with all values`() {
        val currentTime = System.currentTimeMillis()
        val card = Card(
            id = 1,
            deckId = 1,
            word = "Hello",
            translation = "Привет",
            isKnown = true,
            lastReviewedAt = currentTime,
            createdAt = currentTime,
            updatedAt = currentTime
        )
        
        assertThat(card.id).isEqualTo(1)
        assertThat(card.deckId).isEqualTo(1)
        assertThat(card.word).isEqualTo("Hello")
        assertThat(card.translation).isEqualTo("Привет")
        assertThat(card.isKnown).isTrue()
        assertThat(card.lastReviewedAt).isEqualTo(currentTime)
        assertThat(card.createdAt).isEqualTo(currentTime)
        assertThat(card.updatedAt).isEqualTo(currentTime)
    }

    @Test
    fun `card equality test`() {
        val card1 = Card(
            id = 1,
            deckId = 1,
            word = "Hello",
            translation = "Привет"
        )
        
        val card2 = Card(
            id = 1,
            deckId = 1,
            word = "Hello",
            translation = "Привет"
        )
        
        val card3 = Card(
            id = 2,
            deckId = 1,
            word = "Hello",
            translation = "Привет"
        )
        
        assertThat(card1).isEqualTo(card2)
        assertThat(card1).isNotEqualTo(card3)
    }
} 