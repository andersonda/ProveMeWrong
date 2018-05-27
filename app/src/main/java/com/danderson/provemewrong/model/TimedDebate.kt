package com.danderson.provemewrong.model

class TimedDebate(topic: String, category: String, isTurnBased:Boolean,
                  val date: String): Debate(topic, category, isTurnBased) {

}