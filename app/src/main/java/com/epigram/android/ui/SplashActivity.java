package com.epigram.android.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;

import com.epigram.android.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);
        findViewById(R.id.left_logo).post(new Runnable() {
            @Override
            public void run() {


                View screen = findViewById(R.id.splash_root);
                View logoR = findViewById(R.id.right_logo);
                View logoL = findViewById(R.id.left_logo);

                ViewGroup.LayoutParams layoutL = logoL.getLayoutParams();
                ViewGroup.LayoutParams layoutR = logoR.getLayoutParams();

                double screenWidth = screen.getWidth();
                double logoRWidth = (screenWidth - (screen.getPaddingRight()+screen.getPaddingLeft()))*0.6;
                double touchingPos = screenWidth - (logoRWidth + screen.getPaddingRight());
                double logoLWidth = logoL.getWidth();

                double logoLRight = logoL.getRight()-(logoLWidth/4);

                double translation = logoLRight - touchingPos;
                double paddingForCenter = ((screenWidth - (logoRWidth + (logoLWidth)*0.5)-(screen.getPaddingLeft()*0.5))*0.5);//;

                layoutR.width = (int) logoRWidth;
                findViewById(R.id.right_logo).setLayoutParams(layoutR);

                findViewById(R.id.right_logo).setPadding(0,0,(int) paddingForCenter,0);

                //double translation = findViewById(R.id.left_logo).getRight() - findViewById(R.id.right_logo).getLeft();



                final ObjectAnimator alpha = ObjectAnimator.ofFloat(findViewById(R.id.splash_root), "alpha", 1f);
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(findViewById(R.id.left_logo), "scaleX", 0.5f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(findViewById(R.id.left_logo), "scaleY", 0.5f);
                ObjectAnimator translate = ObjectAnimator.ofFloat(findViewById(R.id.left_logo), "translationX", -(float) translation); // ((float) ((findViewById(R.id.right_logo).getLeft())))
                ObjectAnimator logo = ObjectAnimator.ofFloat(findViewById(R.id.right_logo), "alpha", 1f);

                AnimatorSet animator = new AnimatorSet();
                AnimatorSet animator2 = new AnimatorSet();
                //AnimatorSet delay = new AnimatorSet();
                //delay.setStartDelay(500);

                animator2.playTogether(scaleX, scaleY, translate,logo);
                animator.playSequentially(alpha, animator2); // , delay

                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                animator.start();
            }
        });



        //startActivity(new Intent(this, MainActivity.class));
    }
}
