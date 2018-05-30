package com.danderson.provemewrong.model

class DebateLine(val user:User = User(), val content: String = "", val time: String = "") {
    val likedBy = mutableListOf<String>()
    val references = mutableListOf<String>()
}