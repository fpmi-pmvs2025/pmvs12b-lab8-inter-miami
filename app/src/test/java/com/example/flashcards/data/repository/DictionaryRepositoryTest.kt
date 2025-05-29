package com.example.flashcards.data.repository

import com.example.flashcards.data.api.DictionaryApi
import com.example.flashcards.data.api.DictionaryResponse
import com.example.flashcards.data.api.Meaning
import com.example.flashcards.data.api.Definition
import com.example.flashcards.data.api.Phonetic
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import java.io.IOException

class DictionaryRepositoryTest {
    private lateinit var dictionaryApi: DictionaryApi
    private lateinit var dictionaryRepository: DictionaryRepository

    @Before
    fun setup() {
        dictionaryApi = mockk()
        dictionaryRepository = DictionaryRepository()
    }

    private fun createSampleResponse(): DictionaryResponse {
        return DictionaryResponse(
            word = "test",
            phonetic = "/test/",
            phonetics = listOf(
                Phonetic(text = "/test/", audio = "https://audio.com/test.mp3")
            ),
            origin = "From Old English test",
            meanings = listOf(
                Meaning(
                    partOfSpeech = "noun",
                    definitions = listOf(
                        Definition(
                            definition = "A procedure for testing something",
                            example = "A test case",
                            synonyms = listOf("examination", "assessment"),
                            antonyms = listOf("guess")
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `getFirstDefinition returns correct definition`() {
        val response = createSampleResponse()
        
        val result = dictionaryRepository.getFirstDefinition(response)
        
        assertThat(result).isEqualTo("A procedure for testing something")
    }

    @Test
    fun `getFirstDefinition returns null when no definitions available`() {
        val response = DictionaryResponse(
            word = "test",
            phonetic = null,
            phonetics = emptyList(),
            origin = null,
            meanings = emptyList()
        )
        
        val result = dictionaryRepository.getFirstDefinition(response)
        
        assertThat(result).isNull()
    }

    @Test
    fun `getExample returns correct example`() {
        val response = createSampleResponse()
        
        val result = dictionaryRepository.getExample(response)
        
        assertThat(result).isEqualTo("A test case")
    }

    @Test
    fun `getExample returns null when no example available`() {
        val response = DictionaryResponse(
            word = "test",
            phonetic = null,
            phonetics = emptyList(),
            origin = null,
            meanings = listOf(
                Meaning(
                    partOfSpeech = "noun",
                    definitions = listOf(
                        Definition(
                            definition = "A procedure for testing something",
                            example = null,
                            synonyms = emptyList(),
                            antonyms = emptyList()
                        )
                    )
                )
            )
        )
        
        val result = dictionaryRepository.getExample(response)
        
        assertThat(result).isNull()
    }

    @Test
    fun `getSynonyms returns correct synonyms`() {
        val response = createSampleResponse()
        
        val result = dictionaryRepository.getSynonyms(response)
        
        assertThat(result).containsExactly("examination", "assessment")
    }

    @Test
    fun `getAntonyms returns correct antonyms`() {
        val response = createSampleResponse()
        
        val result = dictionaryRepository.getAntonyms(response)
        
        assertThat(result).containsExactly("guess")
    }

    @Test
    fun `getOrigin returns correct origin`() {
        val response = createSampleResponse()
        
        val result = dictionaryRepository.getOrigin(response)
        
        assertThat(result).isEqualTo("From Old English test")
    }

    @Test
    fun `getPhonetic returns correct phonetic`() {
        val response = createSampleResponse()
        
        val result = dictionaryRepository.getPhonetic(response)
        
        assertThat(result).isEqualTo("/test/")
    }

    @Test
    fun `getAudioUrl returns correct audio URL`() {
        val response = createSampleResponse()
        
        val result = dictionaryRepository.getAudioUrl(response)
        
        assertThat(result).isEqualTo("https://audio.com/test.mp3")
    }

    @Test
    fun `getAudioUrl returns null when no audio available`() {
        val response = DictionaryResponse(
            word = "test",
            phonetic = null,
            phonetics = listOf(
                Phonetic(text = "/test/", audio = null)
            ),
            origin = null,
            meanings = emptyList()
        )
        
        val result = dictionaryRepository.getAudioUrl(response)
        
        assertThat(result).isNull()
    }
} 