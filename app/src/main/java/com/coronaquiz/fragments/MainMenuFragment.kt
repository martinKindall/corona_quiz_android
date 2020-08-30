package com.coronaquiz.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.coronaquiz.R
import com.coronaquiz.dataClasses.RankingEntry
import com.coronaquiz.dataClasses.UserData
import com.coronaquiz.viewModels.RankingViewModel
import com.coronaquiz.viewModels.UserDataViewModel
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.reactivex.rxjava3.core.Completable


class MainMenuFragment : Fragment() {
    private val userDataViewModel: UserDataViewModel by activityViewModels()
    private val rankingViewModel: RankingViewModel by activityViewModels()
    private var editNameState = false

    override fun onStart() {
        super.onStart()
        (activity as? AppCompatActivity)?.let {
            it.supportActionBar?.hide()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainView = inflater.inflate(R.layout.fragment_main_menu, container, false)
        initComponents(mainView)
        observeUserData(mainView)
        observeRankingEntry(mainView)
        return mainView
    }

    override fun onStop() {
        super.onStop()
        editNameState = false
    }

    private fun observeRankingEntry(mainView: View) {
        rankingViewModel.ranking.observe(viewLifecycleOwner, Observer {
            initRankingData(mainView, rankingViewModel.userRankingEntry)
        })
    }

    private fun initRankingData(mainView: View, userRankingEntry: RankingEntry?) {
        val scoreView = mainView.findViewById<TextView>(R.id.menu_user_data_obtained_score_view)
        val scoreIconsView = mainView.findViewById<View>(R.id.menu_user_data_score_icon)
        val noScoreView = mainView.findViewById<TextView>(R.id.menu_user_data_no_score_text)

        userRankingEntry?.let {
            scoreView.text = it.score.toString()
            scoreView.visibility = View.VISIBLE
            scoreIconsView.visibility = View.VISIBLE
            noScoreView.visibility = View.GONE
        }?: run {
            noScoreView.visibility = View.VISIBLE
            scoreView.visibility = View.GONE
            scoreIconsView.visibility = View.GONE
        }
    }

    private fun observeUserData(mainView: View) {
        userDataViewModel.userData.observe(viewLifecycleOwner, Observer {
            initUserDataView(mainView, it)
            initEditNameView(mainView, it)
        })
    }

    private fun initEditNameView(mainView: View, userData: UserData) {
        val userNameView = mainView.findViewById<TextView>(R.id.menu_user_data_username_view)
        val editUsernameView = mainView.findViewById<TextInputLayout>(R.id.menu_user_data_edittext)
        editUsernameView.editText?.setText(userData.username)

        mainView
            .findViewById<ImageView>(R.id.menu_user_editname_view)
            .setOnClickListener { editView ->
                if (editNameState) {
                    editNameState = false
                    userNameView.visibility = View.VISIBLE
                    editUsernameView.visibility = View.GONE
                    editUsernameView.editText?.let {
                        validateUsernameAndShowSpinner(
                            mainView, editView, it)
                    }
                } else {
                    editNameState = true
                    userNameView.visibility = View.GONE
                    editUsernameView.visibility = View.VISIBLE
                }
            }
    }

    private fun validateUsernameAndShowSpinner(
        mainView: View,
        editView: View,
        editText: EditText
    ) {
        val spinner = mainView
            .findViewById<ProgressBar>(R.id.menu_edit_spinner)
        spinner.visibility = View.VISIBLE
        editView.visibility = View.GONE
        validateAndUpdateUsername(editText.text.toString())
            .subscribe({
                spinner.visibility = View.GONE
                editView.visibility = View.VISIBLE
            }, {
                FirebaseCrashlytics.getInstance()
                    .log(it.message?:"Uknown error")
            })
    }

    private fun validateAndUpdateUsername(newName: String): Completable {
        return if (newName != "null" && newName.length > 2) {
            userDataViewModel.updateUserName(newName)
                .flatMapCompletable { isNotRepeated ->
                    if (isNotRepeated) {
                        rankingViewModel.updateUsernameRankingEntry(
                            userDataViewModel.uid,
                            newName
                        )
                    } else {
                        Toast.makeText(context, R.string.username_already_exists, Toast.LENGTH_SHORT)
                            .show()
                    }
                    Completable.complete()
                }
        } else {
            Toast.makeText(context, R.string.username_too_short, Toast.LENGTH_SHORT)
                .show()
            Completable.complete()
        }
    }

    private fun initComponents(mainView: View?) {
        mainView?.let {
            initGameBtn(it)
            initRankingsBtn(it)
            initCloseSessionBtn(it)
            initAboutBtn(it)
        }
    }

    private fun initAboutBtn(mainView: View) {
        mainView.findViewById<Button>(R.id.main_menu_about)
            .setOnClickListener {
                findNavController().navigate(R.id.action_mainMenuFragment_to_aboutFragment)
            }
    }

    private fun initCloseSessionBtn(mainView: View) {
        mainView.findViewById<Button>(R.id.main_menu_close_session)
            .setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                activity?.recreate()
            }
    }

    private fun initUserDataView(
        mainView: View,
        userData: UserData
    ) {
        mainView.findViewById<TextView>(R.id.menu_user_data_username_view)
            .text = userData.username
    }

    private fun initRankingsBtn(mainView: View) {
        mainView.findViewById<Button>(R.id.rankingsBtn)
            .setOnClickListener {
                findNavController().navigate(R.id.action_mainMenuFragment_to_rankingContainerFragment)
            }
    }

    private fun initGameBtn(mainView: View) {
        mainView.findViewById<Button>(R.id.startGameBtn)
            .setOnClickListener {
                findNavController().navigate(R.id.action_mainMenuFragment_to_gameFragment)
            }
    }
}
