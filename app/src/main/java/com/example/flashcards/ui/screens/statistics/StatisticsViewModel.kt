package com.example.flashcards.ui.screens.statistics

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
import java.util.concurrent.TimeUnit

data class StatisticsState(
    val deck: Deck? = null,
    val totalCards: Int = 0,
    val knownCards: Int = 0,
    val unknownCards: Int = 0,
    val lastReviewDate: Long? = null,
    val averageAccuracy: Float = 0f,
    val cardsByLastReview: Map<TimeRange, Int> = emptyMap(),
    val isLoading: Boolean = true,
    val error: String? = null
)

enum class TimeRange {
    TODAY, THIS_WEEK, THIS_MONTH, OLDER
}

class StatisticsViewModel(
    private val deckId: Long,
    private val cardRepository: CardRepository,
    private val deckRepository: DeckRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StatisticsState())
    val state: StateFlow<StatisticsState> = _state

    init {
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            try {
                val deck = deckRepository.getDeckById(deckId)
                val cards = cardRepository.getCardsByDeckId(deckId).first()

                val now = System.currentTimeMillis()
                val today = now - TimeUnit.DAYS.toMillis(1)
                val thisWeek = now - TimeUnit.DAYS.toMillis(7)
                val thisMonth = now - TimeUnit.DAYS.toMillis(30)

                val cardsByLastReview = cards.groupBy { card ->
                    when (card.lastReviewedAt) {
                        null -> TimeRange.OLDER
                        else -> when {
                            card.lastReviewedAt >= today -> TimeRange.TODAY
                            card.lastReviewedAt >= thisWeek -> TimeRange.THIS_WEEK
                            card.lastReviewedAt >= thisMonth -> TimeRange.THIS_MONTH
                            else -> TimeRange.OLDER
                        }
                    }
                }.mapValues { (_, cards) -> cards.size }

                val knownCards = cards.count { card -> card.isKnown }
                val lastReviewDate = cards.maxOfOrNull { card -> card.lastReviewedAt ?: 0L }

                _state.update { currentState ->
                    currentState.copy(
                        deck = deck,
                        totalCards = cards.size,
                        knownCards = knownCards,
                        unknownCards = cards.size - knownCards,
                        lastReviewDate = lastReviewDate,
                        averageAccuracy = if (cards.isNotEmpty()) {
                            knownCards.toFloat() / cards.size
                        } else 0f,
                        cardsByLastReview = cardsByLastReview,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
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
            if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
                return StatisticsViewModel(deckId, cardRepository, deckRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 