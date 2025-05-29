package com.example.flashcards.data.model

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class DeckTest {

    @Before
    fun setup() {}
    
    @Test
    fun `deck creation with default values`() {
        val deck = Deck(
            id = 1,
            name = "English Words"
        )
        
        assertThat(deck.id).isEqualTo(1)
        assertThat(deck.name).isEqualTo("English Words")
        assertThat(deck.createdAt).isNotEqualTo(0)
        assertThat(deck.updatedAt).isNotEqualTo(0)
    }

    @Test
    fun `deck creation with all values`() {
        val currentTime = System.currentTimeMillis()
        val deck = Deck(
            id = 1,
            name = "English Words",
            createdAt = currentTime,
            updatedAt = currentTime
        )
        
        assertThat(deck.id).isEqualTo(1)
        assertThat(deck.name).isEqualTo("English Words")
        assertThat(deck.createdAt).isEqualTo(currentTime)
        assertThat(deck.updatedAt).isEqualTo(currentTime)
    }

    @Test
    fun `deck equality test`() {
        val deck1 = Deck(
            id = 1,
            name = "English Words"
        )
        
        val deck2 = Deck(
            id = 1,
            name = "English Words"
        )
        
        val deck3 = Deck(
            id = 2,
            name = "English Words"
        )
        
        assertThat(deck1).isEqualTo(deck2)
        assertThat(deck1).isNotEqualTo(deck3)
    }
} 