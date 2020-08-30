package com.coronaquiz.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.coronaquiz.classes.RankingLogic
import com.coronaquiz.dataClasses.IndexScoreAndSortedRankingList
import com.coronaquiz.dataClasses.RankingEntry
import com.coronaquiz.webServices.RankingService
import io.reactivex.rxjava3.core.Completable

class RankingViewModel: ViewModel() {
    val ranking = MutableLiveData<List<RankingEntry>>()
    var userRankingEntry: RankingEntry? = null
    private val rankingService = RankingService()
    private val rankingLogic = RankingLogic()
    private lateinit var indexScoreAndSortedRankingList: IndexScoreAndSortedRankingList

    fun fetch(uid: String): Completable {
        return rankingService.fetch(uid)
            .flatMapCompletable {
                indexScoreAndSortedRankingList = it
                it.userScoreIndex?.let { scoreIndex ->
                    userRankingEntry = it.sortedRankingList[scoreIndex]
                }
                updateRankingList(it.sortedRankingList)
                Completable.complete()
            }
    }

    fun shouldReplacePreviousRankingEntry(
        uid: String,
        newRankingEntry: RankingEntry) {
        if (rankingLogic.shouldReplaceRankingEntry(
                userRankingEntry,
                newRankingEntry
            )) {
            userRankingEntry = newRankingEntry
            rankingService.save(
                uid,
                newRankingEntry
            ).andThen(fetch(uid))
            .subscribe()
        }
    }

    fun updateUsernameRankingEntry(uid: String, newName: String) {
        val userRankingEntry = userRankingEntry?: return
        indexScoreAndSortedRankingList.userScoreIndex?.let {
            val currentRankingList = indexScoreAndSortedRankingList.sortedRankingList
            currentRankingList[it] = rankingLogic.create(
                newName,
                userRankingEntry.score,
                userRankingEntry.timeElapsed,
                userRankingEntry.countryCode
            )
            updateRankingList(currentRankingList)
            rankingService.updateName(uid, newName)
        }
    }

    private fun updateRankingList(rankingList: List<RankingEntry>) {
        ranking.value = rankingList
    }
}