package com.example.finalproject.ui.flashcard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.finalproject.R
import com.example.finalproject.ui.data.Flashcard
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DialogAddFlashcard : DialogFragment() {

    private lateinit var editTextTerm: EditText
    private lateinit var editTextDefinition: EditText
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_add_flashcard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editTextTerm = view.findViewById(R.id.edit_text_term)
        editTextDefinition = view.findViewById(R.id.edit_text_definition)
        databaseReference = FirebaseDatabase.getInstance().getReference("flashcards")

        val categoryId = arguments?.getString("categoryId") ?: "" // Retrieve categoryId

        databaseReference = FirebaseDatabase.getInstance()
            .getReference("categories/$categoryId/flashcards")

        view.findViewById<Button>(R.id.btn_save_flashcard).setOnClickListener {
            val term = editTextTerm.text.toString().trim()
            val definition = editTextDefinition.text.toString().trim()

            if (term.isNotEmpty() && definition.isNotEmpty()) {
                //Generate id here
                val flashcardId = databaseReference.push().key
                //Generate object with term,definition, and ID
                val flashcard = Flashcard(term, definition, flashcardId!!)

                flashcardId?.let {
                    databaseReference.child(it).setValue(flashcard).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Notify the user of success
                        } else {
                            // Handle the error
                        }
                    }
                }
            }
            dismiss()
        }
    }
}
