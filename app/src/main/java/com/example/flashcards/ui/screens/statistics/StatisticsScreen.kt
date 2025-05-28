package com.example.flashcards.ui.screens.statistics

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
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    deckId: Long,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = viewModel(
        factory = StatisticsViewModel.Factory(
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
                        text = state.deck?.name ?: "Statistics",
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
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatisticsCard(
                        title = "Progress",
                        content = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                LinearProgressIndicator(
                                    progress = state.averageAccuracy,
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color(0xFF3b5bfd),
                                    trackColor = Color(0xFFE0E0E0)
                                )
                                Text(
                                    text = "${(state.averageAccuracy * 100).toInt()}% learned",
                                    fontSize = 16.sp,
                                    color = Color(0xFF222222)
                                )
                            }
                        }
                    )

                    StatisticsCard(
                        title = "Cards Overview",
                        content = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                StatisticRow("Total Cards", state.totalCards.toString())
                                StatisticRow("Known Cards", state.knownCards.toString())
                                StatisticRow("Unknown Cards", state.unknownCards.toString())
                            }
                        }
                    )

                    StatisticsCard(
                        title = "Last Review",
                        content = {
                            Text(
                                text = state.lastReviewDate?.let {
                                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                        .format(Date(it))
                                } ?: "Never",
                                fontSize = 16.sp,
                                color = Color(0xFF222222)
                            )
                        }
                    )

                    StatisticsCard(
                        title = "Review Distribution",
                        content = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TimeRange.values().forEach { range ->
                                    val count = state.cardsByLastReview[range] ?: 0
                                    StatisticRow(
                                        label = when (range) {
                                            TimeRange.TODAY -> "Today"
                                            TimeRange.THIS_WEEK -> "This Week"
                                            TimeRange.THIS_MONTH -> "This Month"
                                            TimeRange.OLDER -> "Older"
                                        },
                                        value = count.toString()
                                    )
                                }
                            }
                        }
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
fun StatisticsCard(
    title: String,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF222222)
            )
            content()
        }
    }
}

@Composable
fun StatisticRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF8A8A8A)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color(0xFF222222),
            fontWeight = FontWeight.Medium
        )
    }
} 