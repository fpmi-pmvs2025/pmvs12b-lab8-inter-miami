package com.example.flashcards.ui.screens.quiz

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcards.FlashcardsApplication
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    deckId: Long,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QuizViewModel = viewModel(
        factory = QuizViewModel.Factory(
            deckId = deckId,
            cardRepository = (LocalContext.current.applicationContext as FlashcardsApplication)
                .container.cardRepository,
            deckRepository = (LocalContext.current.applicationContext as FlashcardsApplication)
                .container.deckRepository
        )
    )
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            Surface(shadowElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF3b5bfd),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = state.deck?.name ?: "Quiz Mode",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(0xFF222222)
                    )
                }
            }
        },
        containerColor = Color(0xFFF8F9FB)
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF3b5bfd)
                )
            } else if (state.isComplete) {
                QuizCompleteScreen(
                    totalQuestions = state.answeredQuestions,
                    correctAnswers = state.correctAnswers,
                    onFinish = onNavigateUp
                )
            } else {
                state.currentQuestion?.let { question ->
                    QuizQuestionContent(
                        word = question.card.word,
                        options = question.options,
                        selectedAnswer = state.selectedAnswer,
                        hasAnswered = state.hasAnswered,
                        onAnswerSelected = { viewModel.selectAnswer(it) },
                        onNextQuestion = { viewModel.nextQuestion() }
                    )
                }
            }

            state.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(error)
                }
            }
        }
    }
}

@Composable
fun QuizQuestionContent(
    word: String,
    options: List<String>,
    selectedAnswer: String?,
    hasAnswered: Boolean,
    onAnswerSelected: (String) -> Unit,
    onNextQuestion: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = word,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEach { option ->
                val isCorrect = option == options.find { it == selectedAnswer }
                val isSelected = option == selectedAnswer

                Button(
                    onClick = { onAnswerSelected(option) },
                    enabled = !hasAnswered,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when {
                            !hasAnswered -> Color(0xFFE0E0E0)
                            isCorrect -> Color(0xFF43a047)
                            isSelected -> Color(0xFFe53935)
                            else -> Color(0xFFE0E0E0)
                        },
                        contentColor = when {
                            !hasAnswered -> Color(0xFF616161)
                            isCorrect -> Color.White
                            isSelected -> Color.White
                            else -> Color(0xFF616161)
                        }
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = option,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = hasAnswered,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Button(
                onClick = onNextQuestion,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3b5bfd),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Next Question", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun QuizCompleteScreen(
    totalQuestions: Int,
    correctAnswers: Int,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color(0xFF3b5bfd)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Quiz Complete!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF222222)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Score: $correctAnswers/$totalQuestions",
            fontSize = 18.sp,
            color = Color(0xFF8A8A8A)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Accuracy: ${(correctAnswers * 100f / totalQuestions).toInt()}%",
            fontSize = 16.sp,
            color = Color(0xFF8A8A8A)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onFinish,
            modifier = Modifier.width(200.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3b5bfd),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Finish", fontSize = 16.sp)
        }
    }
} 