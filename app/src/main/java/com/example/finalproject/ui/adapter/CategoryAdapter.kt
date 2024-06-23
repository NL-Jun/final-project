package com.example.finalproject.ui.adapter

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.ui.data.Category
import com.google.firebase.database.FirebaseDatabase




class CategoryAdapter(private val categories: MutableList<Category>, private val onClick: (Category) -> Unit) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewCategory: TextView = itemView.findViewById(R.id.tvCategoryTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.textViewCategory.text = category.title
        holder.itemView.setOnClickListener {
            onClick(category)
        }

        holder.itemView.setOnLongClickListener {view->

            val context = view.context
            // Use the category ID to reference the correct node
            val categoryRef = FirebaseDatabase.getInstance().getReference("categories/${category.id}")
            categoryRef.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Category deleted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error deleting category: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
            categories.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
            true // Return true to indicate that the click was handled
        }

    }


    override fun getItemCount(): Int = categories.size

    fun addCategory(category: Category) {
        categories.add(category)
        notifyItemInserted(categories.size - 1)
    }

    fun updateCategories(newCategories: List<Category>) {
        categories.clear()
        categories.addAll(newCategories)
        notifyDataSetChanged()
    }
}