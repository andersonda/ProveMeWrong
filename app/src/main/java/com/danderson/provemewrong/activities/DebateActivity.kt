package com.danderson.provemewrong.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.EditText
import android.widget.ImageButton
import com.danderson.provemewrong.R
import com.danderson.provemewrong.adapters.DebateLineAdapter
import com.danderson.provemewrong.model.Debate
import com.danderson.provemewrong.model.DebateBase
import com.danderson.provemewrong.model.DebateLine
import com.danderson.provemewrong.model.User
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class DebateActivity: AppCompatActivity() {

    val EXTRA_DEBATE = "extra_debate"
    val EXTRA_USER = "extra_user"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debate)
        supportActionBar?.title = "Debate"

        val debate = intent.getSerializableExtra(EXTRA_DEBATE) as Debate
        val user = intent.getSerializableExtra(EXTRA_USER) as User

        val recycler = findViewById<RecyclerView>(R.id.recycler_debate_lines)
        val layout = LinearLayoutManager(this)
        layout.stackFromEnd = true
        recycler.layoutManager = layout
        val adapter = DebateLineAdapter()
        recycler.adapter = adapter

        val send = findViewById<ImageButton>(R.id.send_message)
        val message = findViewById<EditText>(R.id.message_text)

        send.setOnClickListener {
            val debateLine = DebateLine(
                    user, message.text.toString(), DebateBase.formatter.format(Date())
            )
            adapter.debateLines.add(debateLine)
            adapter.notifyDataSetChanged()
            recycler.smoothScrollToPosition(adapter.itemCount - 1)
            message.text.clear()
        }
    }
}
