package com.ontime.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.ontime.app.auth.LoginActivity

/**
 * Screen 1: Splash Screen.
 *
 * Displays the On-Time branding for a short moment when the app starts,
 * then routes to Login - or straight to Home if Firebase Auth already
 * has a signed-in user, so returning users don't have to log in every
 * time (Firebase persists the session between launches on its own).
 */
class SplashActivity : AppCompatActivity() {

    companion object {
        private const val SPLASH_DELAY_MS = 1800L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val loggedIn = FirebaseAuth.getInstance().currentUser != null
            val destination = if (loggedIn) HomeActivity::class.java
                               else LoginActivity::class.java
            startActivity(Intent(this, destination))
            finish()
        }, SPLASH_DELAY_MS)
    }
}
