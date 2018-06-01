package com.danderson.provemewrong.model

import java.io.Serializable

class DebateLine(val id: String = "", val user:User = User(), val content: String = "", val time: String = ""): Serializable {
    val likedBy = mutableListOf<String>()
}