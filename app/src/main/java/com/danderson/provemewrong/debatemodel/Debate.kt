package com.danderson.provemewrong.debatemodel

import com.google.firebase.database.Exclude

open class Debate (val topic: String,
                   val category: String,
                   val isTurnBased: Boolean){

    @Exclude
    var id: String? = null

    var isOpen = true

    var moderator: String? = null

    val participants = mutableListOf<String>()

    val debateLines = mutableListOf<DebateLine>()
}