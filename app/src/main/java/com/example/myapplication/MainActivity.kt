package com.example.myapplication

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity() {

    private lateinit var taskInput: EditText
    private lateinit var addButton: Button
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var emptyStateImage: ImageView
    private lateinit var emptyStateText: TextView

    private val taskList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        setupClickListeners()
        updateEmptyState()
    }

    private fun initViews() {
        taskInput = findViewById(R.id.taskInput)
        addButton = findViewById(R.id.addButton)
        taskRecyclerView = findViewById(R.id.taskRecyclerView)
        emptyStateImage = findViewById(R.id.emptyStateImage)
        emptyStateText = findViewById(R.id.emptyStateText)
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(taskList) { position ->
            // Handle task deletion (swipe or tap)
            taskList.removeAt(position)
            taskAdapter.notifyItemRemoved(position)
            updateEmptyState()
            Toast.makeText(this, "Task completed!", Toast.LENGTH_SHORT).show()
        }

        taskRecyclerView.layoutManager = LinearLayoutManager(this)
        taskRecyclerView.adapter = taskAdapter
    }

    private fun setupClickListeners() {
        addButton.setOnClickListener {
            addTask()
        }

        // Handle double-tap on empty area to clear all tasks
        emptyStateImage.setOnClickListener {
            if (taskList.isNotEmpty()) {
                taskList.clear()
                taskAdapter.notifyDataSetChanged()
                updateEmptyState()
                Toast.makeText(this, "All tasks cleared!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addTask() {
        val taskText = taskInput.text.toString().trim()
        if (taskText.isNotEmpty()) {
            val newTask = Task(taskText)
            taskList.add(newTask)
            taskAdapter.notifyItemInserted(taskList.size - 1)
            taskInput.text.clear()
            updateEmptyState()
            Toast.makeText(this, "Task added!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateEmptyState() {
        if (taskList.isEmpty()) {
            emptyStateImage.visibility = android.view.View.VISIBLE
            emptyStateText.visibility = android.view.View.VISIBLE
            taskRecyclerView.visibility = android.view.View.GONE
        } else {
            emptyStateImage.visibility = android.view.View.GONE
            emptyStateText.visibility = android.view.View.GONE
            taskRecyclerView.visibility = android.view.View.VISIBLE
        }
    }
}

// Task data class
data class Task(
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

// RecyclerView Adapter
class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onTaskClick: (Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val taskText: TextView = itemView.findViewById(R.id.taskText)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): TaskViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskText.text = task.text

        holder.deleteButton.setOnClickListener {
            onTaskClick(position)
        }

        // Handle item tap to mark as completed
        holder.itemView.setOnClickListener {
            onTaskClick(position)
        }
    }

    override fun getItemCount(): Int = tasks.size
}