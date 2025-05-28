package com.example.flashcards.navigation

sealed class Screen(val route: String) {
    object DeckList : Screen("deck_list")
    object CardList : Screen("card_list/{deckId}") {
        fun createRoute(deckId: Long) = "card_list/$deckId"
    }
    object StudyMode : Screen("study_mode/{deckId}") {
        fun createRoute(deckId: Long) = "study_mode/$deckId"
    }
    object QuizMode : Screen("quiz_mode/{deckId}") {
        fun createRoute(deckId: Long) = "quiz_mode/$deckId"
    }
    object Statistics : Screen("statistics/{deckId}") {
        fun createRoute(deckId: Long) = "statistics/$deckId"
    }
} 