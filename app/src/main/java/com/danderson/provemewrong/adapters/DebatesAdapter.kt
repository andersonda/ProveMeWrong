package com.danderson.provemewrong.adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.danderson.provemewrong.activities.DebateActivity
import com.danderson.provemewrong.R
import com.danderson.provemewrong.model.DebateBase
import com.danderson.provemewrong.model.TimedDebate
import com.danderson.provemewrong.model.User
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class DebatesAdapter(val context: Context): RecyclerView.Adapter<DebatesAdapter.ViewHolder>(){

    val EXTRA_DEBATE = "extra_debate"
    val EXTRA_USER = "extra_user"

    var debates = DebateBase.getDebates(FirebaseAuth.getInstance().currentUser!!.uid, this as RecyclerView.Adapter<*>)

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var topic: TextView = itemView.findViewById(R.id.card_topic)
        var category: TextView = itemView.findViewById(R.id.card_category)
        var participants: RecyclerView = itemView.findViewById(R.id.card_participants)
        var time: TextView = itemView.findViewById(R.id.card_time)
        var end: TextView = itemView.findViewById(R.id.card_expiration)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_debate, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val debate = debates[position]
        holder.topic.text = debate.topic
        holder.category.text = debate.category
        val format = SimpleDateFormat("EEE, MMM d", Locale.US)
        holder.time.text = String.format(context.getString(R.string.last_activity), format.format(Date()))
        holder.end.text = if(debate is TimedDebate)
            String.format(context.getString(R.string.end_date), format.format(Date()))
        else
            String.format(context.getString(R.string.end_date), "none")

        holder.participants.layoutManager = GridLayoutManager(context, 4)

        val adapter = UserAdapter<User>()
        adapter.users = DebateBase.getParticipantsForDebate(debate.id!!, adapter)
        holder.participants.adapter = adapter

        holder.itemView.setOnClickListener{
            val intent = Intent(context, DebateActivity::class.java)
            intent.putExtra(EXTRA_DEBATE, debate)
            intent.putExtra(EXTRA_USER, adapter.users.find{it.id == FirebaseAuth.getInstance().currentUser!!.uid})
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return debates.size
    }
}