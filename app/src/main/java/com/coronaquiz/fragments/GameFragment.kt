package com.coronaquiz.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.coronaquiz.components.AnswerButtonsComponent
import com.coronaquiz.R
import com.coronaquiz.classes.RankingLogic
import com.coronaquiz.dataClasses.Question
import com.coronaquiz.viewModels.QuestionsViewModel
import com.coronaquiz.viewModels.RankingViewModel
import com.coronaquiz.viewModels.UserDataViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class GameFragment : Fragment() {
    private val questionsViewModel: QuestionsViewModel by activityViewModels()
    private val userDataViewModel: UserDataViewModel by activityViewModels()
    private val rankingViewModel: RankingViewModel by activityViewModels()
    private val fragmentState =  MutableLiveData<StateFragment>()
    private val rankingLogic = RankingLogic()
    private var totalQuestionSize: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupInitialState()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainView = inflater.inflate(R.layout.fragment_game, container, false)
        observeState(mainView)
        return mainView
    }

    private fun observeState(mainView: View?) {
        fragmentState.observe(viewLifecycleOwner, Observer {
            initViews(mainView, it)
        })
    }

    private fun initViews(
        mainView: View?,
        state: StateFragment
    ) {
        state.remainingQuestions.firstOrNull()?.let {
            mainView?.let { mainView ->
                initQuestionText(mainView, it.question)
                initAlternativesComponent(mainView, it.alternatives)
                initCountView(mainView, state.correctAnswers)
                initCurrentQuestionText(
                    mainView,
                    state.currentQuestion,
                    state.currentQuestion - 1 + state.remainingQuestions.size
                )
            }
        }?: run {
            finishedGameLogic(mainView, state)
        }
    }

    private fun finishedGameLogic(
        mainView: View?,
        state: StateFragment
    ) {
        saveRankingEntry(state.correctAnswers, state.initialTime)
        mainView?.let { initCountView(it, state.correctAnswers) }

        val finishTitle = if (state.correctAnswers == totalQuestionSize) {
            R.string.congratulations
        } else {
            R.string.game_keep_trying
        }

        MaterialAlertDialogBuilder(context)
            .setTitle(finishTitle)
            .setMessage(getString(R.string.game_result, state.correctAnswers, totalQuestionSize))
            .setPositiveButton(R.string.game_play_again) { _, _ ->
                setupInitialState()
            }
            .setNegativeButton(R.string.game_main_menu) { _ ,_ ->
                this@GameFragment.findNavController().navigateUp()
            }
            .setCancelable(false)
            .show()
    }

    private fun saveRankingEntry(correctAnswers: Int, initialTime: Long) {
        val elapsedTimeSeconds = ((System.currentTimeMillis() - initialTime) / 1000).toInt()
        val newRankingEntry = rankingLogic.create(
            userDataViewModel.userData.value!!.username,
            correctAnswers,
            elapsedTimeSeconds,
            userDataViewModel.userData.value!!.countryCode)

        rankingViewModel.shouldReplacePreviousRankingEntry(
            userDataViewModel.uid,
            newRankingEntry)
    }

    private fun initCurrentQuestionText(mainView: View, currentQuestion: Int, totalQuestions: Int) {
        val currentCnt = "$currentQuestion/$totalQuestions"
        mainView.findViewById<TextView>(R.id.gameFragment_current_question)
            .text = currentCnt
    }

    private fun initCountView(mainView: View, correctAnswers: Int) {
        mainView.findViewById<TextView>(R.id.gameFragment_header_correct_count)
            .text = correctAnswers.toString()
    }

    private fun initQuestionText(mainView: View, question: String) {
        mainView.findViewById<TextView>(R.id.gameFragment_question_text)
            .text = question
    }

    private fun initAlternativesComponent(mainView: View, alternatives: List<String>) {
        mainView.findViewById<AnswerButtonsComponent>(R.id.gameFragment_alternatives)
            .initialize(
                alternatives,
                object: IntReceptor {
                    override fun putInt(number: Int) {
                        fragmentState.value?.let {
                            fragmentState.value = it.provideAnswerAndNextQuestion(number)
                        }?: run {
                            throw RuntimeException("State should have been initialized")
                        }
                    }
                }
            )
    }

    private fun setupInitialState() {
        questionsViewModel.questions.value?.let {
            fragmentState.value = StateFragment(it.shuffled())
            totalQuestionSize = it.size
        }?: run {
            throw RuntimeException("Questions should have been loaded")
        }
    }

    inner class StateFragment(
        val remainingQuestions: List<Question>,
        val correctAnswers: Int = 0,
        val currentQuestion: Int = 1,
        val initialTime: Long = System.currentTimeMillis()
    ) {
        fun provideAnswerAndNextQuestion(answer: Int): StateFragment {
            val remainingQuestions = remainingQuestions
            val currentQuestion = remainingQuestions.first()
            val correctAnswers = correctAnswers +
                    if (currentQuestion.provideAnswer(answer)) 1 else 0
            val currentQuestionCnt = this.currentQuestion + 1
            return StateFragment(
                remainingQuestions.drop(1),
                correctAnswers = correctAnswers,
                currentQuestion = currentQuestionCnt,
                initialTime = initialTime
            )
        }
    }

    interface IntReceptor {
        fun putInt(number: Int)
    }
}
