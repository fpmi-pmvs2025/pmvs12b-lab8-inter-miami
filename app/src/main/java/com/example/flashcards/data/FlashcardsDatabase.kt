package com.example.flashcards.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flashcards.data.dao.CardDao
import com.example.flashcards.data.dao.DeckDao
import com.example.flashcards.data.model.Card
import com.example.flashcards.data.model.Deck

@Database(
    entities = [Deck::class, Card::class],
    version = 1,
    exportSchema = false
)
abstract class FlashcardsDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
    abstract fun cardDao(): CardDao

    companion object {
        @Volatile
        private var INSTANCE: FlashcardsDatabase? = null

        fun getDatabase(context: Context): FlashcardsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlashcardsDatabase::class.java,
                    "flashcards_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 