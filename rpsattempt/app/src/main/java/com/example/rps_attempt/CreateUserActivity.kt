package com.example.rps_attempt

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class CreateUserActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        Log.e("START", "AUTH")

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
         val currentUser = auth.currentUser
        Log.e("USER LOGGED IN", "MANAGE DATA HERE")
        Log.e("USER LOGGED IN", currentUser.toString())
        //  updateUI(currentUser)
    }

    fun createUser(view: View) {
        val usernameInput = findViewById<EditText>(R.id.new_user_username).text.toString()
        val passwordInput = findViewById<EditText>(R.id.new_user_password).text.toString()
        val emailInput = findViewById<EditText>(R.id.new_user_email).text.toString()
        createAccount(emailInput, passwordInput)
        finish()
    }

    private fun createAccount(email: String, password: String) {
        Log.d("createAccount", ":$email")

        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("createUserWithEmail", ":success")
                    val user = auth.currentUser
                    Log.e("USER IS", user.toString())
                    // updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("createUserWithEmail", ":failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    // updateUI(null)
                }

            }
        // [END create_user_with_email]
    }


}
