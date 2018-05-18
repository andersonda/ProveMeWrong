package com.danderson.provemewrong


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.danderson.provemewrong.debatemodel.DebateBase
import com.squareup.picasso.Picasso
import java.io.InputStream
import java.lang.ref.WeakReference

class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val email = view.findViewById<TextView>(R.id.email)
        val name = view.findViewById<TextView>(R.id.name)
        val photo = view.findViewById<ImageView>(R.id.profile_picture)
        val args = arguments!!
        email.text = args.getString(ARG_EMAIL)
        name.text = args.getString(ARG_DISPLAY)
        Picasso.get()
                .load(args.getString(ARG_IMAGE))
                .resize(512, 512)
                .into(photo)
        return view
    }

    companion object {
        const val ARG_EMAIL = "profile_email"
        const val ARG_DISPLAY = "profile_display"
        const val ARG_IMAGE = "profile_image_url"

        @JvmStatic
        fun newInstance(userEmail: String, userName: String, userPhoto: String): ProfileFragment{
            val args = Bundle()
            args.putString(ARG_EMAIL, userEmail)
            args.putString(ARG_DISPLAY, userName)
            args.putString(ARG_IMAGE, userPhoto)
            val fragment = ProfileFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
