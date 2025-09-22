package ru.netology.nmedia.ui

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R

class AppActivity : AppCompatActivity(R.layout.activity_app) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = findViewById<StatsView>(R.id.stats)
        view.data = listOf(
            500F,
            500F,
            500F,
            500F,
        )
        val textView = findViewById<TextView>(R.id.label)
        val drawingAnimation = StatsView.DrawingProgressAnimation(view).apply {
            duration = 4000
            interpolator = LinearInterpolator()
        }
        val animationSet = AnimationSet(true).apply {
            addAnimation(AnimationUtils.loadAnimation(this@AppActivity, R.anim.animation))
            addAnimation(drawingAnimation)
        }

        view.startAnimation(animationSet.apply {
                setAnimationListener(object: Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        textView.text = ("onAnimationStart")
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        textView.text = ("onAnimationEnd")
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                        textView.text = ("onAnimationRepeat")
                    }
                })
            }
        )
    }
}