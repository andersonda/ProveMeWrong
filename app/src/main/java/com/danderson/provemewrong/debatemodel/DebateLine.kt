package com.danderson.provemewrong.debatemodel

class DebateLine(val user: User, val content: String, val time: String) {
    val likedBy = mutableListOf<String>()
    val references = mutableListOf<String>()
}