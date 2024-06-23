package com.example.finalproject.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.ui.adapter.CategoryAdapter
import com.example.finalproject.ui.data.Category
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.*

class CategoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var categories: MutableList<Category>
    private lateinit var adapter: CategoryAdapter
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category, container, false)

        recyclerView = view.findViewById(R.id.rv_category)
        // Set the LayoutManager to GridLayoutManager
        val numberOfColumns = 2
        recyclerView.layoutManager = GridLayoutManager(context, numberOfColumns)
        categories = mutableListOf()

        fetchCategories()

        adapter = CategoryAdapter(categories) { category ->
            // Navigate to FlashcardFragment using Safe Args

            val bundle = Bundle().apply {
                putString("categoryId", category.id) // Pass the category ID
            }
            findNavController().navigate(R.id.action_navigation_category_to_navigation_flashcard, bundle)
        }
        recyclerView.adapter = adapter
        //adapter = CategoryAdapter(categories)
       // recyclerView.adapter = adapter

        databaseReference = FirebaseDatabase.getInstance().getReference("categories")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categories.clear()
                for (categorySnapshot in snapshot.children) {
                    val category = categorySnapshot.getValue(Category::class.java)
                    category?.let { categories.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        // Find the button and set an OnClickListener to show the dialog
        val addButton: Button = view.findViewById(R.id.btn_addCat)
        addButton.setOnClickListener {
            val dialog = DialogAddCategory()
            dialog.show(parentFragmentManager, "DialogAddCategory")
        }


        return view
    }

    fun fetchCategories() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("categories")
        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                categories.clear()
                for (categorySnapshot in snapshot.children) {
                    // Assuming each child represents a Category object with 'id' and 'title'
                    val category = categorySnapshot.getValue(Category::class.java)?.copy(
                        id = categorySnapshot.key!! // Set the 'id' from the snapshot key
                    )
                    category?.let { categories.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }
}