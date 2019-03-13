package com.example.rps_attempt

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore





class UserDashboard : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val firestoreUser by lazy {
        FirebaseFirestore.getInstance().collection("Users")
    }
    private val firestoreGame by lazy {
        FirebaseFirestore.getInstance().collection("Games")
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
                    welcomeText.text = """     ${document.data!!["username"].toString()}"""
                } else {
                    Log.e(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "get failed with ", exception)
            }

        // Get all the active games where the logged in user is user1 or user2
        val active_games_ll = findViewById<LinearLayout>(R.id.active_games_linear_layout)
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(10, 10, 10, 0)

        val active_game_ref_1 = firestoreGame.whereEqualTo("user1", user!!.uid)
        active_game_ref_1.get()
            .addOnSuccessListener { document ->
                for(doc in document){
                    if (document != null) {
                        val activeGameButton = Button(this)
                        activeGameButton.text = doc.id
                        activeGameButton.setBackgroundColor(
                            resources.getColor(R.color.colorPrimary)
                        )
                        activeGameButton.setTextColor(
                            resources.getColor(R.color.white)
                        )
                        activeGameButton.setOnClickListener {
                            val intent = Intent(this, Gameplay::class.java)
                            startActivity(intent)
                        }
                        active_games_ll.addView(activeGameButton, lp)
                    }
                }
            }

        // This part gets all the games where the logged in user is user2
        // No user2 references yet so leaving it commented out.
        // Should probably rewrite these two sections into their own function to reduce redundant code

//        val active_game_ref_2 = firestoreGame.whereEqualTo("user2", user!!.uid)
//        active_game_ref_2.get()
//            .addOnSuccessListener { document ->
//                for(doc in document){
//                    if (document != null) {
//                        val activeGameButton = Button(this)
//                        activeGameButton.text = doc.id
//                        activeGameButton.setBackgroundColor(
//                            resources.getColor(R.color.colorPrimary))
//                        activeGameButton.setOnClickListener(
//                            goToGame()
//                        )
//                        active_games_ll.addView(activeGameButton, lp)
//                    }
//                }
//            }
    }

    /** Called when going to a gameplay page */
    fun createNewGame(view: View) {
        val uid = auth.currentUser!!.uid
        val newMessage = mapOf(
            "user1" to uid,
            "user2" to "",
            "status" to "active")

        firestoreGame.document().set(newMessage)
            .addOnSuccessListener {
                Log.e("NEW GAME", "SUCCESS")
            }
            .addOnFailureListener { e -> Log.e("ERROR", e.message) }

        goToGame(view)
    }

    /** Called when going to a gameplay page */
    fun goToGame(view: View) {
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
