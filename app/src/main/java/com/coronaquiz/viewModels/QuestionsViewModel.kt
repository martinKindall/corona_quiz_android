package com.coronaquiz.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.coronaquiz.dataClasses.Question
import com.coronaquiz.webServices.QuestionsService
import io.reactivex.rxjava3.core.Completable
import java.util.*


class QuestionsViewModel: ViewModel() {
    val questions = MutableLiveData<List<Question>>()
    private val lang = Locale.getDefault().language

    fun fetchQuestions(): Completable {
        return QuestionsService()
            .fetchQuestions()
            .flatMapCompletable {
                it.forEach { question ->
                    question.lang = lang
                }
                questions.value = it
                Completable.complete()
            }
    }
}