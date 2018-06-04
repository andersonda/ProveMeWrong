package com.danderson.provemewrong.model

import android.support.v7.widget.RecyclerView
import android.util.Log
import com.danderson.provemewrong.adapters.ContactAdapter
import com.danderson.provemewrong.adapters.UserAdapter
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Collection of debates. This is a placeholder for testing. In the future, it will be linked to Firebase.
 */
object DebateBase {
    val formatter: SimpleDateFormat = SimpleDateFormat("EEE MMM dd, HH:mm", Locale.US)
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
                addContact(uid, dataSnapshot)
            }
        })
    }

    private fun addContact(uid: String, child: DataSnapshot){
        // add contacts reference for sender
        val contactReference = database.getReference("/contacts")
        contactReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(dbError: DatabaseError?) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // add contacts entry for sender
                if(!dataSnapshot.child("$uid/${child.key}").exists()){
                    contactReference.child("$uid/${child.key}").setValue(Contact.ContactStatus.SENT)
                }
                // add contacts entry for receiver
                if(!dataSnapshot.child("${child.key}/$uid").exists()){
                    contactReference.child("${child.key}/$uid").setValue(Contact.ContactStatus.RECEIVED)
                }
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
                Log.i("CHILDMOVED", "child_moved")
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
                            val pendingIndex = contacts.pending.indexOf(contact)
                            contacts.pending.remove(contact)
                            insertSorted(contacts.accepted, contact)
                            pendingAdapter?.notifyItemRemoved(pendingIndex)
                            contactsAdapter.notifyItemInserted(contacts.accepted.indexOf(contact))
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
                            contactsAdapter.notifyItemInserted(contacts.accepted.indexOf(contact))
                        }
                        else{
                            insertSorted(contacts.pending, contact)
                            pendingAdapter?.notifyItemInserted(contacts.pending.indexOf(contact))
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
                            val pendingIndex = contacts.pending.indexOf(contact)
                            contacts.pending.remove(contact)
                            pendingAdapter?.notifyItemRemoved(pendingIndex)
                        }
                        else{
                            val acceptedIndex = contacts.accepted.indexOf(contact)
                            contacts.accepted.remove(contact)
                            contactsAdapter.notifyItemRemoved(acceptedIndex)
                        }
                    }
                })
            }
        })
        return contacts
    }

    @Suppress("NAME_SHADOWING")
    private fun getContact(contactType: DataSnapshot, user: DataSnapshot): Contact{
        val user = user.getValue(User::class.java)!!
        val contactType = contactType.getValue(Contact.ContactStatus::class.java)!!
        val contact = Contact(user.email, user.displayName, user.imageURL, contactType)
        contact.id = user.id
        return contact
    }

    fun addDebate(debate: Debate){
        debates.add(debate)
        val debateReference = database.getReference("/debates/").push()
        debateReference.setValue(debate)

        debate.participants.forEach {
            database.getReference("/users/$it/debates/${debateReference.key}").setValue(true)
        }
    }

    fun getDebates(user:String, adapter:RecyclerView.Adapter<*>): MutableList<Debate>{
        val debates = mutableListOf<Debate>()
        val userDebateReference = database.getReference("/users/$user/debates")
        userDebateReference.addChildEventListener(object: ChildEventListener{
            override fun onCancelled(dbError: DatabaseError?) {

            }

            override fun onChildMoved(child: DataSnapshot, p1: String?) {
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(child: DataSnapshot, p1: String?) {
                adapter.notifyDataSetChanged()
            }

            override fun onChildAdded(child: DataSnapshot, p1: String?) {
                val key = child.key
                Log.i("KEY", key)
                val debateReference = database.getReference("/debates/$key")
                debateReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(dbError: DatabaseError) {
                    }
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val topic = dataSnapshot.child("topic").getValue(String::class.java)!!
                        val category = dataSnapshot.child("category").getValue(String::class.java)!!
                        val turnBased = dataSnapshot.child("turnBased").getValue(Boolean::class.java)!!
                        val debate = if (dataSnapshot.child("date").exists()) {
                            val date = dataSnapshot.child("date").getValue(String::class.java)!!
                            TimedDebate(topic, category, turnBased, date)
                        } else {
                            Debate(topic, category, turnBased)
                        }
                        debate.id = key
                        debate.moderator = dataSnapshot.child("moderator").getValue(String::class.java)!!
                        debates.add(debate)
                        adapter.notifyDataSetChanged()
                    }
                })
            }

            override fun onChildRemoved(child: DataSnapshot) {
                adapter.notifyDataSetChanged()
            }

        })
        return debates
    }

    fun <P: User, T: UserAdapter<P>> getParticipantsForDebate(id: String, moderator: String, adapter: T): MutableList<User>{
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
                            val user = dataSnapshot.getValue(User::class.java)!!
                            val index = insertSorted(participants, user)
                            if(user.id == moderator){
                                adapter.moderatorIndex = index
                            }
                            adapter.notifyDataSetChanged()
                        }
                    })
                }
            }

        })
        return participants
    }

    @Suppress("NAME_SHADOWING")
    fun addDebateLine(debate: Debate, debateLine: DebateLine){
        val reference = database.getReference("/debates/${debate.id}/lines").push()
        val debateLine = DebateLine(reference.key, debateLine.user, debateLine.content, debateLine.time)
        reference.setValue(debateLine)
    }

    fun removeDebateLine(debate: Debate, debateLine: DebateLine){
        database.getReference("/debates/${debate.id}/lines/${debateLine.id}").removeValue()
    }

    fun editDebateLine(debate: Debate, debateLine: DebateLine, newContent: String){
        debateLine.isEdited = true
        debateLine.content = newContent
        database.getReference("/debates/${debate.id}/lines/${debateLine.id}")
                .setValue(debateLine)
    }

    fun likeDebateLine(debate: Debate, debateLine: DebateLine, name: String){
        database.getReference("/debates/${debate.id}/lines/${debateLine.id}/likedBy").push()
                .setValue(name)
    }

    fun getLinesForDebate(debate: Debate, adapter: RecyclerView.Adapter<*>): MutableList<DebateLine>{

        val lines = mutableListOf<DebateLine>()

        val lineReference = database.getReference("debates/${debate.id}/lines")
        lineReference.addChildEventListener(object : ChildEventListener{
            override fun onCancelled(dbError: DatabaseError) {

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                val debateLine = dataSnapshot.getValue(DebateLine::class.java)!!
                val index = lines.indexOf(debateLine)
                lines[index] = debateLine
                adapter.notifyItemChanged(index)
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                lines.add(dataSnapshot.getValue(DebateLine::class.java)!!)
                adapter.notifyItemInserted(adapter.itemCount - 1)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val line = dataSnapshot.getValue(DebateLine::class.java)
                val index = lines.indexOf(line)
                lines.removeAt(index)
                adapter.notifyItemRemoved(index)
            }
        })

        return lines
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

    private fun <T: Comparable<T>> insertSorted(items: MutableList<T>, item: T): Int{
        var index = items.binarySearch(item)
        if(index < 0){
            index = -(index + 1)
        }
        items.add(index, item)
        return index
    }
}