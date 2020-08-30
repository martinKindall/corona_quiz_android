package com.coronaquiz.classes

import com.coronaquiz.dataClasses.RankingEntry

const val MAX_ELAPSED_TIME = 300

class RankingLogic {

    fun shouldReplaceRankingEntry(
        previousRanking: RankingEntry?,
        currRanking: RankingEntry
    ): Boolean {
        return previousRanking == null ||
            currRanking.timeElapsed <= MAX_ELAPSED_TIME &&
            previousRanking.internalScore > currRanking.internalScore
    }

    fun create(
        username: String,
        score: Int,
        timeElapsed: Int,
        countryCode: String
    ): RankingEntry {
        return RankingEntry(
            username,
            score,
            timeElapsed,
            countryCode,
            convertScoreAndTimeToComparableScore(
                score,
                timeElapsed
            ))
    }

    private fun convertScoreAndTimeToComparableScore(
        correctAnswers: Int,
        elapsedTime: Int
    ): Int {
        return 100000 - (correctAnswers * 1000 + MAX_ELAPSED_TIME - elapsedTime)
    }
}