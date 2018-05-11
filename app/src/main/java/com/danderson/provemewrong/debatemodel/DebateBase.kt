package com.danderson.provemewrong.debatemodel

/**
 * Collection of debates. This is a placeholder for testing. In the future, it will be linked to Firebase.
 */
object DebateBase {
    private var debates: ArrayList<Debate> = ArrayList()
    var categories = arrayListOf<String>("Economics", "Foreign Relations", "Social", "Entertainment", "Other")

    fun add(debate: Debate){
        debates.add(debate)
    }

    fun getDebate(position: Int): Debate{
        return debates[position]
    }

    fun getCount(): Int{
        return debates.size
    }
}