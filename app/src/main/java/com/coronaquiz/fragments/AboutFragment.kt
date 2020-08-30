package com.coronaquiz.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.coronaquiz.R
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import com.coronaquiz.viewModels.UserDataViewModel
import com.coronaquiz.webServices.SuggestionsService
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import android.content.Intent
import android.os.AsyncTask


class AboutFragment : Fragment() {
    private val userDataViewModel: UserDataViewModel by activityViewModels()
    private val suggestionsService = SuggestionsService()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainView =  inflater.inflate(R.layout.fragment_about, container, false)
        initComponents(mainView)

        return mainView
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return findNavController().popBackStack()
    }

    private fun initComponents(mainView: View?) {
        mainView?.let {
            initToolbar(it)
            initEditText(it)
            initShareBtn(it)
        }
    }

    private fun initShareBtn(mainView: View) {
        mainView.findViewById<Button>(R.id.about_share_app)
            .setOnClickListener {
                AsyncTask.execute {
                    try {
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                        val shareMsg = getString(R.string.share_app_msg)
                        val finalMsg = "$shareMsg https://play.google.com/store/apps/details?id=com.coronaquiz\n\n"
                        shareIntent.putExtra(Intent.EXTRA_TEXT, finalMsg)
                        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app_hint)))
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().log(e.message?:"Uknown error.")
                    }
                }

            }
    }

    private fun initEditText(mainView: View) {
        val editTextView = mainView.findViewById<TextInputLayout>(R.id.about_edittext_view)
        val sendSpinner = mainView.findViewById<ProgressBar>(R.id.about_send_spinner)

        mainView.findViewById<Button>(R.id.about_send_btn)
            .setOnClickListener { buttonView ->
                val editText = editTextView.editText?.text.toString()
                if (editText != "null" && editText.length > 5) {
                    handleSpinnerVisibilityAndSendText(
                        buttonView,
                        sendSpinner,
                        editText,
                        editTextView
                    )
                } else {
                    Toast.makeText(context, R.string.text_too_short, Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun handleSpinnerVisibilityAndSendText(
        buttonView: View,
        sendSpinner: ProgressBar,
        editText: String,
        editTextView: TextInputLayout
    ) {
        sendSpinner.visibility = View.VISIBLE
        buttonView.visibility = View.GONE
        suggestionsService.send(
            userDataViewModel.uid,
            userDataViewModel.userData.value?.email?:"",
            editText
        ).subscribe({
            sendSpinner.visibility = View.GONE
            buttonView.visibility = View.VISIBLE
            editTextView.editText?.setText("")
            Toast.makeText(context, R.string.thanks_for_suggestion, Toast.LENGTH_SHORT)
                .show()
        }, {
            FirebaseCrashlytics.getInstance().log(it.message?:"Uknown error.")
        })
    }

    private fun initToolbar(mainView: View) {
        val toolbar = mainView.findViewById<Toolbar>(R.id.about_toolbar)
        (activity as? AppCompatActivity)?.let {
            it.setSupportActionBar(toolbar)
            (activity as? AppCompatActivity)?.supportActionBar?.let {
                it.show()
                it.setDisplayHomeAsUpEnabled(true)
                it.title = getString(R.string.about)
            }
        }
    }
}
