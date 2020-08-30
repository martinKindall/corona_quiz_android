package com.coronaquiz

import com.coronaquiz.classes.RankingLogic
import com.coronaquiz.dataClasses.RankingEntry
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class RankingLogicReplaceRankingTest {
    private lateinit var previuosRankingEntry: RankingEntry
    private lateinit var previuosRankingEntry2: RankingEntry
    private lateinit var previuosRankingEntry3: RankingEntry
    private lateinit var previuosRankingEntry4: RankingEntry
    private lateinit var previuosRankingEntry5: RankingEntry
    private lateinit var previuosRankingEntry6: RankingEntry
    private lateinit var currentRankingEntry: RankingEntry
    private lateinit var currentRankingEntry2: RankingEntry
    private lateinit var currentRankingEntry3: RankingEntry
    private lateinit var currentRankingEntry4: RankingEntry
    private lateinit var currentRankingEntry5: RankingEntry
    private lateinit var currentRankingEntry6: RankingEntry
    private val rankingLogic = RankingLogic()

    @Before
    fun setup() {
        previuosRankingEntry = rankingLogic.create(
            "",
            5,
            10,
            ""
        )

        currentRankingEntry = rankingLogic.create(
            "",
            10,
            20,
            ""
        )

        previuosRankingEntry2 = rankingLogic.create(
            "",
            10,
            10,
            ""
        )

        currentRankingEntry2 = rankingLogic.create(
            "",
            10,
            5,
            ""
        )

        previuosRankingEntry3 = rankingLogic.create(
            "",
            10,
            10,
            ""
        )

        currentRankingEntry3 = rankingLogic.create(
            "",
            10,
            20,
            ""
        )

        previuosRankingEntry4 = rankingLogic.create(
            "",
            10,
            10,
            ""
        )

        currentRankingEntry4 = rankingLogic.create(
            "",
            9,
            5,
            ""
        )

        previuosRankingEntry5 = rankingLogic.create(
            "",
            10,
            10,
            ""
        )

        currentRankingEntry5 = rankingLogic.create(
            "",
            8,
            40,
            ""
        )

        previuosRankingEntry6 = rankingLogic.create(
            "",
            10,
            10,
            ""
        )

        currentRankingEntry6 = rankingLogic.create(
            "",
            11,
            5,
            ""
        )
    }

    @Test
    fun replacePreviousBecauseOfHigherScoreDespiteTookLonger() {
        assertTrue(rankingLogic.shouldReplaceRankingEntry(
            previuosRankingEntry,
            currentRankingEntry
        ))
    }

    @Test
    fun replacePreviousBecauseOfSameScoreButQuicker() {
        assertTrue(rankingLogic.shouldReplaceRankingEntry(
            previuosRankingEntry2,
            currentRankingEntry2
        ))
    }

    @Test
    fun doesntReplacePreviousBecauseSameScoreButTookLonger() {
        assertFalse(rankingLogic.shouldReplaceRankingEntry(
            previuosRankingEntry3,
            currentRankingEntry3
        ))
    }

    @Test
    fun doesntReplacePreviousBecauseLowerScoreEventhoughQuicker() {
        assertFalse(rankingLogic.shouldReplaceRankingEntry(
            previuosRankingEntry4,
            currentRankingEntry4
        ))
    }

    @Test
    fun doesntReplacePreviousBecauseLowerScore() {
        assertFalse(rankingLogic.shouldReplaceRankingEntry(
            previuosRankingEntry5,
            currentRankingEntry5
        ))
    }

    @Test
    fun replacePreviousBecauseOfHigherScore() {
        assertTrue(rankingLogic.shouldReplaceRankingEntry(
            previuosRankingEntry6,
            currentRankingEntry6
        ))
    }
}