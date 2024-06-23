package com.example.finalproject.ui.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.finalproject.databinding.FragmentAddTodoBinding
import com.example.finalproject.ui.data.TodoData
import com.google.android.material.textfield.TextInputEditText


class AddTodoFragment : DialogFragment() {

    private lateinit var binding: FragmentAddTodoBinding
    private var listener: DialogNextBtnClickListener? = null // Made nullable
    private var toDoData: TodoData? = null

    fun setListener(listener: DialogNextBtnClickListener) {
        this.listener = listener
    }

    companion object {
        const val TAG = "AddTodoPopupFragment"

        @JvmStatic
        fun newInstance(taskId: String, task: String, desc: String) =
            AddTodoFragment().apply {
                arguments = Bundle().apply{
                    putString("taskId", taskId)
                    putString("task", task)
                    putString("description", desc)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddTodoBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null ){
            toDoData = TodoData(arguments?.getString("taskId").toString(), arguments?.getString("task").toString(), arguments?.getString("description").toString())

            binding.todoEt.setText(toDoData?.task)
            binding.todoDesc.setText(toDoData?.description)

        }
        binding.todoClose.setOnClickListener{
            dismiss()
        }

        binding.todoNextBtn.setOnClickListener {

            val todoTask = binding.todoEt.text.toString()
            val todoDesc = binding.todoDesc.text.toString()

            if (todoTask.isNotEmpty()){
                if (toDoData == null){
                    listener?.onSaveTask(todoTask , todoDesc, binding.todoEt, binding.todoDesc )

                }else{
                    toDoData!!.task = todoTask
                    toDoData!!.description = todoDesc
                    listener?.onUpdateTask(toDoData!!, binding.todoEt, binding.todoDesc)
                }

            }
        }
    }

    private fun registerEvents() {
        binding.todoNextBtn.setOnClickListener {
            val todoTask = binding.todoEt.text.toString()
            val todoDesc = binding.todoDesc.text.toString()

            if (todoTask.isNotEmpty()) {
                listener?.let { nonNullListener ->
                    if (toDoData == null) {
                        nonNullListener.onSaveTask(todoTask, todoDesc, binding.todoEt, binding.todoDesc)
                    } else {
                        toDoData?.apply {
                            task = todoTask
                            //desc = todoDesc
                        }
                        toDoData?.let { nonNullTodoData ->
                            nonNullListener.onUpdateTask(nonNullTodoData, binding.todoEt, binding.todoDesc)
                        }
                    }
                } ?: run {
                    Toast.makeText(context, "Listener not initialized", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please type some task", Toast.LENGTH_SHORT).show()
            }
        }

        binding.todoClose.setOnClickListener {
            dismiss()
        }
    }

    interface DialogNextBtnClickListener {
        fun onSaveTask(task: String, description: String, todoEt: TextInputEditText, todoDesc: TextInputEditText)
        fun onUpdateTask(todoData: TodoData, todoEt: TextInputEditText, todoDesc: TextInputEditText)
    }
}
