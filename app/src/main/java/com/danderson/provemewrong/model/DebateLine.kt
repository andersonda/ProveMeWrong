package com.danderson.provemewrong.model

import java.io.Serializable

class DebateLine(val id: String = "", val user:User = User(), var content: String = "", val time: String = "",
                 var isEdited: Boolean = false): Serializable {
    val likedBy = mutableMapOf<String, String>()

    override fun equals(other: Any?): Boolean {
        if(this === other)
            return true
        if(other !is DebateLine)
            return false
        // email is the only field check necessary, as emails are unique across users
        return(this.id == other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}