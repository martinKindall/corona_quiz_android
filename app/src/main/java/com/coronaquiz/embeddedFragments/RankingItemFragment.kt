package com.coronaquiz.embeddedFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.coronaquiz.R
import com.coronaquiz.classes.RankingItemRecyclerViewAdapter
import com.coronaquiz.viewModels.RankingViewModel


class RankingItemFragment : Fragment() {
    private val rankingViewModel: RankingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rankingitem_list, container, false)
        rankingViewModel.ranking.observe(viewLifecycleOwner, Observer {
            if (view is RecyclerView) {
                with(view) {
                    layoutManager = LinearLayoutManager(context)
                    adapter = RankingItemRecyclerViewAdapter(it)
                }
            }
        })
        return view
    }
}
