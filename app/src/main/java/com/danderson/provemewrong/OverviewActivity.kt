package com.danderson.provemewrong

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_overview.*

class OverviewActivity : AppCompatActivity() {

    val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)
        if(currentUser == null){
            startActivity(Intent(this, AuthenticationActivity::class.java))
            finish()
            return
        }

        configurePager()
    }

    override fun onResume() {
        super.onResume()
        viewPager.currentItem = 1
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.overview, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.sign_out -> {
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener{
                            if(it.isSuccessful){
                                startActivity(Intent(this@OverviewActivity, AuthenticationActivity::class.java))
                                finish()
                            } else{
                                // report error
                            }
                        }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Configures the ViewPager and BottomNavigationView to talk to each other
     */
    private fun configurePager(){
        val adapter = NavigationPagerAdapter(supportFragmentManager, 3)
        viewPager.adapter = adapter
        navigation.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.navigation_profile -> viewPager.currentItem = 0
                R.id.navigation_home -> viewPager.currentItem = 1
                R.id.navigation_search -> viewPager.currentItem = 2
                else -> viewPager.currentItem = 1
            }
            false
        }
        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                navigation.menu.getItem(position).isChecked = true
                supportActionBar?.title = when(position){
                    0 -> "Profile"
                    1 -> "My Debates"
                    2 -> "Browse"
                    else -> "My Debates"
                }
            }
        })
    }

    inner class NavigationPagerAdapter(fm: FragmentManager, private var tabCount: Int): FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment? {
            return when (position) {
                0 -> {
                    ProfileFragment.newInstance(currentUser!!.email!!, currentUser.displayName!!,
                            currentUser.photoUrl.toString())
                }
                1 -> OverviewFragment()
                2 -> BrowseFragment()
                else -> null
            }
        }

        override fun getCount(): Int {
            return tabCount
        }
    }
}
