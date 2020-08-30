package com.coronaquiz.webServices

import com.coronaquiz.dataClasses.UserData
import com.google.firebase.database.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

private const val DEFAULT_NAME = "Default"

class UserService {
    private val database = FirebaseDatabase.getInstance().reference

    fun get(uid: String): Single<UserData> {
        return Single.create { subscriber ->
            val userDataListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.getValue(UserData::class.java)?.let {
                        subscriber.onSuccess(it)
                    }?: run {
                        subscriber.onSuccess(UserData("", "", ""))
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    subscriber.onError(databaseError.toException())
                }
            }
            database.child("users/$uid").addListenerForSingleValueEvent(userDataListener)
        }
    }

    fun save(uid: String, username: String?, countryCode: String, email: String): Single<UserData> {
        return Completable.create { subscriber ->
            database.child("users/$uid").updateChildren(mapOf(
                Pair("username", DEFAULT_NAME),
                Pair("countryCode", countryCode),
                Pair("email", email),
                Pair("created_at", ServerValue.TIMESTAMP)
            )).addOnCompleteListener {
                subscriber.onComplete()
            }
        }.toSingle {}.flatMap {
            if (username != null && username != DEFAULT_NAME) {
                saveName(uid, username).map {
                    if (it) {
                        UserData(username, email, countryCode)
                    } else {
                        UserData(DEFAULT_NAME, email, countryCode)
                    }
                }
            } else {
                Single.just(UserData(DEFAULT_NAME, email, countryCode))
            }
        }
    }

    fun saveName(uid: String, newName: String, oldName: String? = null): Single<Boolean> {
        return Single.create { subscriber ->
            database.child("users/$uid").updateChildren(mapOf(
                Pair("username", newName)))
                .addOnSuccessListener {
                    subscriber.onSuccess(true)
                    if (newName != DEFAULT_NAME) {
                        database.child("usernames").updateChildren(mapOf(
                            Pair(newName, uid)))
                    }
                    oldName?.let {
                        database.child("usernames/$it").removeValue()
                    }
                }
                .addOnCanceledListener {
                    subscriber.onSuccess(false)
                }
                .addOnFailureListener {
                    when (it) {
                        is DatabaseException -> subscriber.onSuccess(false)
                        else -> subscriber.onError(it)
                    }
                }
        }
    }
}