package com.coronaquiz.webServices

import com.coronaquiz.dataClasses.IndexScoreAndSortedRankingList
import com.coronaquiz.dataClasses.RankingEntry
import com.google.firebase.database.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class RankingService {
    private val database = FirebaseDatabase.getInstance().reference

    fun save(
        uid: String,
        rankingEntry: RankingEntry): Completable {
        return Completable.create { subscriber ->
            database.child("ranking").child(uid)
                .updateChildren(mapOf(
                    Pair("username", rankingEntry.username),
                    Pair("countryCode", rankingEntry.countryCode),
                    Pair("score", rankingEntry.score),
                    Pair("timeElapsed", rankingEntry.timeElapsed),
                    Pair("created_at", ServerValue.TIMESTAMP),
                    Pair("internalScore", rankingEntry.internalScore)
                )).addOnCompleteListener {
                    subscriber.onComplete()
                }
        }
    }

    fun fetch(uid: String): Single<IndexScoreAndSortedRankingList> {
        return Single.create { subscriber ->
            val rankingListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val rankingSortedList = arrayListOf<RankingEntry>()
                    var indexRanking: Int? = null
                    dataSnapshot.children.forEachIndexed { index, rankingSnapshot ->
                        if (rankingSnapshot.key == uid) {
                            indexRanking = index
                        }
                        rankingSnapshot.getValue(RankingEntry::class.java)?.let {
                            rankingSortedList.add(it)
                        }
                    }
                    subscriber.onSuccess(IndexScoreAndSortedRankingList(
                        indexRanking,
                        rankingSortedList
                    ))
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    subscriber.onError(databaseError.toException())
                }
            }
            database.child("ranking")
                .orderByChild("internalScore")
                .addListenerForSingleValueEvent(rankingListener)
        }
    }

    fun updateName(uid: String, newName: String) {
        database.child("ranking/$uid").updateChildren(mapOf(
            Pair("username", newName)))
    }
}