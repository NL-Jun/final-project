package com.example.finalproject.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentSignUpBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


class SignUpFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navControl: NavController
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide the bottom navigation menu
        //(activity as? AppCompatActivity)?.supportActionBar?.hide()
        //activity?.findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.GONE

        init(view)
        registerEvents()
    }

    private fun init(view: View){
        navControl = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
    }

    private fun registerEvents(){

        binding.authTextView.setOnClickListener{
            navControl.navigate(R.id.action_navigation_SignUp_to_navigation_signIn)
        }

       /* binding.registerButton.setOnClickListener{
            val email = binding.emailEditText.text.toString().trim()
            val pass = binding.passwordEditText.text.toString().trim()
            val VerifyPass = binding.passwordReenterText.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty() && VerifyPass.isNotEmpty()){
                if(pass == VerifyPass){
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(
                        OnCompleteListener {
                            if (it.isSuccessful){
                                Toast.makeText(context, "Registered Sucessfully", Toast.LENGTH_SHORT).show()
                                navControl.navigate(R.id.action_navigation_SignUp_to_navigation_home)
                            }else{
                                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        })
                }
            }
        }*/

        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val pass = binding.passwordEditText.text.toString().trim()
            val verifyPass = binding.passwordReenterText.text.toString().trim()

            // Regular expression to validate email format
            val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")

            if (email.isNotEmpty() && pass.isNotEmpty() && verifyPass.isNotEmpty()) {
                if (pass == verifyPass) {
                    if (email.matches(emailRegex)) { // Check if email format is valid
                        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Registered Successfully", Toast.LENGTH_SHORT).show()
                                // Navigate to home fragment upon successful registration
                                navControl.navigate(R.id.action_navigation_SignUp_to_navigation_signIn)
                            } else {
                                // Handle registration failure
                                val errorMessage = task.exception?.message ?: "Registration failed"
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Invalid email format", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }


    }



}