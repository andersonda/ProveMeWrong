package com.danderson.provemewrong.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

open class User(var email: String = "", var displayName: String = "", var imageURL: String = "",
                var id: String = ""): Comparable<User>, Serializable{

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

    override fun hashCode(): Int {
        var result = email.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + imageURL.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + debates.hashCode()
        return result
    }
}