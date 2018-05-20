package com.danderson.provemewrong.debatemodel

class Contact(email: String, displayName: String, imageURL: String, var type: ContactStatus) :
    User(email, displayName, imageURL){

    enum class ContactStatus{
        SENT, RECEIVED, ACCEPTED
    }
}