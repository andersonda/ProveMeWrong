package com.danderson.provemewrong.debatemodel

import com.google.firebase.database.Exclude

open class Debate (val topic: String,
                   val category: String,
                   val isTurnBased: Boolean){
}