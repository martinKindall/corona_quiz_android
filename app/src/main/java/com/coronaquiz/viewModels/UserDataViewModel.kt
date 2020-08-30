package com.coronaquiz.viewModels

import android.os.Handler
import android.os.Message
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.coronaquiz.dataClasses.GeoIpCountry
import com.coronaquiz.dataClasses.UserDataToSave
import com.coronaquiz.dataClasses.UserData
import com.coronaquiz.webServices.GeoIpCountryService
import com.coronaquiz.webServices.GetLocationListener
import com.coronaquiz.webServices.UserService
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single


class UserDataViewModel : ViewModel() {
    val userData = MutableLiveData<UserData>()
    lateinit var uid: String
    private val userService = UserService()

    fun loadUserData(
        uid: String,
        email: String?,
        displayName: String?
    ): Completable {
        this.uid = uid
        val email = email?: run { throw RuntimeException("User has no email!") }
        return userService.get(uid).flatMapCompletable { receivedUserData ->
            if (!receivedUserDataIsEmpty(receivedUserData)) {
                userData.value = UserData(
                    receivedUserData.username,
                    email,
                    receivedUserData.countryCode)
                Completable.complete()
            } else {
                Completable.create { subscriber ->
                    var countryCode = "PY"
                    val msg = Message()
                    val handler = Handler {
                        UserDataToSave(
                            displayName,
                            email,
                            countryCode
                        ).let {
                            save(it).subscribe({ userData ->
                                this.userData.value = userData
                                subscriber.onComplete()
                            }, { error ->
                                FirebaseCrashlytics.getInstance().log(error.message?:"Uknown error.")
                                subscriber.onError(error)
                            })
                        }
                        false
                    }
                    GeoIpCountryService().getLocation(object: GetLocationListener {
                        override fun getLocation(geoIpCountry: GeoIpCountry) {
                            countryCode = geoIpCountry.country_code
                        }

                        override fun onError(error: Exception?) {}

                        override fun finally() {
                            object : Thread() {
                                override fun run() {
                                    handler.sendMessage(msg)
                                }
                            }.also {
                                it.start()
                                it.join()
                            }
                        }
                    })
                }
            }
        }
    }

    fun updateUserName(newName: String): Single<Boolean> {
        val previousData = userData.value

        return if (previousData != null &&
                previousData.username != newName) {
                userService.saveName(uid, newName, previousData.username).map {
                    if (it) {
                        userData.value = UserData(
                            newName,
                            previousData.email,
                            previousData.countryCode)
                    } else {
                        userData.value = userData.value
                    }
                    it
                }
            } else {
                Single.just(false)
            }
    }

    private fun receivedUserDataIsEmpty(userData: UserData): Boolean {
        return userData.username == ""
    }

    private fun save(userData: UserDataToSave): Single<UserData> {
        return userService.save(
            uid,
            userData.username,
            userData.countryCode,
            userData.email
        )
    }
}