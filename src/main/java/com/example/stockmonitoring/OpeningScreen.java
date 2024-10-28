package com.example.stockmonitoring;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

public class OpeningScreen extends AppCompatActivity {
    Animation animationScale;
    Animation animationRotate;
    Animation animationFadeIn;

    AnimationSet animationSet;

    ImageView openingLogo;

    Intent goToHome;

    SharedPreferences saveUsername;

    String username;

    TextView welcomeMessage3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_screen);

        //init animation set / animations
        animationScale = AnimationUtils.loadAnimation(OpeningScreen.this, R.anim.scale);
        animationRotate = AnimationUtils.loadAnimation(OpeningScreen.this, R.anim.rotate);
        animationFadeIn = AnimationUtils.loadAnimation(OpeningScreen.this, R.anim.fade_in);

        animationSet = new AnimationSet(false);

        animationSet.addAnimation(animationScale);
        animationSet.addAnimation(animationRotate);

        //set image
        openingLogo = findViewById(R.id.openingLogo);

        openingLogo.setAnimation(animationSet);

        //go to home screen
        goToHome = new Intent(this, MainActivity.class);

        Boolean timer = new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                welcomeMessage3 = findViewById(R.id.welcomeMessage3);

                saveUsername = getSharedPreferences("username", 0);
                username = saveUsername.getString("username", "");
                setWelcomeMessage(username);

                welcomeMessage3.setAnimation(animationFadeIn);
                Boolean timer = new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        finish();
                        startActivity(goToHome);
                    }
                }, 5000);
            }
        }, 5000);
    }

    public void setWelcomeMessage(String name)
    {
        if (name.equals("")) {
            welcomeMessage3.setText("Welcome guest");
        } else {
            welcomeMessage3.setText("Welcome " + name);
        }
    }
}