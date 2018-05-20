package com.danderson.provemewrong.debatemodel

import android.support.v7.widget.RecyclerView
import android.util.Log
import com.danderson.provemewrong.ContactAdapter
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Collection of debates. This is a placeholder for testing. In the future, it will be linked to Firebase.
 */
object DebateBase {
    val formatter: SimpleDateFormat = SimpleDateFormat("EEE MMM dd, HH:mm", Locale.US)

    /**
     * Constants for contacts.
     *      SENT     -> contact request has been sent by the current user but not yet accepted by the receiver
     *      RECEIVED -> contact request has been received by the current user, but they have not yet accepted it
     *      ACCEPTED -> contact request has been accepted by recipient
     */

    private var debates: ArrayList<Debate> = ArrayList()
    private val database = FirebaseDatabase.getInstance()

    fun addDebate(debate: Debate, user:FirebaseUser){
        debates.add(debate)
        val debateReference = database.getReference("/debates/").push()
        debateReference.setValue(debate)
        val userReference = database.getReference("/users/${user.uid}/debates/${debateReference.key}").setValue(true)
    }

    fun addContact(uid: String, contactEmail: String){
        val userReference = database.getReference("/users")
        val query: Query = userReference.orderByChild("email").equalTo(contactEmail)
        query.addChildEventListener(object: ChildEventListener{
            override fun onCancelled(dbError: DatabaseError) {

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                // add contacts reference for sender
                var contactReference = database.getReference("/contacts/$uid/${dataSnapshot.key}")
                contactReference.setValue(Contact.ContactStatus.SENT)

                // add contacts reference for receiver
                contactReference = database.getReference("/contacts/${dataSnapshot.key}/$uid")
                contactReference.setValue(Contact.ContactStatus.RECEIVED)
            }
        })
    }

    fun removeContact(uid:String, contact:String){
        val userReference = database.getReference("/contacts/$uid/$contact").removeValue()
        val contactReference = database.getReference("/contacts/$contact/$uid").removeValue()
    }

    fun getDebates(user:FirebaseUser, adapter:RecyclerView.Adapter<*>? = null): List<Debate>{
        val debates = mutableListOf<Debate>()
        val userDebateReference = database.getReference("/users/${user.uid}/debates")
        val userDebateChangeListener = object: ValueEventListener{

            override fun onCancelled(dbError: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(child in dataSnapshot.children){

                    val key = child.key
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
                            debate.id = key
                            debate.moderator = if(dataSnapshot.child("moderator").exists())
                                                    dataSnapshot.child("moderator").getValue(String::class.java)
                                                else null
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

    fun getParticipantsForDebate(id: String, adapter:RecyclerView.Adapter<*>): List<User>{
        val participants = mutableListOf<User>()
        val debateReference = database.getReference("/debates/$id/participants")
        debateReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(dbError: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(child in dataSnapshot.children){
                    val userReference = database.getReference("/users/${child.getValue(String::class.java)}")
                    userReference.addListenerForSingleValueEvent(object:ValueEventListener{
                        override fun onCancelled(dbError: DatabaseError) {

                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            participants.add(dataSnapshot.getValue(User::class.java)!!)
                            adapter.notifyDataSetChanged()
                        }
                    })
                }
            }

        })
        return participants
    }

    class Contacts{
        val pending = mutableListOf<Contact>()
        val accepted = mutableListOf<Contact>()
    }

    fun getContactsForUser(uid: String, contactsAdapter:ContactAdapter,
                           pendingAdapter:ContactAdapter): Contacts{
        val contacts = Contacts()
        val contactReference = database.getReference("/contacts/$uid")
        contactReference.addChildEventListener(object: ChildEventListener{
            override fun onCancelled(dbError: DatabaseError?) {

            }

            override fun onChildMoved(child: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(child: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(child: DataSnapshot, p1: String?) {
                val userReference = database.getReference("/users/${child.key}")
                userReference.addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(dbError: DatabaseError) {
                    }
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val contact = getContact(child, dataSnapshot)
                        if(contact.type == Contact.ContactStatus.ACCEPTED){
                            contacts.accepted.add(contact)
                            contactsAdapter.notifyDataSetChanged()
                        }
                        else{
                            contacts.pending.add(contact)
                            pendingAdapter.notifyDataSetChanged()
                        }
                    }
                })
            }

            override fun onChildRemoved(child: DataSnapshot) {
                val userReference = database.getReference("/users/${child.key}")
                userReference.addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(dbError: DatabaseError) {
                    }
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val contact = getContact(child, dataSnapshot)
                        if(contacts.pending.contains(contact)){
                            contacts.pending.remove(contact)
                            pendingAdapter.notifyDataSetChanged()
                        }
                        else{
                            contacts.accepted.remove(contact)
                            contactsAdapter.notifyDataSetChanged()
                        }
                    }
                })
            }
        })
        return contacts
    }

    private fun getContact(contactType: DataSnapshot, user: DataSnapshot): Contact{
        val user = user.getValue(User::class.java)!!
        val contactType = contactType.getValue(Contact.ContactStatus::class.java)!!
        val contact = Contact(user.email, user.displayName, user.imageURL, contactType)
        contact.id = user.id
        return contact
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