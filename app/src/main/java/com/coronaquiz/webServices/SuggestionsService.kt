package com.coronaquiz.webServices

import com.google.firebase.database.*
import io.reactivex.rxjava3.core.Completable

class SuggestionsService {
    private val database = FirebaseDatabase.getInstance().reference

    fun send(
        uid: String,
        email: String,
        suggestion: String
    ): Completable {
        return Completable.create { subscriber ->
            val dbRef = database.child("userSuggestions")
            val key = dbRef.push().key
            if (key == null) {
                subscriber.onError(RuntimeException("Key not generated"))
            } else {
                database.child("userSuggestions")
                    .child(key)
                    .updateChildren(mapOf(
                        Pair("uid", uid),
                        Pair("email", email),
                        Pair("suggestion", suggestion),
                        Pair("created_at", ServerValue.TIMESTAMP)
                    )).addOnSuccessListener {
                        subscriber.onComplete()
                    }.addOnFailureListener {
                        subscriber.onError(it)
                    }
            }
        }
    }
}