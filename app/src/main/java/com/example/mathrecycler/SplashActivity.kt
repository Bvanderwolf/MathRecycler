package com.example.mathrecycler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //start MainActivity after 1000 miliseconds
        Handler().postDelayed({
            startActivity(
                Intent(this@SplashActivity,
                    MainActivity::class.java)
            )
            finish()
        }, 1000)
    }
}
