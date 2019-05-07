package com.example.battleship

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.View
import com.google.common.net.InetAddresses.increment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.activity_victory_screen.*

class victory_screen : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val firestoreUser by lazy {
        FirebaseFirestore.getInstance().collection("Users")
    }
    private val firestoreGame by lazy {
        FirebaseFirestore.getInstance().collection("Games")
    }
    var opponent_uid = ""

    val allButtons = listOf(
        "a0", "a1", "a2", "a3", "a4", "a5",
        "b0", "b1", "b2", "b3", "b4", "b5",
        "c0", "c1", "c2", "c3", "c4", "c5",
        "d0", "d1", "d2", "d3", "d4", "d5",
        "e0", "e1", "e2", "e3", "e4", "e5",
        "f0", "f1", "f2", "f3", "f4", "f5"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_victory_screen)
        opponent_uid  = intent.getStringExtra("opponent")
        button5.text = "Play " + opponent_uid + " again"

        //INCREMENT WIN COUNT

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        val docRef = firestoreUser.document(user!!.uid)
        docRef.get().addOnSuccessListener {document ->
            if(document.data?.get("wins") != null){
                val newWins = document.data!!["wins"].toString().toInt() + 1
                docRef.update(mapOf(
                    "wins" to newWins)
                )
            }else{
                docRef.update(mapOf(
                    "wins" to 1)
                )
            }
        }
    }

    fun gotoMenu(v: View) {
        val intent = Intent(this, UserDashboard::class.java)
        startActivity(intent)
        finish()
    }

    /** Called when going to a gameplay page */
    fun createNewGame(view: View) {
        if (opponent_uid == "COMPUTER") createNewSinglePlayerGame()
        else createNewMultiplayerGame()
    }

    fun createNewMultiplayerGame() {
        val uid = auth.currentUser!!.uid
        val newMessage = mapOf(
            "user1" to uid,
            "user2" to opponent_uid,
            "user1ShipsSet" to false,
            "user2ShipsSet" to false,
            "activeUser" to uid,
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
        finish()
    }

    fun createNewSinglePlayerGame() {
        val uid = auth.currentUser!!.uid
        val newMessage = mapOf(
            "user1" to uid,
            "user2" to "COMPUTER",
            "status" to "active",
            "created" to FieldValue.serverTimestamp(),
            "user1ShipsSet" to false,
            "user2ShipsSet" to true,
            "activeUser" to uid
        )

        val newGame = firestoreGame.document()

        newGame.set(newMessage)
            .addOnSuccessListener {

                createRandomComputerShips(newGame)

                val intent = Intent(this, Gameplay::class.java)
                intent.putExtra("tag", newGame.id)
                startActivity(intent)
                finish()
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
            firestoreShips.document().set(newMessage)
            i++
        }
    }
}

