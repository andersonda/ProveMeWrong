package com.danderson.provemewrong.fragments



import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.danderson.provemewrong.R
import com.danderson.provemewrong.activities.CreateDebateActivity
import com.danderson.provemewrong.adapters.DebatesAdapter

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
        adapter = DebatesAdapter(context!!)
        debates.adapter = adapter

        fab.setOnClickListener{
            startActivity(Intent(activity, CreateDebateActivity::class.java))
        }

        return v
    }
}
