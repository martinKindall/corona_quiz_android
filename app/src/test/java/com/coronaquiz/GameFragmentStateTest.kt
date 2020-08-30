package com.coronaquiz

import com.coronaquiz.fragments.GameFragment
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class GameFragmentStateTest {
    private lateinit var state1: GameFragment.StateFragment

    @Before
    fun setup() {
        state1 = GameFragment().StateFragment(Mock().questions)
    }

    @Test
    fun correctAnswersIncreased() {
        assertEquals(state1.correctAnswers, 0)
        val nextState = state1.provideAnswerAndNextQuestion(0)
        assertEquals(nextState.correctAnswers, 1)
        val lastState = nextState.provideAnswerAndNextQuestion(1)
        assertEquals(lastState.correctAnswers, 2)
    }

    @Test
    fun correctAnswerDoesntIncrease() {
        assertEquals(state1.correctAnswers, 0)
        val nextState = state1.provideAnswerAndNextQuestion(2)
        assertEquals(nextState.correctAnswers, 0)
        val lastState = nextState.provideAnswerAndNextQuestion(0)
        assertEquals(lastState.correctAnswers, 0)
    }

    @Test
    fun nextQuestionTest() {
        assertEquals(state1.remainingQuestions.size, 3)
        assertEquals(
            state1.remainingQuestions.first().question,
            "¿En cuál ciudad de China se originó el Coronavirus?")
        val nextState = state1.provideAnswerAndNextQuestion(0)
        assertEquals(nextState.remainingQuestions.size, 2)
        assertEquals(
            nextState.remainingQuestions.first().question,
            "¿Qué día se declaró el Coronavirus como Pandemia mundial?")
        val lastState = nextState.provideAnswerAndNextQuestion(0)
        assertEquals(lastState.remainingQuestions.size, 1)
    }
}