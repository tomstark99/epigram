package com.example.epigram.ui.splash

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.epigram.ui.main.MainActivity
import com.example.epigram.R
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        setContentView(R.layout.activity_splash_screen)
        splash_left_logo.post {
            val layoutR = splash_right_logo.layoutParams

            val screenWidth = splash_root.width.toDouble()
            val logoRWidth = (screenWidth - (splash_root.paddingRight + splash_root.paddingLeft)) * 0.6
            val touchingPos = screenWidth - (logoRWidth + splash_root.paddingRight)
            val logoLWidth = splash_left_logo.width.toDouble()

            val logoLRight = splash_left_logo.right - logoLWidth / 4

            val translation = logoLRight - touchingPos
            val paddingForCenter = (screenWidth - (logoRWidth + logoLWidth * 0.5) - splash_root.paddingLeft * 0.5) * 0.5//;

            layoutR.width = logoRWidth.toInt()
            splash_right_logo.layoutParams = layoutR

            splash_right_logo.setPadding(0, 0, paddingForCenter.toInt(), 0)

            //double translation = findViewById(R.id.left_logo).getRight() - findViewById(R.id.right_logo).getLeft();


            val alpha = ObjectAnimator.ofFloat(splash_root, "alpha", 1f)
            val scaleX = ObjectAnimator.ofFloat(splash_left_logo, "scaleX", 0.5f)
            val scaleY = ObjectAnimator.ofFloat(splash_left_logo, "scaleY", 0.5f)
            val translate = ObjectAnimator.ofFloat(
                splash_left_logo,
                "translationX",
                -translation.toFloat()
            ) // ((float) ((findViewById(R.id.right_logo).getLeft())))
            val logo = ObjectAnimator.ofFloat(splash_right_logo, "alpha", 1f)

            val animator = AnimatorSet()
            val animator2 = AnimatorSet()
            //AnimatorSet delay = new AnimatorSet();
            //delay.setStartDelay(500);

            animator2.playTogether(scaleX, scaleY, translate, logo)
            animator.playSequentially(alpha, animator2) // , delay

            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {

                }

                override fun onAnimationEnd(animator: Animator) {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }

                override fun onAnimationCancel(animator: Animator) {

                }

                override fun onAnimationRepeat(animator: Animator) {

                }
            })
            animator.start()
        }
    }
}
