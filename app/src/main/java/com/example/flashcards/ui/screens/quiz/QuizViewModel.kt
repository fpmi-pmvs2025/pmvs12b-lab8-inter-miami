package com.example.flashcards.ui.screens.quiz

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

data class QuizState(
    val deck: Deck? = null,
    val currentQuestion: QuizQuestion? = null,
    val answeredQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val isLoading: Boolean = true,
    val isComplete: Boolean = false,
    val selectedAnswer: String? = null,
    val hasAnswered: Boolean = false,
    val error: String? = null
)

data class QuizQuestion(
    val card: Card,
    val options: List<String>
)

class QuizViewModel(
    private val deckId: Long,
    private val cardRepository: CardRepository,
    private val deckRepository: DeckRepository
) : ViewModel() {

    private val _state = MutableStateFlow(QuizState())
    val state: StateFlow<QuizState> = _state

    private var questions: List<QuizQuestion> = emptyList()
    private var currentQuestionIndex = 0

    init {
        loadQuiz()
    }

    private fun loadQuiz() {
        viewModelScope.launch {
            try {
                val deck = deckRepository.getDeckById(deckId)
                val cards = cardRepository.getCardsByDeckId(deckId).first().shuffled()
                
                questions = cards.map { card ->
                    val incorrectOptions = cardRepository.getRandomCardsExcluding(
                        deckId = deckId,
                        excludedCardId = card.id,
                        limit = 3
                    ).map { it.translation }

                    val options = (incorrectOptions + card.translation).shuffled()
                    QuizQuestion(card, options)
                }

                if (questions.isNotEmpty()) {
                    _state.update { currentState ->
                        currentState.copy(
                            deck = deck,
                            currentQuestion = questions.first(),
                            isLoading = false
                        )
                    }
                } else {
                    _state.update { it.copy(error = "No cards available for quiz", isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun selectAnswer(answer: String) {
        if (_state.value.hasAnswered) return

        _state.update { currentState ->
            currentState.copy(
                selectedAnswer = answer,
                hasAnswered = true,
                correctAnswers = currentState.correctAnswers +
                    if (answer == currentState.currentQuestion?.card?.translation) 1 else 0
            )
        }
    }

    fun nextQuestion() {
        currentQuestionIndex++
        if (currentQuestionIndex < questions.size) {
            _state.update { currentState ->
                currentState.copy(
                    currentQuestion = questions[currentQuestionIndex],
                    selectedAnswer = null,
                    hasAnswered = false,
                    answeredQuestions = currentState.answeredQuestions + 1
                )
            }
        } else {
            _state.update { currentState ->
                currentState.copy(
                    isComplete = true,
                    answeredQuestions = currentState.answeredQuestions + 1
                )
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
            if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
                return QuizViewModel(deckId, cardRepository, deckRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 