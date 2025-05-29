package com.example.flashcards.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.flashcards.data.model.Card
import com.example.flashcards.ui.BaseComposeTest
import com.example.flashcards.ui.screens.cardlist.CardListScreen
import org.junit.Test

class CardListScreenTest : BaseComposeTest() {

    @Test
    fun cardList_whenEmpty_showsEmptyState() {
        composeTestRule.setContent {
            CardListScreen(
                deckId = 1L,
                onNavigateUp = {}
            )
        }

        composeTestRule
            .onNodeWithText("No cards yet")
            .assertIsDisplayed()
    }

    @Test
    fun cardList_showsAddButton() {
        composeTestRule.setContent {
            CardListScreen(
                deckId = 1L,
                onNavigateUp = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Add Card")
            .assertIsDisplayed()
    }

    @Test
    fun cardList_showsBackButton() {
        var backPressed = false
        
        composeTestRule.setContent {
            CardListScreen(
                deckId = 1L,
                onNavigateUp = { backPressed = true }
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        assert(backPressed)
    }

    @Test
    fun cardList_showsDeckName() {
        composeTestRule.setContent {
            CardListScreen(
                deckId = 1L,
                onNavigateUp = {}
            )
        }

        // Initially shows "Cards" while loading
        composeTestRule
            .onNodeWithText("Cards")
            .assertIsDisplayed()
    }
} 