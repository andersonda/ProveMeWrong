package com.danderson.provemewrong


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val email = view.findViewById<TextView>(R.id.email)
        val name = view.findViewById<TextView>(R.id.name)
        val args = arguments!!
        email.text = args.getString(ARG_EMAIL)
        name.text = args.getString(ARG_DISPLAY)
        return view
    }

    companion object {
        const val ARG_EMAIL = "profile_email"
        const val ARG_DISPLAY = "profile_display"

        fun newInstance(userEmail: String, userName: String): ProfileFragment{
            val args: Bundle = Bundle()
            args.putString(ARG_EMAIL, userEmail)
            args.putCharSequence(ARG_DISPLAY, userName)

            val fragment = ProfileFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
