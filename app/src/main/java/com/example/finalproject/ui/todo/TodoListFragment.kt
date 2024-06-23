package com.example.finalproject.ui.todo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.databinding.FragmentTodolistBinding
import com.example.finalproject.ui.adapter.TodoAdapter
import com.example.finalproject.ui.data.TodoData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class TodoListFragment : Fragment(), AddTodoFragment.DialogNextBtnClickListener,
    TodoAdapter.TaskAdapterInterface {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding: FragmentTodolistBinding
    private var popUpFragment: AddTodoFragment? = null
    private lateinit var authId: String

    private lateinit var adapter: TodoAdapter
    private lateinit var mList: MutableList<TodoData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodolistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        getDataFromFirebase()
        registerEvents()
    }

    private fun getDataFromFirebase() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear() // Clear the current list to avoid duplicates
                adapter.notifyDataSetChanged()

                // Iterate through each child node in the "tasks" node
                for (taskSnapshot in snapshot.children) {
                    // Retrieve the taskId, task, and description values from the snapshot
                    val taskId = taskSnapshot.key ?: continue // Retrieve the taskId
                    val taskData = taskSnapshot.value as? Map<String, String> // Cast to Map<String, String>

                    // Extract task and description
                    val task = taskData?.get("task") ?: ""
                    val description = taskData?.get("description") ?: ""

                    // Create a TodoData object using the taskId, task, and description
                    val todoTask = TodoData(taskId, task, description)

                    // Add the TodoData object to the list
                    mList.add(todoTask)
                }

                // Notify the adapter that the data set has changed
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors that occur during data retrieval
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun registerEvents() {
        binding.addBtnTodo.setOnClickListener {
            popUpFragment = AddTodoFragment()
            popUpFragment?.setListener(this)
            popUpFragment?.show(childFragmentManager, AddTodoFragment.TAG)
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        authId = auth.currentUser!!.uid
        databaseRef = FirebaseDatabase.getInstance().reference.child("tasks").child(authId)
        binding.rvTodoList.setHasFixedSize(true)
        binding.rvTodoList.layoutManager = LinearLayoutManager(context)
        mList = mutableListOf()
        adapter = TodoAdapter(mList)
        adapter.setListener(this)
        binding.rvTodoList.adapter = adapter
    }

    override fun onSaveTask(task: String, description: String, todoEt: TextInputEditText, todoDesc: TextInputEditText) {
        val newTaskRef = databaseRef.push()  // Generate a new unique key

        val taskData = mapOf(
            "task" to task,
            "description" to description
        )

        newTaskRef.setValue(taskData) // Store the task data (both task and description) under the new key
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Task Added Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, it.exception?.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        popUpFragment?.dismiss()
    }

    override fun onUpdateTask(todoData: TodoData, todoEt: TextInputEditText, todoDesc: TextInputEditText) {
        val taskRef = databaseRef.child(todoData.taskId)  // Reference to the specific task

        val updates = hashMapOf<String, Any>(
            "task" to todoData.task,
            "description" to todoData.description // Update BOTH task and description
        )

        taskRef.updateChildren(updates)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()

                    // Find the position of the updated item in the list
                    val position = mList.indexOfFirst { it.taskId == todoData.taskId }

                    // Update the item in the list
                    if (position != -1) {
                        mList[position] = todoData
                        adapter.notifyItemChanged(position) // Notify adapter of change
                    }
                    // Dismiss the dialog after updating the list and notifying the adapter
                    popUpFragment?.dismiss()
                } else {
                    Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onEditItemClicked(todoData: TodoData, position: Int) {
        if(popUpFragment != null){
            childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()

            popUpFragment = AddTodoFragment.newInstance(todoData.taskId, todoData.task, todoData.description)
            popUpFragment!!.setListener(this)
            popUpFragment!!.show(
                childFragmentManager, AddTodoFragment.TAG
            )
        }
    }

    override fun onDeleteItemClicked(todoData: TodoData, position: Int) {
        databaseRef.child(todoData.taskId).removeValue().addOnCompleteListener{
            if (it.isSuccessful){
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

}
