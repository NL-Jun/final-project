package com.example.finalproject.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.finalproject.R
import com.google.firebase.auth.FirebaseAuth


/*class SplashFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)


        val handler = Handler(Looper.myLooper()!!)
        handler.postDelayed(Runnable {
            if(auth.currentUser == null ){
                navController.navigate(R.id.action_navigation_splash_to_activity1)
            }else{
                navController.navigate(R.id.action_navigation_splash_to_activity2)
            }

        }, 1000)
    }


    private fun init(view: View){
        auth = FirebaseAuth.getInstance()
        navController = Navigation.findNavController(view)
    }


}*/

class SplashFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide the bottom navigation menu
        /*(activity as? AppCompatActivity)?.supportActionBar?.hide()
        activity?.findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.GONE*/


        init(view)


        Handler(Looper.getMainLooper()).postDelayed({

            val currentUser = auth.currentUser
            if (::navController.isInitialized) {
                // Check user login status and navigate accordingly
                if (currentUser == null) {
                    //User not signed in, go to sign in fragment
                    navController.navigate(R.id.action_navigation_splash_to_navigation_signIn)
                } else {
                    //User is signed in, go to sign home fragment
                    navController.navigate(R.id.action_navigation_splash_to_navigation_home)
                }
            }
        }, 6000)
    }

    private fun init(view: View) {
        auth = FirebaseAuth.getInstance()
        navController = Navigation.findNavController(view)
    }
}

