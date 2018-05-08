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
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_overview.*
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
        private val topics = arrayOf("Topic 1", "Topic 2", "Topic 3", "Topic 4", "Topic 5", "Topic 6", "Topic 7", "Topic 8", "Topic 9", "Topic 10", "Topic 11", "Topic 12", "Topic 13", "Topic 14", "Topic 15")
        private val participants = arrayOf("Donald Trump", "Bernie Sanders", "Hillary Clinton", "Alex", "Barack Obama")

        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            var topic: TextView
            var participants: TextView
            var time: TextView

            init {
                topic = itemView.findViewById(R.id.topic)
                participants = itemView.findViewById(R.id.participants)
                time = itemView.findViewById(R.id.time)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.debate_card, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.topic.text = topics[position]
            holder.participants.text = participants[position % 5]
            val dateformat:SimpleDateFormat = SimpleDateFormat("EEE MMM dd, HH:mm", Locale.US)
            holder.time.text = dateformat.format(Date())
        }

        override fun getItemCount(): Int {
            return topics.size
        }
    }
}
