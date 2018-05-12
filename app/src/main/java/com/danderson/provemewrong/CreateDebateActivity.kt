package com.danderson.provemewrong

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.danderson.provemewrong.debatemodel.Debate
import com.danderson.provemewrong.debatemodel.DebateBase
import com.danderson.provemewrong.debatemodel.TimedDebate
import java.util.*

class CreateDebateActivity : AppCompatActivity(), CreateDebateFragment.DebateCreation{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_debate)

        supportFragmentManager.beginTransaction().add(R.id.create_debate_container,
                CreateDebateFragment()).commit()
    }

    override fun onDebateInformationSubmitted(topic: String, category: String, isVote: Boolean, isTurnBased: Boolean, date: Date?) {
        val debate = if(isVote) Debate(topic, category, isTurnBased)
                     else TimedDebate(topic, category, isTurnBased, date!!)

        DebateBase.add(debate)
        finish()
    }
}
