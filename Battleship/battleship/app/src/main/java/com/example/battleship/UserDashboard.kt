package com.example.battleship

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_dashboard.*


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

    val allButtons = listOf(
        "a0", "a1", "a2", "a3", "a4", "a5",
        "b0", "b1", "b2", "b3", "b4", "b5",
        "c0", "c1", "c2", "c3", "c4", "c5",
        "d0", "d1", "d2", "d3", "d4", "d5",
        "e0", "e1", "e2", "e3", "e4", "e5",
        "f0", "f1", "f2", "f3", "f4", "f5"
    )


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
                    // Capture the layout's TextView and set the string as its text
                    val welcomeText = findViewById<TextView>(R.id.welcome_text)
                    welcomeText.text = """     ${document.data!!["username"].toString()}"""
                    if(document.data!!["wins"] != null){
                        user_wins.text = document.data!!["wins"].toString()
                    }else{
                        user_wins.text = "0"
                    }
                    if(document.data!!["losses"] != null){
                        user_losses.text = document.data!!["losses"].toString()
                    }else{
                        user_losses.text = "0"
                    }
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
            "created" to FieldValue.serverTimestamp(),
            "user1ShipsSet" to false,
            "user2ShipsSet" to false,
            "activeUser" to uid
        )

        val newGame = firestoreGame.document()

        newGame.set(newMessage)
            .addOnSuccessListener {
                Log.e("NEW GAME", "SUCCESS")

                createRandomComputerShips(newGame)

                val intent = Intent(this, Gameplay::class.java)
                intent.putExtra("tag", newGame.id)
                startActivity(intent)
            }
            .addOnFailureListener { e -> Log.e("ERROR", e.message) }

    }

    fun createRandomComputerShips(gameRef: DocumentReference){
        var i = 0
        var cShips = ArrayList<String>()
        while(i < 6){
            val firestoreShips = gameRef.collection("Ships")
            var randomPosition = allButtons.random()
            while(randomPosition in cShips){
                randomPosition = allButtons.random()
            }
            cShips.add(randomPosition)
            val newMessage = mapOf(
                "position" to randomPosition,
                "user" to "COMPUTER",
                "hit" to false,
                "created" to FieldValue.serverTimestamp()
            )
            Log.e("CPU SHIP:", randomPosition)
            firestoreShips.document().set(newMessage)
            i++
        }
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
        lp.setMargins(0, 10, 0, 0)

        val activeGameText = TextView(this)
        activeGameText.text = "ACTIVE GAMES"
        activeGameText.gravity = Gravity.CENTER
        activeGameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24.0F);
        active_games_ll.addView(activeGameText, lp)

        val active_game_ref_1 = firestoreGame.whereEqualTo("user1", user!!.uid)
        active_game_ref_1.get()
            .addOnSuccessListener { document ->
                for(doc in document){
                    if (document != null && !gameIds.contains(doc.id) && doc.data!!["status"] == "active") {
                        gameIds.add(doc.id)

                        val docRef = firestoreUser.document(doc.data!!["user2"].toString())
                        docRef.get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    val activeGameButton = Button(this)
                                    if(document.data?.get("username") != null){
                                        activeGameButton.text = document.data!!["username"].toString()
                                    }else{
                                        activeGameButton.text = "Computer"
                                    }
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
                            if (document != null && !gameIds.contains(doc.id) && doc.data!!["status"] == "active") {
                                gameIds.add(doc.id)

                                val docRef = firestoreUser.document(doc.data!!["user1"].toString())
                                docRef.get()
                                    .addOnSuccessListener { document ->
                                        if (document != null) {
                                            val activeGameButton = Button(this)
                                            if(document.data!!["username"] != null){
                                                activeGameButton.text = document.data!!["username"].toString()
                                            }else{
                                                activeGameButton.text = "Computer"
                                            }
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
