package com.danderson.provemewrong.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.danderson.provemewrong.Navigation
import com.danderson.provemewrong.R
import com.danderson.provemewrong.adapters.ContactAdapter
import com.danderson.provemewrong.adapters.ParticipantAdapter
import com.danderson.provemewrong.adapters.UserAdapter
import com.danderson.provemewrong.model.Contact
import com.danderson.provemewrong.model.DebateBase
import com.danderson.provemewrong.model.User
import com.google.firebase.auth.FirebaseAuth

class AddParticipantsFragment : Fragment(){
    private var navigationCallback: Navigation? = null
    private var participantsCallback: AddParticipants? = null

    interface AddParticipants{
        fun onParticipantsAdded(users: List<User>)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.fragment_add_participants, container, false)

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val participants: RecyclerView = v.findViewById(R.id.recycler_participants)
        participants.layoutManager = LinearLayoutManager(activity)
        val participantAdapter = ParticipantAdapter(uid)
        participantAdapter.users = DebateBase.getUser(uid)
        participants.adapter = participantAdapter


        val contacts: RecyclerView = v.findViewById(R.id.recycler_contacts)
        contacts.layoutManager = LinearLayoutManager(activity)
        val contactAdapter = object: ContactAdapter(false, context!!){

            override fun onBindViewHolder(holder: UserAdapter<Contact>.ViewHolder, position: Int) {
                (holder as ContactAdapter.ViewHolder).options.visibility = View.GONE
                holder.itemView.setOnClickListener{
                    val user = users[position] as User
                    if(!participantAdapter.users.contains(user)){
                        participantAdapter.users.add(user)
                        participantAdapter.notifyDataSetChanged()
                    }
                }
                super.onBindViewHolder(holder, position)
            }
        }

        contactAdapter.users = DebateBase.getContactsForUser(uid, contactAdapter, null).accepted
        contacts.adapter = contactAdapter

        val backButton: Button = v.findViewById(R.id.button_back)
        backButton.setOnClickListener{
            navigationCallback!!.onBackButtonPressed()
        }

        val nextButton: Button = v.findViewById(R.id.button_next)
        nextButton.setOnClickListener{
            navigationCallback!!.onNextButtonPressed()
            participantsCallback!!.onParticipantsAdded(participantAdapter.users)
        }

        return v
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            navigationCallback = context as Navigation
        } catch (e: ClassCastException){
            throw ClassCastException(context?.toString()
                    + "must implement Navigation interface")
        }
        try {
            participantsCallback = context as AddParticipants
        } catch (e: ClassCastException){
            throw ClassCastException(context?.toString()
                    + "must implement AddParticipants interface")
        }
    }
}
