package com.danderson.provemewrong.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.danderson.provemewrong.Navigation
import com.danderson.provemewrong.R
import com.danderson.provemewrong.model.DebateBase
import java.util.*

class CreateDebateFragment : Fragment(){
    private var navigationCallback: Navigation? = null
    private var debateCallback: DebateCreation? = null
    private val DIALOG = 1
    private val EXTRA_DATE = "extra_date"
    private var date = Date()
    private var endTime: TextView? = null

    interface DebateCreation{
        fun onDebateInformationSubmitted(topic: String, category: String, isVote: Boolean, isTurnBased: Boolean, date: String?)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {



        val v = inflater.inflate(R.layout.fragment_create_debate, container, false)
        val topic: EditText = v.findViewById(R.id.topic_name)
        val categories: Spinner = v.findViewById(R.id.spinner_category)
        val end: RadioGroup = v.findViewById(R.id.end_radio_group)
        val turns: RadioGroup = v.findViewById(R.id.turn_radio_group)
        val endButton: Button = v.findViewById(R.id.set_end_time)
        endButton.setOnClickListener{
            val dialog = DebateDatePickerDialog()
            dialog.setTargetFragment(this, DIALOG)
            dialog.show(activity?.supportFragmentManager, "debateDatePicker")
        }

        endTime = v.findViewById(R.id.text_view_end)
        endTime?.text = getString(R.string.end_date, "none")

        val adapter: ArrayAdapter<String> = ArrayAdapter(context, android.R.layout.simple_spinner_item, DebateBase.getDebateCategories())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categories.adapter = adapter


        end.setOnCheckedChangeListener{_, checked ->
            when(checked){
                R.id.radio_timed -> {
                    endButton.isEnabled = true
                    endTime?.text = getString(R.string.end_date, DebateBase.formatter.format(date))
                }
                else -> {
                    endButton.isEnabled = false
                    endTime?.text = getString(R.string.end_date, "none")
                }
            }
        }
        val backButton: Button = v.findViewById(R.id.button_back)
        backButton.setOnClickListener{
            navigationCallback!!.onBackButtonPressed()
        }

        val nextButton: Button = v.findViewById(R.id.button_next)
        nextButton.setOnClickListener{
            navigationCallback!!.onNextButtonPressed()
            val isVote = when(end.checkedRadioButtonId){
                R.id.radio_vote -> true
                else -> false
            }
            val isTurnBased = when(turns.checkedRadioButtonId){
                R.id.radio_turn_based -> true
                else -> false
            }
            debateCallback!!.onDebateInformationSubmitted(topic.text.toString(), categories.selectedItem.toString(),
                    isVote, isTurnBased, DebateBase.formatter.format(date))
        }

        return v
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when(requestCode){
            DIALOG -> {
                if(resultCode == Activity.RESULT_OK){
                    date = data.getSerializableExtra(EXTRA_DATE) as Date
                    endTime?.text = getString(R.string.end_date, DebateBase.formatter.format(date))
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
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
            debateCallback= context as DebateCreation
        } catch (e: ClassCastException){
            throw ClassCastException(context?.toString()
                    + "must implement DebateCreation interface")
        }
    }
}
