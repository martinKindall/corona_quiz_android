package com.coronaquiz.webServices

import com.coronaquiz.dataClasses.Question
import com.google.firebase.database.*
import io.reactivex.rxjava3.core.Single

class QuestionsService {
    private val database = FirebaseDatabase.getInstance().reference

    fun fetchQuestions(): Single<List<Question>> {
        return Single.create { subscriber ->
            val questionsListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val questionsType = object : GenericTypeIndicator<HashMap<String, Question>>() {}
                    val questions = dataSnapshot.getValue(questionsType)
                    questions?.values?.toList()?.let {
                        subscriber.onSuccess(it)
                    }?: run {
                        throw RuntimeException("Questions fetched data is empty (and should not).")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    subscriber.onError(databaseError.toException())
                }
            }
            database.child("questions").addListenerForSingleValueEvent(questionsListener)
        }
    }
}