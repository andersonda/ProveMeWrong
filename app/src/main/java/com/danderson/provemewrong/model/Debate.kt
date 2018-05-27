package com.danderson.provemewrong.model

import com.google.firebase.database.Exclude
import java.io.Serializable

open class Debate (val topic: String,
                   val category: String,
                   val isTurnBased: Boolean): Serializable{

    @Exclude
    var id: String? = null

    var isOpen = true

    var moderator: String? = null

    val participants = mutableListOf<String>()

    val debateLines = mutableListOf<DebateLine>()
}