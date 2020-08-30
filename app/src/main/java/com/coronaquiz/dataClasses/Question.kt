package com.coronaquiz.dataClasses

import java.lang.RuntimeException

data class Question(
    val questionDict: Map<String, String>,
    val alternativesDict: Map<String, List<String>>,
    val answer: Int,
    var lang: String = "es"
) {
    constructor(): this(
        mapOf(),
        mapOf(),
        0
    )

    val question: String by lazy {
        questionDict[lang]?: run {
            throw RuntimeException("Language not found for question.")
        }
    }

    val alternatives: List<String> by lazy {
        alternativesDict[lang]?: run {
            throw RuntimeException("Language not found for alternatives.")
        }
    }

    fun provideAnswer(answer: Int): Boolean {
        if (answer + 1 > alternativesDict[alternativesDict.keys.first()]?.size?: run {
                throw RuntimeException("Language not found for alternatives.")
            }) {
            throw RuntimeException("Invalid answer.")
        }

        return answer == this.answer
    }
}
