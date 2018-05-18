package com.danderson.provemewrong.debatemodel

class DebateLine(val user: String, val content: String) {

    val likedBy = mutableListOf<String>()
    val references = mutableListOf<String>()
}