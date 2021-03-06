package com.danderson.provemewrong.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import com.danderson.provemewrong.R
import com.danderson.provemewrong.model.User


class ParticipantAdapter(val creatorId: String): UserAdapter<User>() {

    override var moderatorIndex: Int? = 0

    inner class ViewHolder(itemView: View): UserAdapter<User>.ViewHolder(itemView){
        var remove: ImageButton = itemView.findViewById(R.id.remove_participant)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_participant, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: UserAdapter<User>.ViewHolder, position: Int) {
        holder.layout.orientation = LinearLayout.HORIZONTAL
        holder as ViewHolder

        if(users[position].id == creatorId){
            holder.remove.visibility = View.GONE
        }

        holder.remove.setOnClickListener{
            users.removeAt(holder.adapterPosition)
            if(holder.adapterPosition == moderatorIndex)
                moderatorIndex = 0
            this.notifyItemChanged(0)
            this.notifyItemRemoved(holder.adapterPosition)
        }

        holder.itemView.setOnClickListener{
            val oldIndex = moderatorIndex
            moderatorIndex = holder.adapterPosition
            this.notifyItemChanged(holder.adapterPosition)
            this.notifyItemChanged(oldIndex!!)
        }

        super.onBindViewHolder(holder, position)
    }
}