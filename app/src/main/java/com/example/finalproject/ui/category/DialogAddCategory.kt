package com.example.finalproject.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.finalproject.R
import com.example.finalproject.ui.data.Category
import com.google.firebase.database.FirebaseDatabase

class DialogAddCategory : DialogFragment() {

    private lateinit var editTextCategoryTitle: EditText
    private lateinit var buttonSubmit: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_add_category, container, false)

        editTextCategoryTitle = view.findViewById(R.id.editTextCategoryTitle)
        buttonSubmit = view.findViewById(R.id.btnSubmit)

        buttonSubmit.setOnClickListener {
            val categoryName = editTextCategoryTitle.text.toString().trim()
            if (categoryName.isNotEmpty()) {
                val databaseReference = FirebaseDatabase.getInstance().getReference("categories")
                //Generate ID here
                val categoryId = databaseReference.push().key

                // Create a Category object with the entered name and generated ID
                val category = Category(categoryId!!,categoryName)

                categoryId?.let {
                    // Save the Category object instead of just the name
                    databaseReference.child(it).setValue(category).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Category added successfully", Toast.LENGTH_SHORT).show()
                            dismiss()
                        } else {
                            Toast.makeText(context, "Failed to add category", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Please enter a category name", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}

