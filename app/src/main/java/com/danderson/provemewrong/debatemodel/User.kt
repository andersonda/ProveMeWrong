package com.danderson.provemewrong.debatemodel

open class User(var email: String = "", var displayName: String = "", var imageURL: String = "", var id: String = ""){

    override fun equals(other: Any?): Boolean {
        if(this === other)
            return true
        if(other !is User)
            return false
        // email is the only field check necessary, as emails are unique across users
        return(this.email == other.email)
    }
}