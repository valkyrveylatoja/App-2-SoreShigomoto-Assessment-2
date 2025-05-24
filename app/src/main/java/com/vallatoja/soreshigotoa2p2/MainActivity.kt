package com.vallatoja.soreshigotoa2p2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.content.res.ResourcesCompat
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    private lateinit var linearLayoutCheckboxes: LinearLayout
    private lateinit var prefsManager: SharedPrefsManager

    companion object {
        const val REQUEST_CODE_ADD_REMINDER = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showWelcomeDialog()

        linearLayoutCheckboxes = findViewById(R.id.linearLayoutCheckboxes)
        prefsManager = SharedPrefsManager(applicationContext)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_REMINDER)
        }

        val fabInstruction = findViewById<FloatingActionButton>(R.id.fabInstruction)
        fabInstruction.setOnClickListener {
            val intent = Intent(this, InstructionActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_REMINDER)
        }

        // Load saved tasks and add checkboxes
        loadTasksAndDisplay()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_REMINDER && resultCode == Activity.RESULT_OK) {
            // Reload tasks and refresh checkboxes when a new task is added
            loadTasksAndDisplay()
        }
    }

    private fun loadTasksAndDisplay() {
        linearLayoutCheckboxes.removeAllViews() // clear previous checkboxes

        val tasks = prefsManager.getTasks()
        for (task in tasks) {
            val label = "${task.name} (${task.hour}:${"%02d".format(task.minute)})"
            addReminderBullet(label)
        }
    }

    private fun addReminderBullet(label: String) {
        val textView = TextView(this).apply {
            text = "• $label"
            textSize = 18f
            typeface = ResourcesCompat.getFont(this@MainActivity, R.font.regular_font)
            setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.black))
        }
        linearLayoutCheckboxes.addView(textView)
    }

    // welcome dialog
    private fun showWelcomeDialog() {
        AlertDialog.Builder(this)
            .setTitle("Welcome!")
            .setMessage("Thanks for using SoreShigoto ❤\n\nHere you can add a reminder where you can set the time & date to schedule a notification!")
            .setPositiveButton("Let's go!") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
}


