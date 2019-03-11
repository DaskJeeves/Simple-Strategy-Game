package com.example.rps_attempt

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val firestoreUser by lazy {
        FirebaseFirestore.getInstance().collection("Users")
    }
    private val TAG = "MAINACTIVITY"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

//        Uncomment if it breaks because of signed in user
         auth.signOut()

        val user = auth.currentUser

        if (user != null) {
            // User is signed in
            Log.e("USER IS", user.toString())
            val intent = Intent(this, UserDashboard::class.java)
            startActivity(intent)
        } else {
            // No user is signed in
            Log.e("USER IS", "NULL")
        }
    }


    /** Called when the user taps the login button */
    fun login(view: View) {
        val username = findViewById<EditText>(R.id.username_input)
        val uname = username.text.toString()
        val password = findViewById<EditText>(R.id.password_input)
        val pword = password.text.toString()

        if(uname != "" && pword != ""){
            signInEmail(uname, pword, true)
        }else{
            Log.e("4", "4")
            Toast.makeText(baseContext, "Please enter username and password.",
                Toast.LENGTH_LONG).show()
        }
    }

    private fun signInEmail(email: String, password: String, tryUsername: Boolean) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.e("USER IS", auth.currentUser.toString())
                    Log.e("EMAIL IS", auth.currentUser!!.email)
                    val user = auth.currentUser
                    if (user != null) {
                        if(user.displayName.toString() != ""){
                            Log.e("USERNAME", user.displayName.toString())
                        }
                    }
                    val intent = Intent(this, UserDashboard::class.java)
                    startActivity(intent)
                }
            }.addOnFailureListener {
                if(tryUsername){
                    signInUsername(email, password)
                }
            }
    }

    private fun signInUsername(username: String, password: String) {

        val docRef = firestoreUser.whereEqualTo("username", username)
        docRef.get()
            .addOnSuccessListener { document ->
                for(doc in document){
                    if (document != null) {
                        signInEmail(doc.data!!["email"].toString(), password, false)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "get failed with ", exception)
            }
    }

    /** Called when the user taps the create user button */
    fun createUser(view: View) {
        val intent = Intent(this, CreateUserActivity::class.java)
        intent.putExtra("GET USER", "")
        startActivityForResult(intent, 1)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val user = auth.currentUser
        if (user != null) {
            val intent = Intent(this, UserDashboard::class.java)
            startActivity(intent)
        }
    }

}
