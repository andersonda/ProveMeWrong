package com.danderson.provemewrong.utils

import android.support.v7.widget.RecyclerView
import jp.wasabeef.recyclerview.animators.FadeInAnimator
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator

object Animations {
    fun getUserAnimator(): RecyclerView.ItemAnimator{
        val animator = FadeInAnimator()
        animator.addDuration = 500
        animator.removeDuration = 500
        return animator
    }

    fun getDebateLineAnimator(): RecyclerView.ItemAnimator{
        val animator = SlideInLeftAnimator()
        animator.addDuration = 250
        animator.removeDuration = 250
        return animator
    }
}