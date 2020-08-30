package com.coronaquiz.classes

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.coronaquiz.R
import com.coronaquiz.dataClasses.RankingEntry

import kotlinx.android.synthetic.main.fragment_rankingitem.view.*


class RankingItemRecyclerViewAdapter(
    private val mValues: List<RankingEntry>) : RecyclerView.Adapter<RankingItemRecyclerViewAdapter.ViewHolder>() {
    private val utils = Utils()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_rankingitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.positionView.text = (position + 1).toString()
        holder.usernameView.text = item.username
        holder.scoreView.text = item.score.toString()
        holder.timeView.text = item.timeElapsed.toString()
        holder.countryView.text = utils.countryCodeToEmojiFlag(item.countryCode)
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val positionView: TextView = mView.ranking_item_position
        val usernameView: TextView = mView.ranking_item_username
        val scoreView: TextView = mView.ranking_item_score
        val timeView: TextView = mView.ranking_item_time_elapsed
        val countryView: TextView = mView.ranking_item_country
    }
}
