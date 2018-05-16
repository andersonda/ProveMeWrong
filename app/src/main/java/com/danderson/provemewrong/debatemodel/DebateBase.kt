package com.danderson.provemewrong.debatemodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Collection of debates. This is a placeholder for testing. In the future, it will be linked to Firebase.
 */
object DebateBase {
    val formatter: SimpleDateFormat = SimpleDateFormat("EEE MMM dd, HH:mm", Locale.US)

    private var debates: ArrayList<Debate> = ArrayList()
    private val database = FirebaseDatabase.getInstance()

    fun add(debate: Debate, user:FirebaseUser){
        debates.add(debate)
        val debateReference = database.getReference("/debates/").push()
        debateReference.setValue(debate)
        val userReference = database.getReference("/user-debates/${user.uid}").push().setValue(debateReference.key)
    }

    fun getDebates(user:FirebaseUser, adapter:RecyclerView.Adapter<*>? = null): List<Debate>{
        val debates = mutableListOf<Debate>()
        val userDebateReference = database.getReference("/user-debates/${user.uid}")
        val userDebateChangeListener = object: ValueEventListener{

            override fun onCancelled(dbError: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(child in dataSnapshot.children){
                    val key = child.getValue(String::class.java)!!
                    val debateReference = database.getReference("/debates/$key")
                    debateReference.addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onCancelled(dbError: DatabaseError) {

                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var debate: Debate? = null
                            val topic = dataSnapshot.child("topic").getValue(String::class.java)!!
                            val category = dataSnapshot.child("category").getValue(String::class.java)!!
                            val turnBased = dataSnapshot.child("turnBased").getValue(Boolean::class.java)!!

                            debate = if(dataSnapshot.child("date").exists()){
                                val date = dataSnapshot.child("date").getValue(String::class.java)!!
                                TimedDebate(topic, category, turnBased, date)
                            } else{
                                Debate(topic, category, turnBased)
                            }
                            debates.add(debate)
                            adapter?.notifyDataSetChanged()
                        }
                    })
                }
            }

        }
        userDebateReference.addListenerForSingleValueEvent(userDebateChangeListener)
        return debates
    }

    fun getDebateCategories(): List<String>{
        val categories = mutableListOf<String>()
        val reference = database.getReference("/debate-categories")
        val categoryChangeListener = object: ValueEventListener{

            override fun onCancelled(dbError: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(child in dataSnapshot.children){
                    categories.add(child.getValue<String>(String::class.java)!!)
                }
            }

        }
        reference.addListenerForSingleValueEvent(categoryChangeListener)

        categories.add("Other") // hack to ensure "Other" category is always at the end of the list

        return categories
    }
}