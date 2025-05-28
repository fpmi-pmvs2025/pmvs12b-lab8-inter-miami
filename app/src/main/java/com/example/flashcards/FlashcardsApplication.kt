package com.example.flashcards

import android.app.Application
import com.example.flashcards.di.AppContainer

class FlashcardsApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
} 