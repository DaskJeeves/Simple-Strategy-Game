package com.example.battleship

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

// test

class UserDashboard : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val firestoreUser by lazy {
        FirebaseFirestore.getInstance().collection("Users")
    }
    private val firestoreGame by lazy {
        FirebaseFirestore.getInstance().collection("Games")
    }
    private val TAG = "USERDASHBOARD"

    var gameIds = ArrayList<String>()


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)
        realtimeUpdateListener()

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

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

        findViewById<ImageButton>(R.id.sync_active_games).setOnClickListener{
            loadActiveGames()
        }
    }


    /** Called when going to a gameplay page */
    fun createNewSinglePlayerGame(view: View) {
        val uid = auth.currentUser!!.uid
        val newMessage = mapOf(
            "user1" to uid,
            "user2" to "COMPUTER",
            "status" to "active",
            "created" to FieldValue.serverTimestamp()
        )

        val newGame = firestoreGame.document()

        newGame.set(newMessage)
            .addOnSuccessListener {
                Log.e("NEW GAME", "SUCCESS")
            }
            .addOnFailureListener { e -> Log.e("ERROR", e.message) }

        val intent = Intent(this, Gameplay::class.java)
        intent.putExtra("tag", newGame.id)
        startActivity(intent)
    }

    /** Called when going to a gameplay page */
    fun goToGame(view: View) {
        val intent = Intent(this, Gameplay::class.java)
        startActivity(intent)
    }

    /** Called when going to a gameplay page */
    fun goToChooseUser(view: View) {
        val intent = Intent(this, ChooseUser::class.java)
        startActivity(intent)
    }

    fun loadActiveGames(){

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // Get all the active games where the logged in user is user1 or user2
        val active_games_ll = findViewById<LinearLayout>(R.id.active_games_linear_layout)
        val logout_button_ll = findViewById<LinearLayout>(R.id.logout_button)

        active_games_ll.removeAllViews()

        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(10, 10, 10, 0)


        val activeGameText = TextView(this)
        activeGameText.text = "ACTIVE GAMES"
        activeGameText.gravity = Gravity.CENTER
        activeGameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24.0F);
        active_games_ll.addView(activeGameText, lp)

        val active_game_ref_1 = firestoreGame.whereEqualTo("user1", user!!.uid)
        active_game_ref_1.get()
            .addOnSuccessListener { document ->
                for(doc in document){
                    if (document != null && !gameIds.contains(doc.id)) {
                        gameIds.add(doc.id)

                        val docRef = firestoreUser.document(doc.data!!["user2"].toString())
                        docRef.get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    val activeGameButton = Button(this)
                                    activeGameButton.text = document.data!!["username"].toString()
                                    activeGameButton.tag = doc.id
                                    activeGameButton.setBackgroundColor(
                                        resources.getColor(R.color.active_button)
                                    )
                                    activeGameButton.setTextColor(
                                        resources.getColor(R.color.white)
                                    )
                                    activeGameButton.setOnClickListener {
                                        val intent = Intent(this, Gameplay::class.java)
                                        intent.putExtra("tag", it.tag.toString())
                                        startActivity(intent)
                                    }
                                    active_games_ll.addView(activeGameButton, lp)
                                }
                            }
                    }
                }
                // This part gets all the games where the logged in user is user2
                // Should probably rewrite these two sections into their own function to reduce redundant code
                // Also sort by most recent move

                val active_game_ref_2 = firestoreGame.whereEqualTo("user2", user!!.uid)
                active_game_ref_2.get()
                    .addOnSuccessListener { document ->
                        for(doc in document){
                            if (document != null && !gameIds.contains(doc.id)) {
                                gameIds.add(doc.id)

                                val docRef = firestoreUser.document(doc.data!!["user1"].toString())
                                docRef.get()
                                    .addOnSuccessListener { document ->
                                        if (document != null) {
                                            val activeGameButton = Button(this)
                                            activeGameButton.text = document.data!!["username"].toString()
                                            activeGameButton.tag = doc.id
                                            activeGameButton.setBackgroundColor(
                                                resources.getColor(R.color.active_button)
                                            )
                                            activeGameButton.setTextColor(
                                                resources.getColor(R.color.white)
                                            )
                                            activeGameButton.setOnClickListener {
                                                val intent = Intent(this, Gameplay::class.java)
                                                intent.putExtra("tag", it.tag.toString())
                                                startActivity(intent)
                                            }
                                            active_games_ll.addView(activeGameButton, lp)
                                        }
                                    }
                            }
                        }

                        // LOGOUT BUTTON
                        val logoutButton = Button(this)
                        logoutButton.text = "LOGOUT"
                        logoutButton.setBackgroundColor(
                            resources.getColor(R.color.logout_button)
                        )
                        logoutButton.setTextColor(
                            resources.getColor(R.color.white)
                        )
                        logoutButton.setOnClickListener {
                            auth = FirebaseAuth.getInstance()
                            auth.signOut()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        logout_button_ll.addView(logoutButton, lp)
                    }
            }
    }

    private fun realtimeUpdateListener() {

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user != null) {
            firestoreGame.whereEqualTo("user1", user.uid).addSnapshotListener { documentSnapshot, e ->
                when {
                    e != null -> Log.e("ERROR", e.message)
                    documentSnapshot != null -> {
                        loadActiveGames()
                    }
                }
            }
//            firestoreGame.whereEqualTo("user2", user.uid).addSnapshotListener { documentSnapshot, e ->
//                when {
//                    e != null -> Log.e("ERROR", e.message)
//                    documentSnapshot != null -> {
//                        loadActiveGames()
//                    }
//                }
//            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loadActiveGames()
    }

    /** Called when the user taps the Send button */
    fun signOutUser(view: View) {
        auth = FirebaseAuth.getInstance()
        auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
