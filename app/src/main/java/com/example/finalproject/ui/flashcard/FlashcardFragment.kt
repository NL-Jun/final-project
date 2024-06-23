package com.example.finalproject.ui.flashcard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.ui.data.Flashcard
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FlashcardFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FlashcardAdapter
    private var category: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val categoryId = it.getString("categoryId")
            categoryId?.let { id ->

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_flashcard, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 2) // 2 columns in grid

        val categoryId = arguments?.getString("categoryId") ?: ""//Get Category ID
        adapter = FlashcardAdapter(mutableListOf(), categoryId, requireContext())
        recyclerView.adapter = adapter
        loadFlashcards(categoryId)


        val addButton: Button = view.findViewById(R.id.addButton)
        addButton.setOnClickListener {
            val categoryId = arguments?.getString("categoryId") ?: ""
            openAddFlashcardDialog(categoryId)
        }

        return view
    }

    private fun loadFlashcards(categoryId: String) {
        // Target specific category
        val databaseReference = FirebaseDatabase.getInstance().getReference("categories/$categoryId/flashcards")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newFlashcards = mutableListOf<Flashcard>()
                for (flashcardSnapshot in snapshot.children) {
                    val flashcard = flashcardSnapshot.getValue(Flashcard::class.java)
                    flashcard?.let { newFlashcards.add(it) }
                }
                adapter.updateFlashcards(newFlashcards)
            }//Hello

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    private fun openAddFlashcardDialog(categoryId: String) {
        val dialog = DialogAddFlashcard()
        val bundle = Bundle().apply {
            putString("categoryId", categoryId)
        }
        dialog.arguments = bundle
        dialog.show(parentFragmentManager, "DialogAddTermFragment")
    }



    companion object {
        @JvmStatic
        fun newInstance(category: String) =
            FlashcardFragment().apply {
                arguments = Bundle().apply {
                    putString("category", category)
                }
            }
    }
}
