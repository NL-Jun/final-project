package com.example.finalproject.ui.flashcard

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.ui.data.Flashcard
import com.google.firebase.database.FirebaseDatabase


class FlashcardAdapter(private val flashcards: MutableList<Flashcard>, private val categoryId: String, private val context: Context) :
    RecyclerView.Adapter<FlashcardAdapter.FlashcardViewHolder>() {

    class FlashcardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val termTextView: TextView = view.findViewById(R.id.text_flashcard_term)
        val definitionTextView: TextView = view.findViewById(R.id.text_flashcard_definition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flashcard, parent, false)
        return FlashcardViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlashcardViewHolder, position: Int) {
        Log.d("FlashcardAdapter", "onBindViewHolder called for position: $position")
        val flashcard = flashcards[position]
        holder.termTextView.text = flashcard.term
        holder.definitionTextView.text = flashcard.definition

        // Conditional Visibility Change for Definition
        if (flashcard.isDefinitionVisible) {
            holder.definitionTextView.visibility = View.VISIBLE
        } else {
            holder.definitionTextView.visibility = View.GONE
        }

        //click listener
        holder.itemView.setOnClickListener {
            flashcard.isDefinitionVisible = !flashcard.isDefinitionVisible //Toggle visibility
            Log.d("FlashcardAdapter", "Definition visibility: ${flashcard.isDefinitionVisible}")
            notifyItemChanged(position) //update the item
        }

        //Long Click Listener
        holder.itemView.setOnLongClickListener {
            val flashcardToDelete = flashcards[position]
            showDeleteConfirmationDialog(flashcardToDelete, context)
            true
        }
    }

    override fun getItemCount(): Int = flashcards.size

    fun addFlashcard(flashcard: Flashcard) {
        flashcards.add(flashcard)
        notifyItemInserted(flashcards.size - 1)
    }

    fun updateFlashcards(newFlashcards: List<Flashcard>) {
        flashcards.clear()
        flashcards.addAll(newFlashcards)
        notifyDataSetChanged()
    }

    // Function to show the delete confirmation dialog
    private fun showDeleteConfirmationDialog(
        flashcard: Flashcard,
        context: Context
    ) { // Add context parameter
        val builder = AlertDialog.Builder(context) // Use the provided context
        builder.setMessage("Are you sure you want to delete this flashcard?")
            .setPositiveButton("Yes") { _, _ ->
                val position = flashcards.indexOf(flashcard)
                if (position != -1) {
                    flashcards.removeAt(position)
                    notifyItemRemoved(position)
                    deleteFlashcardFromFirebase(flashcard, categoryId)
                    notifyDataSetChanged()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteFlashcardFromFirebase(flashcard: Flashcard, categoryId: String) {
        //val categoryId = arguments?.getString("categoryId") ?: ""//

        val databaseReference = FirebaseDatabase.getInstance()
            .getReference("categories/$categoryId/flashcards")
        Log.d("FlashcardAdapter", "Deleting from reference: $databaseReference")


        flashcard.id?.let { flashcardId ->
            databaseReference.child(flashcardId).removeValue()
                .addOnSuccessListener {
                    Log.d("FlashcardAdapter", "Flashcard deleted successfully")
                    // Optionally show a success message
                }.addOnFailureListener {e ->
                    Log.e("FlashcardAdapter", "Failed to delete flashcard. Error message: ${e.message}", e)
                    // Handle the error
                }
        }
    }
}