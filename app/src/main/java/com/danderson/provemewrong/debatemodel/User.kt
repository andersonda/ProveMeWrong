package com.danderson.provemewrong.debatemodel

open class User(var email: String = "", var displayName: String = "", var imageURL: String = "", var id: String = ""){

    var debates = mutableMapOf<String, Boolean>()

    override fun equals(other: Any?): Boolean {
        if(this === other)
            return true
        if(other !is User)
            return false
        // email is the only field check necessary, as emails are unique across users
        return(this.email == other.email)
    }
}