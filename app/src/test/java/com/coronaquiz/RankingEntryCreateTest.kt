package com.coronaquiz

import com.coronaquiz.classes.MAX_ELAPSED_TIME
import com.coronaquiz.classes.RankingLogic
import com.coronaquiz.dataClasses.RankingEntry
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class RankingEntryCreateTest {
    private val rankingLogic = RankingLogic()
    private lateinit var rankingEntry: RankingEntry

    @Before
    fun setup() {
        rankingEntry = rankingLogic.create(
            "anyusername",
            4,
            15,
            "CL")
    }

    @Test
    fun checkCreationIsFine() {
        assertEquals(rankingEntry.username, "anyusername")
        assertEquals(rankingEntry.score, 4)
        assertEquals(rankingEntry.timeElapsed, 15)
        assertEquals(rankingEntry.countryCode, "CL")
        assertEquals(
            rankingEntry.internalScore,
            100000 - (4*1000 + MAX_ELAPSED_TIME - rankingEntry.timeElapsed))
    }
}