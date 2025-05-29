package com.example.flashcards.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.flashcards.data.model.Card
import com.example.flashcards.ui.BaseComposeTest
import com.example.flashcards.ui.screens.study.StudyScreen
import org.junit.Test

class StudyScreenTest : BaseComposeTest() {

    @Test
    fun studyScreen_showsBackButton() {
        var backPressed = false

        composeTestRule.setContent {
            StudyScreen(
                deckId = 1L,
                onNavigateUp = { backPressed = true }
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        assert(backPressed)
    }
} 