package com.example.flashcards.ui.screens.decklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.flashcards.data.model.Deck
import com.example.flashcards.data.repository.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class DeckListState(
    val decks: List<DeckWithStats> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

data class DeckWithStats(
    val deck: Deck,
    val totalCards: Int,
    val knownCards: Int
)

class DeckListViewModel(
    private val deckRepository: DeckRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DeckListState())
    val state: StateFlow<DeckListState> = deckRepository.getAllDecks()
        .map { decks ->
            DeckListState(
                decks = decks.map { deck ->
                    DeckWithStats(
                        deck = deck,
                        totalCards = deckRepository.getCardCount(deck.id),
                        knownCards = deckRepository.getKnownCardCount(deck.id)
                    )
                },
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DeckListState()
        )

    fun createDeck(name: String) {
        viewModelScope.launch {
            try {
                val deck = Deck(name = name.trim())
                deckRepository.insertDeck(deck)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun updateDeck(deck: Deck, newName: String) {
        viewModelScope.launch {
            try {
                deckRepository.updateDeck(deck.copy(name = newName.trim()))
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun deleteDeck(deck: Deck) {
        viewModelScope.launch {
            try {
                deckRepository.deleteDeck(deck)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    class Factory(private val deckRepository: DeckRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DeckListViewModel::class.java)) {
                return DeckListViewModel(deckRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 