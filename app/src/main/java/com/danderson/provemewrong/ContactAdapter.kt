package com.danderson.provemewrong

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.danderson.provemewrong.debatemodel.Contact
import com.danderson.provemewrong.debatemodel.DebateBase
import com.google.firebase.auth.FirebaseAuth

class ContactAdapter(val pending: Boolean, val context: Context): UserAdapter(){

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

        val type = (users[position] as Contact).type
        if(pending){
            val display = holder.name.text.toString()
            val displayWithSuffix =  if(type == Contact.ContactStatus.SENT)
                                        String.format(context.getString(R.string.request_sent), display)
                                     else
                                        String.format(context.getString(R.string.request_received), display)

            holder.name.text = displayWithSuffix
        }

        val popup = createPopup(holder, position, type)
        val optionsButton = (holder as ViewHolder).options
        optionsButton.setOnClickListener{
            popup.show()
        }
    }

    private fun createPopup(holder: UserAdapter.ViewHolder, position: Int, type: Contact.ContactStatus): PopupMenu{
        val popup = PopupMenu(context, (holder as ViewHolder).options)
        when(type){
            Contact.ContactStatus.SENT -> {
                popup.menuInflater.inflate(R.menu.popup_sent_contact, popup.menu)
            }
            Contact.ContactStatus.RECEIVED -> {
                popup.menuInflater.inflate(R.menu.popup_received_contact, popup.menu)
            }
            Contact.ContactStatus.ACCEPTED -> {
                Log.i("ACCEPTED", "accepted")
                popup.menuInflater.inflate(R.menu.popup_accepted_contact, popup.menu)
            }
        }

        popup.setOnMenuItemClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
            val contact = users[position].id
            when(it.itemId){
                R.id.cancel_request -> {
                    DebateBase.removeContact(currentUser, contact)
                    true
                }
                R.id.reject_request -> {
                    DebateBase.removeContact(currentUser, contact)
                    true
                }
                R.id.accept_request -> {
                    DebateBase.acceptContact(currentUser, contact)
                    true
                }
                R.id.remove_contact -> {
                    DebateBase.removeContact(currentUser, contact)
                    true
                }
                else -> false
            }
        }

        return popup
    }
}