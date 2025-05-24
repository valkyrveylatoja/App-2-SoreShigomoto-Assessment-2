package com.vallatoja.soreshigotoa2p2

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import android.app.Activity


class MainActivity2 : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var buttonTime: Button
    private lateinit var buttonDate: Button
    private lateinit var buttonAdd: Button
    private lateinit var prefsManager: SharedPrefsManager


    private var selectedHour = 0
    private var selectedMinute = 0
    private var selectedYear = 0
    private var selectedMonth = 0
    private var selectedDay = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        editTextName = findViewById(R.id.editTextName)
        editTextDescription = findViewById(R.id.editTextDescription)
        buttonTime = findViewById(R.id.buttonTime)
        buttonDate = findViewById(R.id.buttonDate)
        buttonAdd = findViewById(R.id.buttonAdd)
        prefsManager = SharedPrefsManager(applicationContext)


        buttonTime.setOnClickListener {
            val timePicker = android.app.TimePickerDialog(
                this,
                { _, hour, minute ->
                    selectedHour = hour
                    selectedMinute = minute
                    buttonTime.text = getString(R.string.time_format, hour, minute)
                },
                selectedHour,
                selectedMinute,
                true
            )
            timePicker.show()
        }

        buttonDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = android.app.DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedYear = year
                    selectedMonth = month
                    selectedDay = dayOfMonth
                    buttonDate.text = getString(R.string.date_format, dayOfMonth, month + 1, year)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        buttonAdd.setOnClickListener {
            val name = editTextName.text.toString()
            val description = editTextDescription.text.toString()

            if (name.isNotEmpty() && description.isNotEmpty()) {
                val task = Task(
                    name = name,
                    description = description,
                    year = selectedYear,
                    month = selectedMonth,
                    day = selectedDay,
                    hour = selectedHour,
                    minute = selectedMinute
                )

                // <-- HERE: Save the task to SharedPreferences
                prefsManager.saveTask(task)

                // Schedule the notification/alarm
                scheduleAlarm(name, description)

                Toast.makeText(this, R.string.toast_saved, Toast.LENGTH_SHORT).show()

                // Return result to MainActivity to refresh list
                val intent = Intent().apply {
                    putExtra("reminder_label", "$name ($selectedHour:${"%02d".format(selectedMinute)})")
                }
                setResult(RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this, R.string.toast_fill_fields, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun scheduleAlarm(name: String, description: String) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedYear)
            set(Calendar.MONTH, selectedMonth)
            set(Calendar.DAY_OF_MONTH, selectedDay)
            set(Calendar.HOUR_OF_DAY, selectedHour)
            set(Calendar.MINUTE, selectedMinute)
            set(Calendar.SECOND, 0)
        }

        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("name", name)
            putExtra("desc", description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager?.canScheduleExactAlarms() == true) {
                try {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } catch (e: SecurityException) {
                    Toast.makeText(this, "Exact alarm permission denied.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(
                    this,
                    "Exact alarm not allowed. Please enable it in system settings.",
                    Toast.LENGTH_LONG
                ).show()
                val settingsIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(settingsIntent)
            }
        } else {
            alarmManager?.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }
}




