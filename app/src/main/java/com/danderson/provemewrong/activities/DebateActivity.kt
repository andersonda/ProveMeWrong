package com.danderson.provemewrong.activities

import android.media.Image
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.danderson.provemewrong.R
import com.danderson.provemewrong.adapters.DebateLineAdapter
import com.danderson.provemewrong.fragments.BottomSheetDebateDialog
import com.danderson.provemewrong.fragments.ViewProfileDialog
import com.danderson.provemewrong.model.Debate
import com.danderson.provemewrong.model.DebateBase
import com.danderson.provemewrong.model.DebateLine
import com.danderson.provemewrong.model.User
import com.danderson.provemewrong.utils.Animations
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class DebateActivity: AppCompatActivity(), BottomSheetDebateDialog.BottomSheetMenuCallbacks {

    val EXTRA_DEBATE = "extra_debate"
    val EXTRA_USER = "extra_user"

    private var user: User? = null
    private var debate: Debate? = null
    private var message: EditText? = null
    private var send: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debate)
        supportActionBar?.title = "Debate"
        user = intent.getSerializableExtra(EXTRA_USER) as User
        debate = intent.getSerializableExtra(EXTRA_DEBATE) as Debate

        val recycler = findViewById<RecyclerView>(R.id.recycler_debate_lines)
        val layout = LinearLayoutManager(this)
        layout.stackFromEnd = true
        recycler.layoutManager = layout
        val adapter = DebateLineAdapter(this)
        adapter.debateLines = DebateBase.getLinesForDebate(debate!!, adapter)
        adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver(){
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                val messageCount = adapter.itemCount
                val lastVisiblePosition = layout.findLastCompletelyVisibleItemPosition()
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 || positionStart >= messageCount - 1 && lastVisiblePosition == positionStart - 1) {
                    recycler.scrollToPosition(positionStart)
                }
            }
        })
        recycler.adapter = adapter
        recycler.itemAnimator = Animations.getDebateLineAnimator()

        send = findViewById(R.id.send_message)
        message = findViewById(R.id.message_text)

        send!!.setOnClickListener(NewDebateLineOnClickListener())
        send!!.isEnabled = false
        send!!.visibility = View.INVISIBLE

        message!!.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                send!!.isEnabled = !s.toString().trim().isEmpty()
                send!!.visibility = if(!s.toString().trim().isEmpty()) View.VISIBLE else View.INVISIBLE
            }
        })

        val toast: Toast = Toast.makeText(this, getString(R.string.tip_message_actions), Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    override fun onLikeMessage(line: DebateLine) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEditMessage(line: DebateLine) {
        message!!.setText(line.content, TextView.BufferType.EDITABLE)
        message!!.setSelection(message!!.text.length)
        send!!.setOnClickListener(EditDebateLineOnClickListener(line))
    }

    override fun onRemoveMessage(line: DebateLine) {
        DebateBase.removeDebateLine(debate!!, line)
    }

    override fun onViewProfile(line: DebateLine) {
        val dialog: ViewProfileDialog = ViewProfileDialog.newInstance(user!!, line.user)
        dialog.show(supportFragmentManager, dialog.tag)
    }

    inner class NewDebateLineOnClickListener: View.OnClickListener{
        override fun onClick(v: View) {
            val debateLine = DebateLine(
                    "", user!!, message!!.text.toString(), DebateBase.formatter.format(Date())
            )
            DebateBase.addDebateLine(debate!!, debateLine)
            message!!.text.clear()
        }
    }

    /**
     * Single click event for editing an existing debate line
     */
    inner class EditDebateLineOnClickListener(private val line: DebateLine): View.OnClickListener{
        override fun onClick(v: View) {
            DebateBase.editDebateLine(debate!!, line, message!!.text.toString())
            message!!.text.clear()
            send!!.setOnClickListener(NewDebateLineOnClickListener())
        }
    }
}
