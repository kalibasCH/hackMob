package com.ucrconductors.view

import android.animation.ObjectAnimator
import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.ucrconductors.R
import kotlinx.coroutines.delay

object Animation {

    suspend fun runVectorsAnim(vararg images: ImageView) {
        images.forEach {
            val objectAnimator = ObjectAnimator.ofFloat(it, "rotationX", 0f, 360f)
            objectAnimator.repeatCount = Animation.INFINITE
            objectAnimator.duration = 4000
            objectAnimator.interpolator = LinearInterpolator()
            objectAnimator.repeatMode = ObjectAnimator.RESTART
            objectAnimator.start()
            delay(450)
        }
    }

    fun rotateXmlAnimationNfcProgress(context: Context, imageView: ImageView) {
        val rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_animation)
        imageView.startAnimation(rotateAnimation)
    }
}