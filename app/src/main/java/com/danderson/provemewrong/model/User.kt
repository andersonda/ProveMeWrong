package com.danderson.provemewrong.model

open class User(var email: String = "", var displayName: String = "", var imageURL: String = "", var id: String = ""): Comparable<User>{
    var debates = mutableMapOf<String, Boolean>()

    override fun equals(other: Any?): Boolean {
        if(this === other)
            return true
        if(other !is User)
            return false
        // email is the only field check necessary, as emails are unique across users
        return(this.email == other.email)
    }

    override fun compareTo(other: User): Int {
        val thisParts = this.displayName.split(" ")
        val otherParts = other.displayName.split(" ")

        return if(thisParts.last() == otherParts.last()){
            thisParts.first().compareTo(otherParts.first())
        }
        else{
            thisParts.last().compareTo(otherParts.last())
        }
    }
}