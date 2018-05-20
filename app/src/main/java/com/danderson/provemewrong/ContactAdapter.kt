package com.danderson.provemewrong

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.danderson.provemewrong.debatemodel.Contact
import com.danderson.provemewrong.debatemodel.User
import com.squareup.picasso.Picasso

class ContactAdapter(val pending: Boolean): UserAdapter(){

    inner class ViewHolder(itemView: View): UserAdapter.ViewHolder(itemView){
        var options: ImageButton = itemView.findViewById(R.id.contact_options)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recycler_contact, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        holder.layout.orientation = LinearLayout.HORIZONTAL
        super.onBindViewHolder(holder, position)
        if(pending){
            val type = (users[position] as Contact).type
            val suffix =  if(type == Contact.ContactStatus.SENT) " (Request Sent)"
                            else " (Request Received)"

            holder.name.text = holder.name.text.toString() + suffix
        }
    }
}