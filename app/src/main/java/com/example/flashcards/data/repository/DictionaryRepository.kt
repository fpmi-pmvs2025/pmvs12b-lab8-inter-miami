package com.example.flashcards.data.repository

import android.util.Log
import com.example.flashcards.data.api.DictionaryApi
import com.example.flashcards.data.api.DictionaryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class DictionaryRepository {
    private val TAG = "DictionaryRepository"

    private val api = Retrofit.Builder()
        .baseUrl("https://api.dictionaryapi.dev/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(DictionaryApi::class.java)

    suspend fun getDefinition(word: String): Result<DictionaryResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Making API request for word: $word")
            val response = api.getDefinition(word)
            Log.d(TAG, "Received response: $response")
            
            if (response.isNotEmpty()) {
                Log.d(TAG, "Successfully found definition for word: $word")
                Result.success(response.first())
            } else {
                Log.e(TAG, "No definition found for word: $word")
                Result.failure(IOException("No definition found"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching definition for word: $word", e)
            Result.failure(e)
        }
    }

    fun getFirstDefinition(response: DictionaryResponse): String? {
        return response.meanings.firstOrNull()?.definitions?.firstOrNull()?.definition
    }

    fun getExample(response: DictionaryResponse): String? {
        return response.meanings.firstOrNull()?.definitions?.firstOrNull()?.example
    }

    fun getSynonyms(response: DictionaryResponse): List<String> {
        return response.meanings.flatMap { it.definitions }.flatMap { it.synonyms }
    }

    fun getAntonyms(response: DictionaryResponse): List<String> {
        return response.meanings.flatMap { it.definitions }.flatMap { it.antonyms }
    }

    fun getOrigin(response: DictionaryResponse): String? {
        return response.origin
    }

    fun getPhonetic(response: DictionaryResponse): String? {
        return response.phonetic
    }

    fun getAudioUrl(response: DictionaryResponse): String? {
        return response.phonetics.firstOrNull { it.audio != null }?.audio
    }
} 