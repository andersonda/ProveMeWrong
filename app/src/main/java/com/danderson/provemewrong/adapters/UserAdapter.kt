package com.danderson.provemewrong.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.danderson.provemewrong.R
import com.danderson.provemewrong.model.User
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Log


open class UserAdapter<T: User>: RecyclerView.Adapter<UserAdapter<T>.ViewHolder>() {

    var users: MutableList<T> = mutableListOf()

    open inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val picture: ImageView = itemView.findViewById(R.id.profile_picture)
        val name: TextView = itemView.findViewById(R.id.display_name)
        val layout: LinearLayout = itemView.findViewById(R.id.user_display)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.entry_user, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.name.text = user.displayName
        val default = TextDrawable.builder()
                .beginConfig()
                    .width(64)
                    .height(64)
                .endConfig()
                .buildRound("${user.displayName[0]}", ColorGenerator.MATERIAL.getColor(user.hashCode()))

        Picasso.get()
                .load(user.imageURL)
                .resize(64, 64)
                .placeholder(default)
                .into(holder.picture, object: Callback{
                    override fun onSuccess() {
                        val imageBitmap = (holder.picture.drawable as BitmapDrawable).bitmap
                        val imageDrawable = RoundedBitmapDrawableFactory.create(holder.itemView.resources, imageBitmap)
                        imageDrawable.isCircular = true
                        imageDrawable.cornerRadius = Math.max(imageBitmap.width, imageBitmap.height) / 2.0f
                        holder.picture.setImageDrawable(imageDrawable)
                    }

                    override fun onError(e: Exception?) {
                        holder.picture.setImageDrawable(default)
                    }
                })
    }

    override fun getItemCount(): Int {
        return users.size
    }
}