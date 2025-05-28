package com.example.flashcards.di

import android.content.Context
import com.example.flashcards.data.FlashcardsDatabase
import com.example.flashcards.data.repository.CardRepository
import com.example.flashcards.data.repository.DeckRepository
import com.example.flashcards.data.repository.DictionaryRepository

class AppContainer(context: Context) {
    private val database = FlashcardsDatabase.getDatabase(context)
    
    val deckRepository = DeckRepository(database.deckDao())
    val cardRepository = CardRepository(database.cardDao())
    val dictionaryRepository = DictionaryRepository()
} 