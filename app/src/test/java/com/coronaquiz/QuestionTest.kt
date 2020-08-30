package com.coronaquiz

import com.coronaquiz.dataClasses.Question
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class QuestionTest {
    private lateinit var question1Alternative: Question
    private lateinit var question2Alternative: Question
    private lateinit var question3Alternative: Question

    @Before
    fun setup() {
        val question1 = mapOf(Pair("es", "¿Capital de Chile?"))
        val question2 = mapOf(
            Pair("es", "¿Población mundial?"),
            Pair("en", "¿World's population?")
            )
        val alternatives1Dict = mapOf(Pair("es", listOf("Santiago", "Buenos Aires")))
        val alternatives2Dict = mapOf(
            Pair("es", listOf("Santiago", "Buenos Aires")),
            Pair("en", listOf("Santiago", "Buenos Aires"))
            )

        question1Alternative = Question(
            question1,
            alternatives1Dict,
            0,
            "es"
        )

        question2Alternative = Question(
            question2,
            alternatives2Dict,
            0,
            "en"
        )

        question3Alternative = Question(
            question2,
            alternatives2Dict,
            0,
            "ru"
        )
    }

    @Test
    fun correctAnswerTest() {
        assertTrue(question1Alternative.provideAnswer(0))
    }

    @Test
    fun badAnswerTest() {
        assertFalse(question1Alternative.provideAnswer(1))
    }

    @Test(expected = RuntimeException::class)
    fun notValidAnswerTest() {
        question1Alternative.provideAnswer(2)
    }

    @Test
    fun getAlternativesTest() {
        assertEquals(listOf("Santiago", "Buenos Aires"), question1Alternative.alternatives)
        assertNotEquals(listOf("Santiago", "Asunción"), question1Alternative.alternatives)
    }

    @Test
    fun getQuestion() {
        assertEquals("¿World's population?", question2Alternative.question)
    }

    @Test(expected = RuntimeException::class)
    fun languageNotFoundTest() {
        question3Alternative.alternatives
    }
}