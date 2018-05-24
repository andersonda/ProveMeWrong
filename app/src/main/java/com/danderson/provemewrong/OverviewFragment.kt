package com.danderson.provemewrong



import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.danderson.provemewrong.adapters.UserAdapter
import com.danderson.provemewrong.debatemodel.DebateBase
import com.danderson.provemewrong.debatemodel.TimedDebate
import com.danderson.provemewrong.debatemodel.User
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 *
 */
class OverviewFragment : Fragment() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter:RecyclerView.Adapter<DebatesAdapter.ViewHolder>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.fragment_overview, container, false)
        val debates = v.findViewById<RecyclerView>(R.id.recycler_view_debates)
        val fab = v.findViewById<FloatingActionButton>(R.id.floatingActionButton)

        layoutManager = LinearLayoutManager(context)
        debates.layoutManager = layoutManager
        adapter = DebatesAdapter()
        debates.adapter = adapter

        fab.setOnClickListener{
            startActivity(Intent(activity, CreateDebateActivity::class.java))
        }

        return v
    }

    inner class DebatesAdapter: RecyclerView.Adapter<DebatesAdapter.ViewHolder>(){

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
            holder.time.text = String.format(getString(R.string.last_activity), format.format(Date()))
            holder.end.text = if(debate is TimedDebate)
                                String.format(getString(R.string.end_date), format.format(Date()))
                              else
                                String.format(getString(R.string.end_date), "none")

            holder.participants.layoutManager = GridLayoutManager(activity, 4)

            val adapter = UserAdapter<User>()
            adapter.users = DebateBase.getParticipantsForDebate(debate.id!!, adapter)
            holder.participants.adapter = adapter
        }

        override fun getItemCount(): Int {
            return debates.size
        }
    }
}
