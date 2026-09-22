package com.ontime.app.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ontime.app.R
import com.ontime.app.databinding.ActivitySettingsBinding
import kotlinx.coroutines.launch


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val repository = SettingsRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadSettings()
        binding.buttonSaveSettings.setOnClickListener { saveSettings() }
    }

    private fun loadSettings() {
        setLoading(true)
        lifecycleScope.launch {
            val settings = repository.load()
            binding.inputDuration.setText(settings.defaultDurationMinutes.toString())
            if (settings.theme == "Dark") {
                binding.radioDark.isChecked = true
            } else {
                binding.radioLight.isChecked = true
            }
            setLoading(false)
        }
    }

    private fun saveSettings() {
        val minutes = binding.inputDuration.text?.toString()?.toIntOrNull()
        if (minutes == null || minutes <= 0) {
            showStatus(getString(R.string.settings_invalid_duration), isError = true)
            return
        }
        val theme = if (binding.radioDark.isChecked) "Dark" else "Light"

        setLoading(true)
        lifecycleScope.launch {
            try {
                repository.save(UserSettings(defaultDurationMinutes = minutes, theme = theme))
                showStatus(getString(R.string.settings_saved), isError = false)
            } catch (e: Exception) {
                showStatus(getString(R.string.settings_save_failed), isError = true)
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressSettings.visibility = if (loading) View.VISIBLE else View.GONE
        binding.buttonSaveSettings.isEnabled = !loading
    }

    private fun showStatus(message: String, isError: Boolean) {
        binding.textSettingsStatus.text = message
        binding.textSettingsStatus.setTextColor(
            getColor(if (isError) R.color.importance_high else R.color.importance_low)
        )
        binding.textSettingsStatus.visibility = View.VISIBLE
    }
}
