package com.ontime.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.ontime.app.auth.LoginActivity
import com.ontime.app.databinding.ActivityHomeBinding
import com.ontime.app.settings.SettingsActivity
import com.ontime.app.activities.AddActivityActivity
/**
 * Temporary landing screen shown after a successful login (Screen 4,
 * Dashboard, replaces this once it's built). Links to Settings so the
 * "user must be able to change their settings" requirement is reachable,
 * and to Log Out so the login flow can be re-tested.
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseAuth.getInstance().currentUser?.displayName?.let { name ->
            if (name.isNotBlank()) {
                binding.textGreeting.text = getString(R.string.home_greeting, name)
            }
        }

        binding.buttonAddActivity.setOnClickListener {
            startActivity(Intent(this, AddActivityActivity::class.java))
        }
        binding.buttonSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.buttonLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
