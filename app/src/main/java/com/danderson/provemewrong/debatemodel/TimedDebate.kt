package com.danderson.provemewrong.debatemodel

import java.util.*

class TimedDebate(topic: String, category: String, isTurnBased:Boolean): Debate(topic, category, isTurnBased) {
    var expiration: Date? = null
}