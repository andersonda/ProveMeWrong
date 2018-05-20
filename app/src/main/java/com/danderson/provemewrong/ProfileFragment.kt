package com.danderson.provemewrong


import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.media.Image
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.danderson.provemewrong.debatemodel.DebateBase
import com.danderson.provemewrong.debatemodel.User
import com.squareup.picasso.Picasso
import java.io.InputStream
import java.lang.ref.WeakReference

class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // layout items for profile card
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

        // layout items for contacts card
        val contactsAdapter = ContactAdapter(false)
        val pendingAdapter = ContactAdapter(true)

        val contacts = DebateBase.getContactsForUser(args.getString(ARG_UID), contactsAdapter, pendingAdapter)
        contactsAdapter.users = contacts.accepted
        pendingAdapter.users = contacts.pending

        val pendingLayoutManager = LinearLayoutManager(activity)
        val pendingContacts = view.findViewById<RecyclerView>(R.id.recycler_pending_contacts)
        pendingContacts.adapter = pendingAdapter
        pendingContacts.layoutManager = pendingLayoutManager

        val currentLayoutManager = LinearLayoutManager(activity)
        val currentContacts = view.findViewById<RecyclerView>(R.id.recycler_contacts)
        currentContacts.adapter = contactsAdapter
        currentContacts.layoutManager = currentLayoutManager


        val addContact = view.findViewById<ImageButton>(R.id.add_contact)
        addContact.setOnClickListener{
            val dialogView = inflater.inflate(R.layout.dialog_new_contact, null)
            val emailText: EditText = dialogView.findViewById(R.id.contact_email)
            val dialog = AlertDialog.Builder(context!!)
                    .setTitle(R.string.dialog_add_contact)
                    .setNegativeButton(R.string.cancel, {_,_ -> })
                    .setPositiveButton(R.string.send,{_,_ ->
                        DebateBase.addContact(args.getString(ARG_UID), emailText.text.toString())
                    })
                    .setView(dialogView)
                    .show()
        }

        return view
    }

    companion object {
        const val ARG_EMAIL = "profile_email"
        const val ARG_DISPLAY = "profile_display"
        const val ARG_IMAGE = "profile_image_url"
        const val ARG_UID = "profile_user_id"

        @JvmStatic
        fun newInstance(userEmail: String, userName: String, userPhoto: String, uid: String): ProfileFragment{
            val args = Bundle()
            args.putString(ARG_EMAIL, userEmail)
            args.putString(ARG_DISPLAY, userName)
            args.putString(ARG_IMAGE, userPhoto)
            args.putString(ARG_UID, uid)
            val fragment = ProfileFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
