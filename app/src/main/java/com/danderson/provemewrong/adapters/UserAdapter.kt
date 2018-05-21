package com.danderson.provemewrong.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.danderson.provemewrong.R
import com.danderson.provemewrong.debatemodel.User
import com.squareup.picasso.Picasso

open class UserAdapter<T: User>: RecyclerView.Adapter<UserAdapter<T>.ViewHolder>() {

    var users: MutableList<T> = mutableListOf()

    open inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val picture: ImageView = itemView.findViewById(R.id.profile_picture)
        val name: TextView = itemView.findViewById(R.id.display_name)
        val layout: LinearLayout = itemView.findViewById(R.id.user_display)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.entry_user, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.name.text = user.displayName
        Picasso.get()
                .load(user.imageURL)
                .resize(64, 64)
                .placeholder(R.drawable.ic_profile)
                .into(holder.picture)
    }

    override fun getItemCount(): Int {
        return users.size
    }
}