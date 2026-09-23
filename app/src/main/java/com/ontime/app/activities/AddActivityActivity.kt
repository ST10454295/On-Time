package com.ontime.app.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ontime.app.R
import com.ontime.app.databinding.ActivityAddActivityBinding
import com.ontime.app.logic.ConflictChecker
import com.ontime.app.logic.Importance
import com.ontime.app.logic.TimeRange
import com.ontime.app.logic.parseTimeToMinutes
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val TAG = "AddActivityActivity"

class AddActivityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddActivityBinding
    private val repository = ActivityRepository()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val calendar = Calendar.getInstance()

    private var selectedDate: String? = null
    private var selectedStartTime: String? = null
    private var selectedEndTime: String? = null

    private val importanceOptions = listOf(Importance.HIGH, Importance.MEDIUM, Importance.LOW)
    private val typeOptions = ActivityType.entries.toList()
    private val activityList = mutableListOf<ActivityItem>()
    private lateinit var listAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupImportanceSpinner()
        setupTypeSpinner()
        setupListView()
        setupPickers()

        binding.buttonSaveActivity.setOnClickListener { onSaveClicked() }
        loadActivities()
    }

    private fun setupImportanceSpinner() {
        val labels = importanceOptions.map { it.name.lowercase().replaceFirstChar(Char::titlecase) }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels)
        binding.spinnerImportance.adapter = adapter
    }

    private fun setupTypeSpinner() {
        val labels = typeOptions.map { it.displayLabel() }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels)
        binding.spinnerType.adapter = adapter
    }

    private fun setupListView() {
        listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        binding.listActivities.adapter = listAdapter
    }

    private fun setupPickers() {
        binding.inputActivityDate.setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                calendar.set(year, month, day)
                selectedDate = dateFormat.format(calendar.time)
                binding.inputActivityDate.setText(selectedDate)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        binding.inputStartTime.setOnClickListener {
            showTimePicker { time -> selectedStartTime = time; binding.inputStartTime.setText(time) }
        }
        binding.inputEndTime.setOnClickListener {
            showTimePicker { time -> selectedEndTime = time; binding.inputEndTime.setText(time) }
        }
    }

    private fun showTimePicker(onPicked: (String) -> Unit) {
        val now = Calendar.getInstance()
        TimePickerDialog(this, { _, hour, minute ->
            onPicked(String.format(Locale.getDefault(), "%02d:%02d", hour, minute))
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
    }

    private fun onSaveClicked() {
        val title = binding.inputActivityTitle.text?.toString()?.trim().orEmpty()
        val description = binding.inputActivityDescription.text?.toString()?.trim().orEmpty()
        val date = selectedDate
        val start = selectedStartTime
        val end = selectedEndTime
        val importance = importanceOptions[binding.spinnerImportance.selectedItemPosition]
        val type = typeOptions[binding.spinnerType.selectedItemPosition]

        if (title.isEmpty() || date == null || start == null || end == null) {
            showError(getString(R.string.error_fill_all_fields)); return
        }

        val newRange = try {
            TimeRange(parseTimeToMinutes(start), parseTimeToMinutes(end))
        } catch (invalid: IllegalArgumentException) {
            Log.e(TAG, "Invalid time range: $start - $end", invalid)
            showError(getString(R.string.error_invalid_time_range)); return
        }
        hideError()

        Log.d(TAG, "Checking '$title' ($type) on $date ($start-$end, $importance) for conflicts")

        lifecycleScope.launch {
            val existingOnDate = try {
                repository.listForDate(date)
            } catch (e: Exception) {
                Log.e(TAG, "Couldn't load existing activities for conflict check", e)
                showError(getString(R.string.error_network)); return@launch
            }

            val existingRanges = existingOnDate.mapNotNull { item ->
                try {
                    TimeRange(parseTimeToMinutes(item.startTime), parseTimeToMinutes(item.endTime)) to item.importance
                } catch (e: IllegalArgumentException) { null }
            }

            val conflict = ConflictChecker.mostSevereConflict(existingRanges, newRange)
            val newActivity = ActivityItem(null, title, description, type, date, start, end, importance)

            if (conflict != null) {
                Log.d(TAG, "Conflict found with importance=$conflict - asking user to confirm")
                showConflictDialog(conflict) { saveActivity(newActivity) }
            } else {
                Log.d(TAG, "No conflict - saving directly")
                saveActivity(newActivity)
            }
        }
    }

    private fun showConflictDialog(conflict: Importance, onKeepAnyway: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.conflict_title))
            .setMessage(getString(R.string.conflict_message, conflict.name))
            .setPositiveButton(R.string.conflict_keep_anyway) { _, _ -> onKeepAnyway() }
            .setNegativeButton(R.string.conflict_change_time, null)
            .show()
    }

    private fun saveActivity(activity: ActivityItem) {
        lifecycleScope.launch {
            try {
                repository.add(activity)
                Log.d(TAG, "Saved '${activity.title}' successfully")
                clearForm()
                loadActivities()
            } catch (e: Exception) {
                Log.e(TAG, "Save failed", e)
                showError(getString(R.string.error_network))
            }
        }
    }

    private fun loadActivities() {
        lifecycleScope.launch {
            try {
                val items = repository.listAll().sortedWith(compareBy({ it.date }, { it.startTime }))
                activityList.clear(); activityList.addAll(items)
                listAdapter.clear()
                listAdapter.addAll(items.map { item ->
                    "${item.date}  ${item.startTime}-${item.endTime}  ${item.title} " +
                            "(${item.type.displayLabel()})  [${item.importance}]"
                })
            } catch (e: Exception) {
                Log.e(TAG, "Couldn't load activities", e)
            }
        }
    }

    private fun clearForm() {
        binding.inputActivityTitle.setText(""); binding.inputActivityDescription.setText("")
        binding.inputActivityDate.setText(""); binding.inputStartTime.setText(""); binding.inputEndTime.setText("")
        selectedDate = null; selectedStartTime = null; selectedEndTime = null
    }

    private fun showError(message: String) {
        binding.textAddActivityError.text = message
        binding.textAddActivityError.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.textAddActivityError.visibility = View.GONE
    }
}

