package com.example.flashcards.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.flashcards.ui.screens.cardlist.CardListScreen
import com.example.flashcards.ui.screens.decklist.DeckListScreen
import com.example.flashcards.ui.screens.quiz.QuizScreen
import com.example.flashcards.ui.screens.statistics.StatisticsScreen
import com.example.flashcards.ui.screens.study.StudyScreen

@Composable
fun FlashcardsNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.DeckList.route
    ) {
        composable(Screen.DeckList.route) {
            DeckListScreen(
                onDeckClick = { deckId ->
                    navController.navigate(Screen.CardList.createRoute(deckId))
                },
                onStudyClick = { deckId ->
                    navController.navigate(Screen.StudyMode.createRoute(deckId))
                },
                onQuizClick = { deckId ->
                    navController.navigate(Screen.QuizMode.createRoute(deckId))
                },
                onStatisticsClick = { deckId ->
                    navController.navigate(Screen.Statistics.createRoute(deckId))
                }
            )
        }

        composable(
            route = Screen.CardList.route,
            arguments = listOf(navArgument("deckId") { type = NavType.LongType })
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getLong("deckId") ?: return@composable
            CardListScreen(
                deckId = deckId,
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = Screen.StudyMode.route,
            arguments = listOf(navArgument("deckId") { type = NavType.LongType })
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getLong("deckId") ?: return@composable
            StudyScreen(
                deckId = deckId,
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = Screen.QuizMode.route,
            arguments = listOf(navArgument("deckId") { type = NavType.LongType })
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getLong("deckId") ?: return@composable
            QuizScreen(
                deckId = deckId,
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = Screen.Statistics.route,
            arguments = listOf(navArgument("deckId") { type = NavType.LongType })
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getLong("deckId") ?: return@composable
            StatisticsScreen(
                deckId = deckId,
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
} 