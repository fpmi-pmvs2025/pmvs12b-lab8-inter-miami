package com.example.flashcards.ui.screens.cardlist

import android.media.MediaPlayer
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcards.FlashcardsApplication
import com.example.flashcards.data.model.Card
import com.example.flashcards.data.model.DictionaryResult
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardListScreen(
    deckId: Long,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CardListViewModel = viewModel(
        factory = CardListViewModel.Factory(
            deckId = deckId,
            cardRepository = (LocalContext.current.applicationContext as FlashcardsApplication)
                .container.cardRepository,
            deckRepository = (LocalContext.current.applicationContext as FlashcardsApplication)
                .container.deckRepository,
            dictionaryRepository = (LocalContext.current.applicationContext as FlashcardsApplication)
                .container.dictionaryRepository
        )
    )
) {
    val state by viewModel.state.collectAsState()
    var showAddCardDialog by remember { mutableStateOf(false) }
    var cardToEdit by remember { mutableStateOf<Card?>(null) }

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
                        text = state.deck?.name ?: "Cards",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(0xFF222222)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = { showAddCardDialog = true },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(fabShape)
                            .background(Color(0xFF3b5bfd))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Card", tint = Color.White)
                    }
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
            } else if (state.cards.isEmpty()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF3b5bfd)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No cards yet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF222222)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add some by tapping the + button!",
                        fontSize = 16.sp,
                        color = Color(0xFF8A8A8A)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.cards) { card ->
                        CardItem(
                            card = card,
                            onEditClick = { cardToEdit = card },
                            onDeleteClick = { viewModel.deleteCard(card) }
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

        if (showAddCardDialog) {
            AddEditCardDialog(
                onDismiss = { showAddCardDialog = false },
                onConfirm = { word, translation ->
                    viewModel.addCard(word, translation)
                    showAddCardDialog = false
                },
                onLookupWord = { word ->
                    viewModel.lookupWord(word)
                },
                dictionaryResult = state.dictionaryResult,
                isLookingUp = false,
                onClearDictionaryResult = {
                    viewModel.clearDictionaryResult()
                },
                onUseDefinition = { definition ->
                    // Implementation needed
                }
            )
        }

        cardToEdit?.let { card ->
            AddEditCardDialog(
                onDismiss = { cardToEdit = null },
                onConfirm = { word, translation ->
                    viewModel.updateCard(card, word, translation)
                    cardToEdit = null
                },
                initialWord = card.word,
                initialTranslation = card.translation,
                onLookupWord = { word ->
                    viewModel.lookupWord(word)
                },
                dictionaryResult = state.dictionaryResult,
                isLookingUp = false,
                onClearDictionaryResult = {
                    viewModel.clearDictionaryResult()
                },
                onUseDefinition = { definition ->
                    // Implementation needed
                }
            )
        }
    }
}

@Composable
fun CardItem(
    card: Card,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.word,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF222222)
                )
                Text(
                    text = card.translation,
                    color = Color(0xFF8A8A8A),
                    fontSize = 14.sp
                )
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
fun AddEditCardDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    initialWord: String = "",
    initialTranslation: String = "",
    onLookupWord: (String) -> Unit,
    dictionaryResult: DictionaryResult?,
    isLookingUp: Boolean,
    onClearDictionaryResult: () -> Unit,
    onUseDefinition: (String) -> Unit
) {
    var word by remember { mutableStateOf(initialWord) }
    var translation by remember { mutableStateOf(initialTranslation) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                if (initialWord.isEmpty()) "Add Card" else "Edit Card",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            ) 
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = word,
                    onValueChange = { word = it },
                    label = { Text("Word") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    trailingIcon = {
                        if (word.isNotBlank()) {
                            IconButton(
                                onClick = { onLookupWord(word) },
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                if (isLookingUp) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Icon(Icons.Default.Search, "Lookup word")
                                }
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = translation,
                    onValueChange = { translation = it },
                    label = { Text("Translation") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                if (dictionaryResult != null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = dictionaryResult.phonetic ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (dictionaryResult.audioUrl != null) {
                                    IconButton(
                                        onClick = {
                                            mediaPlayer?.release()
                                            mediaPlayer = MediaPlayer().apply {
                                                setDataSource(dictionaryResult.audioUrl)
                                                prepare()
                                                start()
                                            }
                                        },
                                        colors = IconButtonDefaults.iconButtonColors(
                                            contentColor = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Icon(Icons.Default.VolumeUp, "Play pronunciation")
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = dictionaryResult.definition,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (dictionaryResult.example != null) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Example: ${dictionaryResult.example}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (dictionaryResult.synonyms.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Synonyms: ${dictionaryResult.synonyms.joinToString(", ")}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (dictionaryResult.antonyms.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Antonyms: ${dictionaryResult.antonyms.joinToString(", ")}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (dictionaryResult.origin != null) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Origin: ${dictionaryResult.origin}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { 
                                    translation = dictionaryResult.definition
                                    onUseDefinition(dictionaryResult.definition)
                                },
                                modifier = Modifier.align(Alignment.End),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text("Use Definition")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(word, translation)
                    onDismiss()
                },
                enabled = word.isNotBlank() && translation.isNotBlank(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }
} 