package com.danderson.provemewrong.adapters

import android.graphics.drawable.BitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.danderson.provemewrong.R
import com.danderson.provemewrong.model.DebateLine
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class DebateLineAdapter: RecyclerView.Adapter<DebateLineAdapter.ViewHolder>() {

    var debateLines = mutableListOf<DebateLine>()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val picture: ImageView = itemView.findViewById(R.id.profile_picture)
        val content: TextView = itemView.findViewById(R.id.debate_line_content)
        val likeButton: ImageButton = itemView.findViewById(R.id.like_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.entry_incoming_debate_line, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return debateLines.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val debateLine = debateLines[position]
        val default = TextDrawable.builder()
                .beginConfig()
                .width(64)
                .height(64)
                .endConfig()
                .buildRound("${debateLine.user.displayName[0]}", ColorGenerator.MATERIAL
                        .getColor(debateLine.user.hashCode()))
        Picasso.get()
                .load(debateLine.user.imageURL)
                .resize(32, 32)
                .placeholder(R.drawable.ic_profile)
                .into(holder.picture, object: Callback {
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
        holder.content.text = debateLine.content
    }
}