package com.danderson.provemewrong.adapters

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.danderson.provemewrong.R
import com.danderson.provemewrong.activities.DebateActivity
import com.danderson.provemewrong.fragments.BottomSheetDebateDialog
import com.danderson.provemewrong.model.DebateLine
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class DebateLineAdapter(val context: Context): RecyclerView.Adapter<DebateLineAdapter.ViewHolder>() {

    var debateLines = mutableListOf<DebateLine>()
    val activity = (context as DebateActivity)

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val picture: ImageView = itemView.findViewById(R.id.profile_picture)
        val content: TextView = itemView.findViewById(R.id.debate_line_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = when(viewType){
            0 -> LayoutInflater.from(parent.context).inflate(R.layout.entry_incoming_debate_line, parent, false)
            else -> LayoutInflater.from(parent.context).inflate(R.layout.entry_outgoing_debate_line, parent, false)
        }
        return ViewHolder(v)
    }

    override fun getItemViewType(position: Int): Int {
        return when(debateLines[position].user.id){
            FirebaseAuth.getInstance().currentUser!!.uid -> 1
            else -> 0
        }
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
                .into(holder.picture, object : Callback {
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

        holder.itemView.setOnLongClickListener {
            val bottomSheet = BottomSheetDebateDialog.newInstance(debateLine)
            bottomSheet.show(activity.supportFragmentManager, bottomSheet.tag)

            true
        }
    }
}