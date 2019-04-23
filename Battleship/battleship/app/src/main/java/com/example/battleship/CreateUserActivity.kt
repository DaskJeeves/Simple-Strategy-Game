package com.example.battleship

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TEST

class CreateUserActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    private val firestoreUser by lazy {
        FirebaseFirestore.getInstance().collection("Users")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
         val currentUser = auth.currentUser
        Log.e("USER LOGGED IN", "MANAGE DATA HERE")
        if (currentUser != null) {
            Log.e("USER LOGGED IN", currentUser.email)
        }
        //  updateUI(currentUser)
    }

    fun createUser(view: View) {
        val usernameInput = findViewById<EditText>(R.id.new_user_username).text.toString()
        val passwordInput = findViewById<EditText>(R.id.new_user_password).text.toString()
        val emailInput = findViewById<EditText>(R.id.new_user_email).text.toString()
        Log.e("CREATE", "USER")
        createAccount(emailInput, passwordInput, usernameInput)
    }

    private fun createAccount(email: String, password: String, username: String) {
        Log.e("createAccount", ":$email")

        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                Log.e("TASK IS", task.toString())
                if (task.isSuccessful) {
                    Log.e("User created", ":success")
                    val user = auth.currentUser
                    // updateUI(user)

                    if (user != null) {

                        // Add display name
//                        val profileUpdates = UserProfileChangeRequest.Builder()
//                            .setDisplayName(username)
//                            //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
//                            .build()
//
//                        user?.updateProfile(profileUpdates)
//                            ?.addOnCompleteListener { task ->
//                                if (task.isSuccessful) {
//                                    Log.e("updated username to ", username)
//                                } else {
//                                    Log.e("failed update to ", username)
//                                }
//                            }

                        // Send verification email
//                        user?.sendEmailVerification()
//                            ?.addOnCompleteListener { task ->
//                                if (task.isSuccessful) {
//                                    Log.e("Email verification.", "Sent")
//                                } else {
//                                    Log.e("failed verification to ", email)
//                                }
//                            }


                        val uid = auth.currentUser!!.uid
                        val newMessage = mapOf(
                            "username" to username,
                            "email" to email)
                        firestoreUser.document(uid).set(newMessage)
                            .addOnSuccessListener {
                                Toast.makeText(this@CreateUserActivity, "User Created", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e -> Log.e("ERROR", e.message) }

                        auth.signInWithEmailAndPassword(email, username)

                        val i = Intent()
                        i.putExtra("user", username)
                        setResult(RESULT_OK, i)
                        finish()
                    }
                } else {
                    // If create user fails, display a message to the user.
                    Log.e("createUserWithEmail", ":failure", task.exception)
                    Log.e("EXCEPTION", task.exception!!.message.toString())
                    if(task.exception.toString().contains("FirebaseAuthWeakPasswordException")){
                        Log.e("SHOW", "WEAK PASSWORD MESSAGE")
                    }
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    // updateUI(null)
                }
            }

    }


}
