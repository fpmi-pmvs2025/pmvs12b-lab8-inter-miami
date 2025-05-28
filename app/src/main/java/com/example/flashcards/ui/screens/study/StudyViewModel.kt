package com.example.flashcards.ui.screens.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.flashcards.data.model.Card
import com.example.flashcards.data.model.Deck
import com.example.flashcards.data.repository.CardRepository
import com.example.flashcards.data.repository.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudyState(
    val deck: Deck? = null,
    val currentCard: Card? = null,
    val isShowingTranslation: Boolean = false,
    val cardsToStudy: List<Card> = emptyList(),
    val studiedCards: Int = 0,
    val knownCards: Int = 0,
    val isLoading: Boolean = true,
    val isComplete: Boolean = false,
    val error: String? = null
)

class StudyViewModel(
    private val deckId: Long,
    private val cardRepository: CardRepository,
    private val deckRepository: DeckRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StudyState())
    val state: StateFlow<StudyState> = _state

    init {
        loadDeckAndCards()
    }

    private fun loadDeckAndCards() {
        viewModelScope.launch {
            try {
                val deck = deckRepository.getDeckById(deckId)
                val cards = cardRepository.getCardsByDeckId(deckId).first()
                _state.update { currentState ->
                    currentState.copy(
                        deck = deck,
                        cardsToStudy = cards.shuffled(),
                        currentCard = cards.firstOrNull(),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun showTranslation() {
        _state.update { it.copy(isShowingTranslation = true) }
    }

    fun markCard(isKnown: Boolean) {
        viewModelScope.launch {
            val currentState = _state.value
            val currentCard = currentState.currentCard ?: return@launch

            try {
                cardRepository.updateCardKnownStatus(currentCard.id, isKnown)

                val remainingCards = currentState.cardsToStudy.drop(1)
                _state.update { state ->
                    state.copy(
                        cardsToStudy = remainingCards,
                        currentCard = remainingCards.firstOrNull(),
                        isShowingTranslation = false,
                        studiedCards = state.studiedCards + 1,
                        knownCards = state.knownCards + (if (isKnown) 1 else 0),
                        isComplete = remainingCards.isEmpty()
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun resetError() {
        _state.update { it.copy(error = null) }
    }

    class Factory(
        private val deckId: Long,
        private val cardRepository: CardRepository,
        private val deckRepository: DeckRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StudyViewModel::class.java)) {
                return StudyViewModel(deckId, cardRepository, deckRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 