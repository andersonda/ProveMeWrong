package com.danderson.provemewrong

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.danderson.provemewrong.debatemodel.DebateBase
import kotlinx.android.synthetic.main.fragment_profile.view.*

class CreateDebateFragment : Fragment() {

    var activityCallback: CreateDebateFragment.DebateCreation? = null

    interface DebateCreation{
        fun onDebateInformationSubmitted(topic: String, category: String, isVote: Boolean, isTurnBased: Boolean)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_create_debate, container, false)
        val topic: EditText = v.findViewById(R.id.topic_name)
        val categories: Spinner = v.findViewById(R.id.spinner_category)
        val end: RadioGroup = v.findViewById(R.id.end_radio_group)
        val turns: RadioGroup = v.findViewById(R.id.turn_radio_group)

        val adapter: ArrayAdapter<String> = ArrayAdapter(context, android.R.layout.simple_spinner_item, DebateBase.categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categories.adapter = adapter

        val cancelButton: Button = v.findViewById(R.id.button_back)
        cancelButton.setOnClickListener{
            startActivity(Intent(activity, OverviewActivity::class.java))
            activity?.finish()
        }

        val submitButton: Button = v.findViewById(R.id.button_next)
        submitButton.setOnClickListener{
            val isVote = when(end.checkedRadioButtonId){
                R.id.radio_vote -> true
                else -> false
            }
            val isTurnBased = when(turns.checkedRadioButtonId){
                R.id.radio_turn_based -> true
                else -> false
            }
            activityCallback?.onDebateInformationSubmitted(topic.text.toString(),
                    categories.selectedItem.toString(), isVote, isTurnBased)

            // placeholder, should next go through screens for participant selection and (optionally)
            // expiration time
            startActivity(Intent(activity, OverviewActivity::class.java))
            activity?.finish()
        }

        return v
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            activityCallback = context as DebateCreation
        } catch (e: ClassCastException){
            throw ClassCastException(context?.toString()
                + "must implement DebateCreation interface")
        }
    }
}
