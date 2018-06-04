package com.danderson.provemewrong.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.view.Gravity
import android.widget.Toast
import com.danderson.provemewrong.fragments.AddParticipantsFragment
import com.danderson.provemewrong.fragments.CreateDebateFragment
import com.danderson.provemewrong.Navigation
import com.danderson.provemewrong.R
import com.danderson.provemewrong.model.Debate
import com.danderson.provemewrong.model.DebateBase
import com.danderson.provemewrong.model.TimedDebate
import com.danderson.provemewrong.model.User
import kotlinx.android.synthetic.main.activity_create_debate.*

class CreateDebateActivity : AppCompatActivity(), Navigation, CreateDebateFragment.DebateCreation,
        AddParticipantsFragment.AddParticipants {

    var debate: Debate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_debate)

        configurePager()
    }

    private fun configurePager(){
        val adapter = CreateDebatePagerAdapter(supportFragmentManager, 2)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                supportActionBar?.title = when(position){
                    0 -> "Create Debate"
                    1 -> "Add Participants"
                    else -> ""
                }
            }
        })
    }

    override fun onBackButtonPressed() {
        when(viewPager.currentItem){
            0 -> {
               AlertDialog.Builder(this)
                        .setTitle(R.string.cancel_debate_creation)
                        .setNegativeButton(R.string.no, { _, _ -> })
                        .setPositiveButton(R.string.yes,{ _, _ ->
                            startActivity(Intent(this, OverviewActivity::class.java))
                            finish()
                        })
                        .show()
            }
            1 -> {
                viewPager.currentItem = 0
            }
        }
    }

    override fun onNextButtonPressed() {
        when(viewPager.currentItem){
            0 -> {
                viewPager.currentItem = 1
                val toast = Toast.makeText(this, getString(R.string.tip_set_moderator), Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
            1 -> {
                AlertDialog.Builder(this)
                        .setTitle(R.string.submit_debate)
                        .setNegativeButton(R.string.no, { _, _ -> })
                        .setPositiveButton(R.string.yes,{ _, _ ->
                            DebateBase.addDebate(debate!!)
                            startActivity(Intent(this, OverviewActivity::class.java))
                            finish()
                        })
                        .show()
            }
        }
    }

    override fun onDebateInformationSubmitted(topic: String, category: String, isVote: Boolean, isTurnBased: Boolean, date: String?) {
        debate = if(isVote){
            Debate(topic, category, isTurnBased)
        } else{
            TimedDebate(topic, category, isTurnBased, date!!)
        }
    }

    override fun onParticipantsAdded(users: List<User>, moderatorIndex: Int) {
        users.forEach{
            debate!!.participants.add(it.id)
        }
        debate!!.moderator = users[moderatorIndex].id
    }

    inner class CreateDebatePagerAdapter(fm: FragmentManager, private var tabCount: Int): FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment? {
            return when (position) {
                0 -> CreateDebateFragment()
                1 -> AddParticipantsFragment()
                else -> null
            }
        }

        override fun getCount(): Int {
            return tabCount
        }
    }

}
