package com.example.finalproject.util

import com.google.firebase.database.FirebaseDatabase
import com.example.finalproject.ui.data.Flashcard

object FirebaseUtil {

    // Reference to the Firebase Realtime Database
    private val database = FirebaseDatabase.getInstance()

    // Reference to the "flashcards" node in the database
    private val flashcardsRef = database.getReference("flashcards")

    // Function to save flashcard data to Firebase Realtime Database
    fun saveFlashcardToFirebase(flashcard: Flashcard) {
        // Generate a unique key for the flashcard
        val key = flashcardsRef.push().key ?: return

        // Set the flashcard data under the generated key
        flashcardsRef.child(key).setValue(flashcard)
    }
}