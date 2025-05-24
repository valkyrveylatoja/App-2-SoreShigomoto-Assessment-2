package com.vallatoja.soreshigotoa2p2

import android.content.Context

class SharedPrefsManager(context: Context) {

    private val prefs = context.getSharedPreferences("tasks_prefs", Context.MODE_PRIVATE)
    private val TASKS_KEY = "task_list"

    // Save a task by encoding it as a single string and adding it to a Set<String>
    fun saveTask(task: Task) {
        val taskString = encodeTask(task)
        val currentTasks = prefs.getStringSet(TASKS_KEY, emptySet()) ?: emptySet()
        val updatedTasks = HashSet(currentTasks)
        updatedTasks.add(taskString)
        prefs.edit().putStringSet(TASKS_KEY, updatedTasks).apply()
    }

    // Retrieve all tasks by decoding each string in the set
    fun getTasks(): List<Task> {
        val tasksSet = prefs.getStringSet(TASKS_KEY, emptySet()) ?: emptySet()
        return tasksSet.mapNotNull { decodeTask(it) }
    }

    // Clear all saved tasks
    fun clearTasks() {
        prefs.edit().remove(TASKS_KEY).apply()
    }

    // Convert Task to a single string with | delimiter
    private fun encodeTask(task: Task): String {
        return listOf(
            task.name.replace("|", ""), // sanitize to avoid delimiter issues
            task.description.replace("|", ""),
            task.year.toString(),
            task.month.toString(),
            task.day.toString(),
            task.hour.toString(),
            task.minute.toString()
        ).joinToString("|")
    }

    // Parse the string back to a Task object
    private fun decodeTask(taskString: String): Task? {
        val parts = taskString.split("|")
        if (parts.size != 7) return null
        return try {
            Task(
                name = parts[0],
                description = parts[1],
                year = parts[2].toInt(),
                month = parts[3].toInt(),
                day = parts[4].toInt(),
                hour = parts[5].toInt(),
                minute = parts[6].toInt()
            )
        } catch (e: Exception) {
            null
        }
    }
}


