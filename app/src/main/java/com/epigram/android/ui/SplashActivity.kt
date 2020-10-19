package com.epigram.android.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

import com.epigram.android.R
import com.epigram.android.data.arch.PreferenceModule

import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDefaultNightMode(PreferenceModule.darkMode.get())
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        setContentView(R.layout.activity_splash_screen)
        findViewById<View>(R.id.left_logo).post {
            val screen = findViewById<View>(R.id.splash_root)
            val logoR = findViewById<View>(R.id.right_logo)
            val logoL = findViewById<View>(R.id.left_logo)

            val layoutL = logoL.layoutParams
            val layoutR = logoR.layoutParams

            val screenWidth = screen.width.toDouble()
            val logoRWidth = (screenWidth - (screen.paddingRight + screen.paddingLeft)) * 0.6
            val touchingPos = screenWidth - (logoRWidth + screen.paddingRight)
            val logoLWidth = logoL.width.toDouble()

            val logoLRight = logoL.right - logoLWidth / 4

            val translation = logoLRight - touchingPos
            val paddingForCenter =
                (screenWidth - (logoRWidth + logoLWidth * 0.5) - screen.paddingLeft * 0.5) * 0.5//;

            layoutR.width = logoRWidth.toInt()
            findViewById<View>(R.id.right_logo).layoutParams = layoutR

            findViewById<View>(R.id.right_logo).setPadding(0, 0, paddingForCenter.toInt(), 0)

            //double translation = findViewById(R.id.left_logo).getRight() - findViewById(R.id.right_logo).getLeft();


            val alpha = ObjectAnimator.ofFloat(findViewById(R.id.splash_root), "alpha", 1f)
            val scaleX = ObjectAnimator.ofFloat(findViewById(R.id.left_logo), "scaleX", 0.5f)
            val scaleY = ObjectAnimator.ofFloat(findViewById(R.id.left_logo), "scaleY", 0.5f)
            val translate = ObjectAnimator.ofFloat(
                findViewById(R.id.left_logo),
                "translationX",
                -translation.toFloat()
            ) // ((float) ((findViewById(R.id.right_logo).getLeft())))
            val logo = ObjectAnimator.ofFloat(findViewById(R.id.right_logo), "alpha", 1f)

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


        //startActivity(new Intent(this, MainActivity.class));
    }
}
