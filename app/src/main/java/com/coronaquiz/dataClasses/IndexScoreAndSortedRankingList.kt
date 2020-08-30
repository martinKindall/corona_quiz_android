package com.coronaquiz.dataClasses

data class IndexScoreAndSortedRankingList(
    val userScoreIndex: Int?,
    val sortedRankingList: MutableList<RankingEntry>
)
