package com.danderson.provemewrong.model

class Contact(email: String, displayName: String, imageURL: String, var type: ContactStatus) :
    User(email, displayName, imageURL){

    /**
     * Constants for contacts.
     *      SENT     -> contact request has been sent by the current user but not yet accepted by the receiver
     *      RECEIVED -> contact request has been received by the current user, but they have not yet accepted it
     *      ACCEPTED -> contact request has been accepted by recipient
     */
    enum class ContactStatus{
        SENT, RECEIVED, ACCEPTED
    }
}