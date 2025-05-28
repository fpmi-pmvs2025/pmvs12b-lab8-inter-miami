package com.example.flashcards.ui.screens.decklist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random
import com.example.flashcards.FlashcardsApplication
import com.example.flashcards.R
import com.example.flashcards.data.model.Deck
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckListScreen(
    onDeckClick: (Long) -> Unit,
    onStudyClick: (Long) -> Unit,
    onQuizClick: (Long) -> Unit,
    onStatisticsClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DeckListViewModel = viewModel(
        factory = DeckListViewModel.Factory(
            (LocalContext.current.applicationContext as FlashcardsApplication).container.deckRepository
        )
    )
) {
    val state by viewModel.state.collectAsState()
    var showAddDeckDialog by remember { mutableStateOf(false) }
    var deckToEdit by remember { mutableStateOf<Deck?>(null) }

    val fabShape = CircleShape

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
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Color(0xFF3b5bfd),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "My Word Sets",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(0xFF222222)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = { showAddDeckDialog = true },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(fabShape)
                            .background(Color(0xFF3b5bfd))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Deck", tint = Color.White)
                    }
                }
            }
        },
        containerColor = Color(0xFFF8F9FB),
        floatingActionButton = {}, // FAB уже реализован в topBar
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.decks.isEmpty()) {
                Text(
                    text = "No decks yet. Create one by tapping the + button!",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.decks) { deckWithStats ->
                        DeckCard(
                            deckWithStats = deckWithStats,
                            onDeckClick = onDeckClick,
                            onStudyClick = onStudyClick,
                            onQuizClick = onQuizClick,
                            onStatisticsClick = onStatisticsClick,
                            onEditClick = { deckToEdit = deckWithStats.deck },
                            onDeleteClick = { viewModel.deleteDeck(deckWithStats.deck) }
                        )
                    }
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

        if (showAddDeckDialog) {
            AddEditDeckDialog(
                onDismiss = { showAddDeckDialog = false },
                onConfirm = { name ->
                    viewModel.createDeck(name)
                    showAddDeckDialog = false
                }
            )
        }

        deckToEdit?.let { deck ->
            AddEditDeckDialog(
                initialName = deck.name,
                onDismiss = { deckToEdit = null },
                onConfirm = { name ->
                    viewModel.updateDeck(deck, name)
                    deckToEdit = null
                }
            )
        }
    }
}

@Composable
fun DeckCard(
    deckWithStats: DeckWithStats,
    onDeckClick: (Long) -> Unit,
    onStudyClick: (Long) -> Unit,
    onQuizClick: (Long) -> Unit,
    onStatisticsClick: (Long) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Случайный цвет для папки (на основе id)
    val folderColors = listOf(
        Color(0xFF3b5bfd), // синий
        Color(0xFFff9800), // оранжевый
        Color(0xFF43a047), // зелёный
        Color(0xFFe91e63), // розовый
        Color(0xFF9c27b0)  // фиолетовый
    )
    val folderColor = remember(deckWithStats.deck.id) {
        folderColors[(deckWithStats.deck.id % folderColors.size).toInt()]
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .clickable { onDeckClick(deckWithStats.deck.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = folderColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    deckWithStats.deck.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF222222)
                )
                Text(
                    "${deckWithStats.totalCards} words",
                    color = Color(0xFF8A8A8A),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row {
                    DeckActionButton(
                        text = "Study",
                        icon = Icons.Default.MenuBook,
                        bgColor = Color(0xFFe3e8fd),
                        contentColor = Color(0xFF3b5bfd),
                        onClick = { onStudyClick(deckWithStats.deck.id) }
                    )
                    Spacer(Modifier.width(8.dp))
                    DeckActionButton(
                        text = "Quiz",
                        icon = Icons.Default.Lightbulb,
                        bgColor = Color(0xFFfff3e0),
                        contentColor = Color(0xFFff9800),
                        onClick = { onQuizClick(deckWithStats.deck.id) }
                    )
                    Spacer(Modifier.width(8.dp))
                    DeckActionButton(
                        text = "Stats",
                        icon = Icons.Default.BarChart,
                        bgColor = Color(0xFF9E9E9E),
                        contentColor = Color.White,
                        onClick = { onStatisticsClick(deckWithStats.deck.id) }
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFFbdbdbd))
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFbdbdbd))
            }
        }
    }
}

@Composable
fun DeckActionButton(
    text: String,
    icon: ImageVector,
    bgColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = bgColor, contentColor = contentColor),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        modifier = Modifier.height(28.dp)
    ) {
        Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, color = contentColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun AddEditDeckDialog(
    initialName: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialName.isEmpty()) "Add Deck" else "Edit Deck") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Deck Name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank()
            ) {
                Text(if (initialName.isEmpty()) "Create" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 