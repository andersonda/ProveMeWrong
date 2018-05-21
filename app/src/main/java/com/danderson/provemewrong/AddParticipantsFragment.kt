package com.danderson.provemewrong

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.danderson.provemewrong.adapters.ParticipantAdapter
import com.danderson.provemewrong.adapters.UserAdapter
import com.danderson.provemewrong.debatemodel.DebateBase
import com.google.firebase.auth.FirebaseAuth

class AddParticipantsFragment : Fragment(){
    private var navigationCallback: Navigation? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.fragment_add_participants, container, false)

        val backButton: Button = v.findViewById(R.id.button_back)
        backButton.setOnClickListener{
            navigationCallback!!.onBackButtonPressed()
        }

        val nextButton: Button = v.findViewById(R.id.button_next)
        nextButton.setOnClickListener{
            navigationCallback!!.onNextButtonPressed()
        }

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val participants: RecyclerView = v.findViewById(R.id.recycler_participants)
        participants.layoutManager = LinearLayoutManager(activity)
        val adapter = ParticipantAdapter(uid)
        adapter.users = DebateBase.getUser(uid)
        participants.adapter = adapter

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
    }
}
