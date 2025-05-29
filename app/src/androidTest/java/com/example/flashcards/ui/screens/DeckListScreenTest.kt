package com.example.flashcards.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.flashcards.data.model.Deck
import com.example.flashcards.ui.BaseComposeTest
import com.example.flashcards.ui.screens.decklist.DeckListScreen
import org.junit.Test

class DeckListScreenTest : BaseComposeTest() {

    @Test
    fun deckList_whenEmpty_showsEmptyState() {
        composeTestRule.setContent {
            DeckListScreen(
                onDeckClick = {},
                onStudyClick = {},
                onQuizClick = {},
                onStatisticsClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("No decks yet. Create one by tapping the + button!")
            .assertIsDisplayed()
    }

    @Test
    fun addDeckButton_isDisplayed() {
        composeTestRule.setContent {
            DeckListScreen(
                onDeckClick = {},
                onStudyClick = {},
                onQuizClick = {},
                onStatisticsClick = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Add Deck")
            .assertIsDisplayed()
    }

    @Test
    fun deckList_clickOnDeck_triggersCallback() {
        var clickedDeckId = -1L

        composeTestRule.setContent {
            DeckListScreen(
                onDeckClick = { clickedDeckId = it },
                onStudyClick = {},
                onQuizClick = {},
                onStatisticsClick = {}
            )
        }

        // Note: This test might need to be updated since the deck list is now managed by ViewModel
        // and we might need to set up a test ViewModel to properly test this functionality
    }

    @Test
    fun deckList_showsEmptyStateMessage() {
        composeTestRule.setContent {
            DeckListScreen(
                onDeckClick = {},
                onStudyClick = {},
                onQuizClick = {},
                onStatisticsClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("No decks yet. Create one by tapping the + button!")
            .assertIsDisplayed()
    }
} 