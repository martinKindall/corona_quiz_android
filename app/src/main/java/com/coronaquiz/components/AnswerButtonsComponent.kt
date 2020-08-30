package com.coronaquiz.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.coronaquiz.R
import com.coronaquiz.classes.Utils
import com.coronaquiz.fragments.GameFragment
import com.google.android.material.button.MaterialButton


class AnswerButtonsComponent(
    context: Context,
    attrs: AttributeSet
): LinearLayout(context, attrs) {
    private val mainView = LayoutInflater.from(context).
        inflate(R.layout.answer_buttons, this, true)

    fun initialize(
        alternatives: List<String>,
        answer: GameFragment.IntReceptor
    ) {
        val containerLayout = mainView.findViewById<LinearLayout>(R.id.alternatives_container)
        containerLayout.removeAllViews()

        alternatives.forEachIndexed { index: Int, it: String ->
            val buttonAlternative = initAlternativeButton(it, index, answer)
            containerLayout.addView(buttonAlternative)
        }
    }

    private fun initAlternativeButton(
        alternative: String,
        index: Int,
        answer: GameFragment.IntReceptor
    ): MaterialButton {
        val buttonAlternative = MaterialButton(context)
        buttonAlternative.text = alternative
        val params = LayoutParams(
            Utils().dpToPixel(context.resources, 200f),
            LayoutParams.WRAP_CONTENT
        )
        params.setMargins(
            0,
            0,
            0,
            Utils().dpToPixel(context.resources, 16f))
        buttonAlternative.layoutParams = params

        buttonAlternative.setOnClickListener {
            answer.putInt(index)
        }

        return buttonAlternative
    }
}