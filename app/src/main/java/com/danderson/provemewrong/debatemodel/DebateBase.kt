package com.danderson.provemewrong.debatemodel

import android.support.v7.widget.RecyclerView
import android.util.Log
import com.danderson.provemewrong.adapters.ContactAdapter
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

    fun getUser(uid: String): MutableList<User>{
        val userReference = database.getReference("/users/$uid")
        val users = mutableListOf<User>()
        userReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(dbError: DatabaseError?) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                users.add(dataSnapshot.getValue(User::class.java)!!)
            }
        })
        return users
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
        database.getReference("/contacts/$uid/$contact").removeValue()
        database.getReference("/contacts/$contact/$uid").removeValue()
    }

    fun acceptContact(uid: String, contact: String){
        database.getReference("/contacts/$uid/$contact").setValue(Contact.ContactStatus.ACCEPTED)
        database.getReference("/contacts/$contact/$uid").setValue(Contact.ContactStatus.ACCEPTED)
    }

    class Contacts{
        val pending = mutableListOf<Contact>()
        val accepted = mutableListOf<Contact>()
    }


    /**
     * gets contacts for current user. if @param pendingAdapter is null,
     * only gets the contacts that have accepted sent requests. otherwise,
     * the pending requests are included
     */
    fun getContactsForUser(uid: String, contactsAdapter: ContactAdapter,
                           pendingAdapter: ContactAdapter?): Contacts{
        val contacts = Contacts()
        val contactReference = database.getReference("/contacts/$uid")
        contactReference.addChildEventListener(object: ChildEventListener{
            override fun onCancelled(dbError: DatabaseError?) {

            }

            override fun onChildMoved(child: DataSnapshot, p1: String?) {
            }

            /**
             * recipient of contact request accepts request
             */
            override fun onChildChanged(child: DataSnapshot, p1: String?) {
                val userReference = database.getReference("/users/${child.key}")
                userReference.addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(dbError: DatabaseError) {

                    }
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val contact = getContact(child, dataSnapshot)
                        if(contacts.pending.contains(contact)){
                            contacts.pending.remove(contact)
                            insertSorted(contacts.accepted, contact)
                            pendingAdapter?.notifyDataSetChanged()
                            contactsAdapter.notifyDataSetChanged()
                        }
                    }
                })
            }

            /**
             * new contact request is submitted by current user
             */
            override fun onChildAdded(child: DataSnapshot, p1: String?) {
                val userReference = database.getReference("/users/${child.key}")
                userReference.addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(dbError: DatabaseError) {
                    }
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val contact = getContact(child, dataSnapshot)
                        if(contact.type == Contact.ContactStatus.ACCEPTED){
                            insertSorted(contacts.accepted, contact)
                            contactsAdapter.notifyDataSetChanged()
                        }
                        else{
                            insertSorted(contacts.pending, contact)
                            pendingAdapter?.notifyDataSetChanged()
                        }
                    }
                })
            }

            /**
             * contact is removed or contact request is rejected
             */
            override fun onChildRemoved(child: DataSnapshot) {
                val userReference = database.getReference("/users/${child.key}")
                userReference.addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(dbError: DatabaseError) {
                    }
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val contact = getContact(child, dataSnapshot)
                        if(contacts.pending.contains(contact)){
                            contacts.pending.remove(contact)
                            pendingAdapter?.notifyDataSetChanged()
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

    fun addDebate(debate: Debate, user:FirebaseUser){
        debates.add(debate)
        val debateReference = database.getReference("/debates/").push()
        debateReference.setValue(debate)

        debate.participants.forEach {
            database.getReference("/users/$it/debates/${debateReference.key}").setValue(true)
        }
    }

    fun getDebates(user:FirebaseUser, adapter:RecyclerView.Adapter<*>? = null): MutableList<Debate>{
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

    fun getParticipantsForDebate(id: String, adapter:RecyclerView.Adapter<*>): MutableList<User>{
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
                            insertSorted(participants, dataSnapshot.getValue(User::class.java)!!)
                            adapter.notifyDataSetChanged()
                        }
                    })
                }
            }

        })
        return participants
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

    private fun <T: Comparable<T>> insertSorted(items: MutableList<T>, item: T){
        val index = items.binarySearch(item)
        if(index < 0){
            items.add(-(index + 1), item)
        }
        else{
            items.add(index, item)
        }
    }
}