package com.example.rps_attempt

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore





class UserDashboard : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val firestoreUser by lazy {
        FirebaseFirestore.getInstance().collection("Users")
    }
    private val TAG = "USERDASHBOARD"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        Log.e("USER", user!!.uid)

        val docRef = firestoreUser.document(user!!.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.e(TAG, "DocumentSnapshot data: " + document.data)
                    // Capture the layout's TextView and set the string as its text
                    val welcomeText = findViewById<TextView>(R.id.welcome_text)
                    welcomeText.text = """   ${document.data!!["username"].toString()}"""
                } else {
                    Log.e(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "get failed with ", exception)
            }


    }



    /** Called when the user taps the Send button */
    fun playRockPaperScissors(view: View) {
        val intent = Intent(this, Gameplay::class.java)
        startActivity(intent)
    }

    /** Called when the user taps the Send button */
    fun signOutUser(view: View) {
        auth = FirebaseAuth.getInstance()
        auth.signOut()
        finish()
    }
}
