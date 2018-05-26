package com.danderson.provemewrong.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.danderson.provemewrong.R
import com.danderson.provemewrong.debatemodel.DebateLine
import com.squareup.picasso.Picasso

class DebateLineAdapter: RecyclerView.Adapter<DebateLineAdapter.ViewHolder>() {

    val debateLines = mutableListOf<DebateLine>()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val picture: ImageView = itemView.findViewById(R.id.profile_picture)
        val content: TextView = itemView.findViewById(R.id.debate_line_content)
        val likeButton: Button = itemView.findViewById(R.id.like_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.entry_debate_line, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return debateLines.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val debateLine = debateLines[position]
        Picasso.get()
                .load(debateLine.user.imageURL)
                .resize(64, 64)
                .placeholder(R.drawable.ic_profile)
                .into(holder.picture)
        holder.content.text = debateLine.content
    }
}