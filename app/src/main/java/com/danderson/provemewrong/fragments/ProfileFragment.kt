package com.danderson.provemewrong.fragments


import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.danderson.provemewrong.R
import com.danderson.provemewrong.adapters.ContactAdapter
import com.danderson.provemewrong.model.DebateBase
import com.danderson.provemewrong.model.User
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // layout items for profile card
        val email = view.findViewById<TextView>(R.id.email)
        val name = view.findViewById<TextView>(R.id.name)
        val photo = view.findViewById<ImageView>(R.id.profile_picture)
        val args = arguments!!
        val user = args.getSerializable(ARG_USER) as User

        email.text = user.email
        name.text = user.displayName
        val default = TextDrawable.builder().buildRound("${name.text[0]}", ColorGenerator.MATERIAL.getColor(email.hashCode()))

        Picasso.get()
                .load(user.imageURL)
                .resize(512, 512)
                .placeholder(default)
                .into(photo, object: Callback {
                    override fun onSuccess() {
                        val imageBitmap = (photo.drawable as BitmapDrawable).bitmap
                        val imageDrawable = RoundedBitmapDrawableFactory.create(resources, imageBitmap)
                        imageDrawable.isCircular = true
                        imageDrawable.cornerRadius = Math.max(imageBitmap.width, imageBitmap.height) / 2.0f
                        photo.setImageDrawable(imageDrawable)
                    }

                    override fun onError(e: Exception?) {
                        photo.setImageDrawable(default)
                    }
                })

        // layout items for contacts card
        val contactsAdapter = ContactAdapter(false, context!!)
        val pendingAdapter = ContactAdapter(true, context!!)

        val contacts = DebateBase.getContactsForUser(user.id, contactsAdapter, pendingAdapter)
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
                    .setNegativeButton(R.string.cancel_request, { _, _ -> })
                    .setPositiveButton(R.string.send,{ _, _ ->
                        DebateBase.addContact(user.id, emailText.text.toString())
                    })
                    .setView(dialogView)
                    .show()
        }

        return view
    }

    companion object {
        const val ARG_USER = "user"

        @JvmStatic
        fun newInstance(user: User): ProfileFragment {
            val args = Bundle()
            args.putSerializable(ARG_USER, user)
            val fragment = ProfileFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
