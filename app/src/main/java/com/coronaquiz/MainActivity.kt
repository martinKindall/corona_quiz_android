package com.coronaquiz

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.viewModels
import com.coronaquiz.viewModels.QuestionsViewModel
import com.coronaquiz.viewModels.RankingViewModel
import com.coronaquiz.viewModels.UserDataViewModel
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.reactivex.rxjava3.core.Completable


private const val RC_SIGN_IN: Int = 10
private const val REFRESH_TIME_SECONDS = 60L

class MainActivity : AppCompatActivity() {
    private val questionsViewModel: QuestionsViewModel by viewModels()
    private val userDataViewModel: UserDataViewModel by viewModels()
    private val rankingViewModel: RankingViewModel by viewModels()
    private var cachedTime: Long? = null
    private var firstStartUp = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            login()
        } else {
            initApp(user)
        }
        cachedTime = System.currentTimeMillis()
    }

    override fun onStart() {
        super.onStart()
        if (firstStartUp) {
            firstStartUp = false
        } else {
            ifRefreshRefetchData()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                FirebaseAuth.getInstance().currentUser?.let {
                    initApp(it)
                }?: run {
                    FirebaseCrashlytics.getInstance().log("User signed in but no user instance")
                    finishAndRemoveTask()
                }
            } else {
                finishAndRemoveTask()
            }
        }
    }

    private fun initApp(user: FirebaseUser) {
        showSpinnerWhileComplete(
            Completable.merge(listOf(
                loadUserData(user),
                loadQuestions(),
                loadRanking(user.uid)
            )).andThen(loadAds())
        )
    }

    private fun loadAds(): Completable {
        MobileAds.initialize(this, "ca-app-pub-6771574080197752~7286055667")
        val mAdView: AdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        return Completable.complete()
    }

    private fun reloadQuestionsAndRanking(user: FirebaseUser) {
        showSpinnerWhileComplete(Completable.merge(listOf(
            loadQuestions(),
            loadRanking(user.uid)
        )))
    }

    private fun showSpinnerWhileComplete(
        completable: Completable) {
        val spinnerView = findViewById<ProgressBar>(R.id.activity_spinner)
        val navHost = findViewById<View>(R.id.nav_host_fragment)
        spinnerView.visibility = View.VISIBLE
        navHost.visibility = View.GONE
        completable.subscribe {
            spinnerView.visibility = View.GONE
            navHost.visibility = View.VISIBLE
        }
    }

    private fun login() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build())

        val customLayout = AuthMethodPickerLayout
            .Builder(R.layout.login_layout)
            .setEmailButtonId(R.id.fui_sign_in_with_email)
            .setGoogleButtonId(R.id.fui_sign_in_with_google)
            .setTosAndPrivacyPolicyId(R.id.main_tos_and_pp)
            .build()

        val policyLink = getString(R.string.policy)

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAuthMethodPickerLayout(customLayout)
                .setTheme(R.style.AppThemeFirebaseAuth)
                .setTosAndPrivacyPolicyUrls(
                    policyLink,
                    policyLink
                )
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN)
    }

    private fun loadUserData(user: FirebaseUser): Completable {
        return userDataViewModel.loadUserData(
            user.uid,
            user.email,
            user.displayName
        )
    }

    private fun loadQuestions(): Completable {
        return questionsViewModel.fetchQuestions()
    }

    private fun loadRanking(uid: String): Completable {
        return rankingViewModel.fetch(uid)
    }

    private fun shouldRefresh(): Boolean {
        return cachedTime?.let {
             (System.currentTimeMillis() - it) / 1000 > REFRESH_TIME_SECONDS
        }?: true
    }

    private fun ifRefreshRefetchData() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null && shouldRefresh()) {
            reloadQuestionsAndRanking(user)
            cachedTime = System.currentTimeMillis()
        }
    }
}
