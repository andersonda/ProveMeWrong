package com.danderson.provemewrong

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import kotlinx.android.synthetic.main.activity_create_debate.*

class CreateDebateActivity : AppCompatActivity(), Navigation{

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
                        .setNegativeButton(R.string.no, {_,_ -> })
                        .setPositiveButton(R.string.yes,{_,_ ->
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
            }
        }
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
