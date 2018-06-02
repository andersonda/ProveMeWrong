package com.danderson.provemewrong.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.danderson.provemewrong.R
import com.danderson.provemewrong.model.DebateBase
import com.danderson.provemewrong.model.User
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class ViewProfileDialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val v = activity!!.layoutInflater.inflate(R.layout.dialog_view_profile, null)
        val otherUser = arguments!!.getSerializable(ARG_OTHER_USER) as User
        val currentUser = arguments!!.getSerializable(ARG_CURRENT_USER) as User
        val email = v.findViewById<TextView>(R.id.email)
        val name = v.findViewById<TextView>(R.id.name)
        val photo = v.findViewById<ImageView>(R.id.profile_picture)
        val editProfile = v.findViewById<ImageButton>(R.id.edit_profile)
        val addContact = v.findViewById<Button>(R.id.add_contact)

        editProfile.visibility = View.GONE
        editProfile.isEnabled = false

        email.text = otherUser.email
        name.text = otherUser.displayName
        val default = TextDrawable.builder()
                .beginConfig()
                .width(256)
                .height(256)
                .endConfig()
                .buildRound("${otherUser.displayName[0]}", ColorGenerator.MATERIAL.getColor(otherUser.hashCode()))

        Picasso.get()
                .load(otherUser.imageURL)
                .resize(256, 256)
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

        addContact.setOnClickListener{
            DebateBase.addContact(currentUser.id, otherUser.email)
            this.dismiss()
        }

        return AlertDialog.Builder(activity).setView(v).create()
    }

    companion object {
        const val ARG_OTHER_USER = "other_user"
        const val ARG_CURRENT_USER = "current_user"

        @JvmStatic
        fun newInstance(currentUser: User, otherUser: User): ViewProfileDialog {
            val args = Bundle()
            args.putSerializable(ARG_CURRENT_USER, currentUser)
            args.putSerializable(ARG_OTHER_USER, otherUser)
            val fragment = ViewProfileDialog()
            fragment.arguments = args
            return fragment
        }
    }
}