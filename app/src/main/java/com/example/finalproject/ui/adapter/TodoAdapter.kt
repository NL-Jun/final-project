package com.example.finalproject.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.databinding.EachTodoItemBinding
import com.example.finalproject.ui.data.TodoData

class TodoAdapter(private val list: MutableList<TodoData>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private var listener: TaskAdapterInterface? = null

    fun setListener(listener: TaskAdapterInterface) {
        this.listener = listener
    }

    inner class TodoViewHolder(val binding: EachTodoItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = EachTodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.todoTask.text = this.task
                binding.todoDesc.text = this.description // Set the description text
                binding.editTask.setOnClickListener {
                    listener?.onEditItemClicked(this, position)
                }
                binding.deleteTask.setOnClickListener {
                    listener?.onDeleteItemClicked(this, position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface TaskAdapterInterface {
        fun onDeleteItemClicked(todoData: TodoData, position: Int)
        fun onEditItemClicked(todoData: TodoData, position: Int)
    }
}
