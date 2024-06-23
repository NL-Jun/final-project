package com.example.finalproject.ui.profile

import android.widget.EditText
import com.google.firebase.database.DatabaseReference
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding // Correct the type here
    private lateinit var navControl: NavController
    private lateinit var auth: FirebaseAuth
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var storageRef: StorageReference
    private lateinit var databaseRef: DatabaseReference
    private lateinit var usernameEditText: EditText
    private var editDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false) // Use the correct inflate method
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        init(view)

        // Initialize Storage Reference
        storageRef = FirebaseStorage.getInstance().reference.child("profile_images/${auth.currentUser?.uid}")

        val currentUser = auth.currentUser

        binding.textEditedName.setText(currentUser?.displayName)
        binding.textEmail.text = "Email          :       ${currentUser?.email}"

        // Initialize database reference
        databaseRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser?.uid ?: "")


        // Make ImageView clickable
        binding.profileImage.setOnClickListener {
            chooseImage()
        }

        // Load and display existing profile image (if any)
        loadProfileImage()

        binding.btnSignOut.setOnClickListener {
            signOut()
        }

        // Fetch and display initial data
        fetchUserData()

        binding.btnEdit.setOnClickListener { showEditDialog() }

    }

    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            uploadImage() // Save to internal storage
        }
    }

    private fun uploadImage() { // Save to internal storage
        imageUri?.let { uri ->
            val filename = "${auth.currentUser?.uid}_profile_image.jpg"

            try {
                val inputStream = context?.contentResolver?.openInputStream(uri)
                val outputStream = context?.openFileOutput(filename, Context.MODE_PRIVATE)
                outputStream?.use {
                    inputStream?.copyTo(it)
                }
                Toast.makeText(context, "Image saved successfully", Toast.LENGTH_SHORT).show()
                loadProfileImage()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(context, "Image URI is null", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProfileImage() {
        val filename = "${auth.currentUser?.uid}_profile_image.jpg"
        try {
            val fis = context?.openFileInput(filename)
            val bitmap = BitmapFactory.decodeStream(fis)
            binding.profileImage.setImageBitmap(bitmap)
            fis?.close()
        } catch (e: Exception) {
            //
        }
    }

    private fun saveName() {
        val newName = binding.textName.text.toString().trim()
        if (newName.isEmpty()) {
            Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val user = auth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnSuccessListener {
                // Update the name in the database
                databaseRef.child("name").setValue(newName)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Name updated successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "Failed to update name in database: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            ?.addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to update name: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun signOut() {
        auth.signOut()

        // Navigate to the sign-in fragment and clear the back stack
        findNavController().navigate(
            R.id.action_navigation_profile_to_navigation_signIn,
            null,
            NavOptions.Builder()
                .setPopUpTo(R.id.navigation_profile, true)
                .build()
        )
    }

    private fun init(view: View) {
        navControl = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
    }

    private fun fetchUserData() {
        val currentUser = auth.currentUser

        databaseRef.child(currentUser?.uid ?: "").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java)
                val studentId = snapshot.child("studentId").getValue(String::class.java)

                binding.textEditedName.text = name ?: currentUser?.displayName ?: ""
                binding.textEditedStudentID.text = studentId ?: ""
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showEditDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.editName)
        val studentIdEditText = dialogView.findViewById<EditText>(R.id.editStudentID)
        val saveButton = dialogView.findViewById<Button>(R.id.btnSave)

        // Prefill with existing data
        nameEditText.setText(binding.textEditedName.text)
        studentIdEditText.setText(binding.textEditedStudentID.text)

        val dialogBuilder = AlertDialog.Builder(requireContext()) // Store the builder
            .setView(dialogView)
            .setPositiveButton(null, null) // Remove default positive button
            .setNegativeButton("Cancel", null)

        // Create and store the dialog instance
        editDialog = dialogBuilder.create()
        editDialog?.show() // Show the dialog (safe call)

        saveButton.setOnClickListener {
            val newName = nameEditText.text.toString().trim()
            val newStudentId = studentIdEditText.text.toString().trim()
            updateUserProfile(newName, newStudentId)

            editDialog?.dismiss() // Dismiss the dialog (safe call)
        }
    }

    private fun updateUserProfile(newName: String, newStudentId: String) {
        val user = auth.currentUser ?: return

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .build()

        user.updateProfile(profileUpdates)
            .addOnSuccessListener {
                val updates = hashMapOf(
                    "name" to newName,
                    "studentId" to newStudentId
                )
                databaseRef.child(user.uid).updateChildren(updates as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        fetchUserData()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "Failed to update database: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to update profile: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
