package com.ontime.app.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.ontime.app.R
import com.ontime.app.databinding.ActivityRegisterBinding


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonRegister.setOnClickListener { attemptRegister() }
        binding.textGoLogin.setOnClickListener { finish() }
    }

    private fun attemptRegister() {
        val fullName = binding.inputFullName.text?.toString()?.trim().orEmpty()
        val email = binding.inputEmail.text?.toString()?.trim().orEmpty()
        val password = binding.inputPassword.text?.toString().orEmpty()
        val confirmPassword = binding.inputConfirmPassword.text?.toString().orEmpty()

        val error = validate(fullName, email, password, confirmPassword)
        if (error != null) {
            showError(error)
            return
        }
        hideError()
        setLoading(true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task -> onAccountCreated(task, fullName) }
    }

    private fun onAccountCreated(task: Task<*>, fullName: String) {
        if (!task.isSuccessful) {
            setLoading(false)
            showError(task.exception?.localizedMessage ?: getString(R.string.error_invalid_login))
            return
        }

        // Store the display name on the Firebase user so we can greet them later.
        val user: FirebaseUser? = auth.currentUser
        val profileUpdate = UserProfileChangeRequest.Builder()
            .setDisplayName(fullName)
            .build()

        user?.updateProfile(profileUpdate)?.addOnCompleteListener {
            setLoading(false)
            Toast.makeText(this, R.string.success_registered, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validate(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): String? {
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return getString(R.string.error_fill_all_fields)
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return getString(R.string.error_invalid_email)
        }
        if (password.length < 6) {
            return getString(R.string.error_password_too_short)
        }
        if (password != confirmPassword) {
            return getString(R.string.error_passwords_dont_match)
        }
        return null
    }

    private fun setLoading(loading: Boolean) {
        binding.buttonRegister.isEnabled = !loading
        binding.buttonRegister.text = if (loading)
            getString(R.string.loading) else getString(R.string.action_register)
    }

    private fun showError(message: String) {
        binding.textRegisterError.text = message
        binding.textRegisterError.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.textRegisterError.visibility = View.GONE
    }
}
