package com.danderson.provemewrong



import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.danderson.provemewrong.debatemodel.Debate
import com.danderson.provemewrong.debatemodel.DebateBase
import com.danderson.provemewrong.debatemodel.TimedDebate
import kotlinx.android.synthetic.main.fragment_overview.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.RecursiveAction

/**
 * A simple [Fragment] subclass.
 *
 */
class OverviewFragment : Fragment() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter:RecyclerView.Adapter<DebatesAdapter.ViewHolder>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_overview, container, false)
        val debates = v.findViewById<RecyclerView>(R.id.recycler_view_debates)
        val fab = v.findViewById<FloatingActionButton>(R.id.floatingActionButton)

        layoutManager = LinearLayoutManager(activity)
        debates.layoutManager = layoutManager
        adapter = DebatesAdapter()
        debates.adapter = adapter

        fab.setOnClickListener{
            startActivity(Intent(activity, CreateDebateActivity::class.java))
        }
        return v
    }

    inner class DebatesAdapter: RecyclerView.Adapter<DebatesAdapter.ViewHolder>(){

        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            var topic: TextView
            var category: TextView
            var participants: LinearLayout
            var time: TextView
            var end: TextView

            init {
                topic = itemView.findViewById(R.id.card_topic)
                category = itemView.findViewById(R.id.card_category)
                participants = itemView.findViewById(R.id.card_participants)
                time = itemView.findViewById(R.id.card_time)
                end = itemView.findViewById(R.id.card_expiration)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.debate_card, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val debate = DebateBase.getDebate(position)
            holder.topic.text = debate.topic
            holder.category.text = debate.category
            val format = SimpleDateFormat("EEE, MMM d", Locale.US)
            holder.time.text = String.format(getString(R.string.last_activity), format.format(Date()))
            holder.end.text = if(debate is TimedDebate)
                                String.format(getString(R.string.end_date), format.format(Date()))
                              else
                                String.format(getString(R.string.end_date), "none")
        }

        override fun getItemCount(): Int {
            return DebateBase.getCount()
        }
    }
}
