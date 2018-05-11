package com.danderson.provemewrong

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import com.danderson.provemewrong.debatemodel.Debate
import com.danderson.provemewrong.debatemodel.DebateBase
import com.danderson.provemewrong.debatemodel.TimedDebate

class CreateDebateActivity : AppCompatActivity(), CreateDebateFragment.DebateCreation {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_debate)
        configureUI()
    }

    override fun onDebateInformationSubmitted(topic: String, category: String, isVote: Boolean, isTurnBased: Boolean) {
        val debate = if(isVote) Debate(topic, category, isTurnBased)
                     else TimedDebate(topic, category, isTurnBased)
        DebateBase.add(debate)
    }


    private fun configureUI(){
        supportFragmentManager.beginTransaction().add(R.id.create_debate_container,
                CreateDebateFragment()).commit()
    }


}
