package com.coronaquiz.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.coronaquiz.R

class RankingContainerFragment: Fragment()  {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainView = inflater.inflate(R.layout.fragment_rankings, container, false)
        initComponents(mainView)
        return mainView
    }

    private fun initComponents(mainView: View) {
        mainView.findViewById<ImageView>(R.id.ranking_back_btn)
            .setOnClickListener {
                findNavController().navigateUp()
            }
    }
}