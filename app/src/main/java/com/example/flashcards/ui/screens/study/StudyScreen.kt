package com.example.flashcards.ui.screens.study

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(
    deckId: Long,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StudyViewModel = viewModel(
        factory = StudyViewModel.Factory(
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
                        text = state.deck?.name ?: "Study Mode",
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
                StudyCompleteScreen(
                    totalCards = state.studiedCards,
                    knownCards = state.knownCards,
                    onFinish = onNavigateUp
                )
            } else {
                state.currentCard?.let { card ->
                    StudyCardContent(
                        word = card.word,
                        translation = card.translation,
                        isShowingTranslation = state.isShowingTranslation,
                        onCardClick = { viewModel.showTranslation() },
                        onKnowClick = { viewModel.markCard(true) },
                        onDontKnowClick = { viewModel.markCard(false) }
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
fun StudyCardContent(
    word: String,
    translation: String,
    isShowingTranslation: Boolean,
    onCardClick: () -> Unit,
    onKnowClick: () -> Unit,
    onDontKnowClick: () -> Unit,
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
                .clickable(onClick = onCardClick)
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = word,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF222222),
                        textAlign = TextAlign.Center
                    )
                    AnimatedVisibility(
                        visible = isShowingTranslation,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            Divider(
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .width(100.dp),
                                color = Color(0xFFE0E0E0)
                            )
                            Text(
                                text = translation,
                                fontSize = 24.sp,
                                color = Color(0xFF8A8A8A),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = isShowingTranslation,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onDontKnowClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE0E0E0),
                        contentColor = Color(0xFF616161)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.width(140.dp)
                ) {
                    Text("Don't Know", fontSize = 16.sp)
                }
                Button(
                    onClick = onKnowClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3b5bfd),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.width(140.dp)
                ) {
                    Text("Know", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun StudyCompleteScreen(
    totalCards: Int,
    knownCards: Int,
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
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color(0xFF3b5bfd)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Study Complete!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF222222)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You've studied $totalCards cards",
            fontSize = 18.sp,
            color = Color(0xFF8A8A8A)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Known: $knownCards (${(knownCards * 100f / totalCards).toInt()}%)",
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