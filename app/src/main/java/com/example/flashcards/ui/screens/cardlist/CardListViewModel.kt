package com.example.flashcards.ui.screens.cardlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.flashcards.data.model.Card
import com.example.flashcards.data.model.Deck
import com.example.flashcards.data.model.DictionaryResult
import com.example.flashcards.data.repository.CardRepository
import com.example.flashcards.data.repository.DeckRepository
import com.example.flashcards.data.repository.DictionaryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CardListState(
    val deck: Deck? = null,
    val cards: List<Card> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val dictionaryResult: DictionaryResult? = null
)

class CardListViewModel(
    private val deckId: Long,
    private val cardRepository: CardRepository,
    private val deckRepository: DeckRepository,
    private val dictionaryRepository: DictionaryRepository
) : ViewModel() {

    private val TAG = "CardListViewModel"

    private val _state = MutableStateFlow(CardListState())
    val state: StateFlow<CardListState> = combine(
        cardRepository.getCardsByDeckId(deckId),
        _state
    ) { cards, currentState ->
        currentState.copy(
            cards = cards,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CardListState()
    )

    init {
        loadDeck()
    }

    private fun loadDeck() {
        viewModelScope.launch {
            try {
                val deck = deckRepository.getDeckById(deckId)
                _state.value = _state.value.copy(
                    deck = deck,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    fun addCard(word: String, translation: String) {
        viewModelScope.launch {
            try {
                val card = Card(
                    deckId = deckId,
                    word = word.trim(),
                    translation = translation.trim()
                )
                cardRepository.insertCard(card)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun updateCard(card: Card, word: String, translation: String) {
        viewModelScope.launch {
            try {
                cardRepository.updateCard(
                    card.copy(
                        word = word.trim(),
                        translation = translation.trim()
                    )
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun deleteCard(card: Card) {
        viewModelScope.launch {
            try {
                cardRepository.deleteCard(card)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun lookupWord(word: String) {
        Log.d(TAG, "Starting lookup for word: $word")
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                Log.d(TAG, "Calling dictionary repository for word: $word")
                dictionaryRepository.getDefinition(word).fold(
                    onSuccess = { response ->
                        Log.d(TAG, "Successfully received response from API")
                        val definition = dictionaryRepository.getFirstDefinition(response)
                        val example = dictionaryRepository.getExample(response)
                        val synonyms = dictionaryRepository.getSynonyms(response)
                        val antonyms = dictionaryRepository.getAntonyms(response)
                        val origin = dictionaryRepository.getOrigin(response)
                        val phonetic = dictionaryRepository.getPhonetic(response)
                        val audioUrl = dictionaryRepository.getAudioUrl(response)

                        if (definition != null) {
                            Log.d(TAG, "Found definition: $definition")
                            val newResult = DictionaryResult(
                                definition = definition,
                                example = example,
                                synonyms = synonyms,
                                antonyms = antonyms,
                                origin = origin,
                                phonetic = phonetic,
                                audioUrl = audioUrl
                            )
                            Log.d(TAG, "Creating new dictionary result: $newResult")
                            _state.update { currentState ->
                                currentState.copy(
                                    dictionaryResult = newResult,
                                    isLoading = false
                                )
                            }
                            Log.d(TAG, "State updated with new dictionary result")
                        } else {
                            Log.e(TAG, "No definition found in response")
                            _state.update { currentState ->
                                currentState.copy(
                                    error = "No definition found",
                                    isLoading = false
                                )
                            }
                        }
                    },
                    onFailure = { error ->
                        Log.e(TAG, "Error fetching definition", error)
                        _state.update { currentState ->
                            currentState.copy(
                                error = error.message ?: "Failed to lookup word",
                                isLoading = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Exception during lookup", e)
                _state.update { currentState ->
                    currentState.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun clearDictionaryResult() {
        _state.update { currentState ->
            currentState.copy(dictionaryResult = null)
        }
    }

    fun resetError() {
        _state.update { currentState ->
            currentState.copy(error = null)
        }
    }

    class Factory(
        private val deckId: Long,
        private val cardRepository: CardRepository,
        private val deckRepository: DeckRepository,
        private val dictionaryRepository: DictionaryRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CardListViewModel::class.java)) {
                return CardListViewModel(deckId, cardRepository, deckRepository, dictionaryRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 