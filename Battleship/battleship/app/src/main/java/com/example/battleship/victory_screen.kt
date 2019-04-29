package com.example.battleship

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_victory_screen)
        setSupportActionBar(toolbar)


        // GET THE USER THAT WON AND DISPLAY IT!

        /*var hitCount = 0
        for(doc in opponentShipsSnapshot) {
            val opponentShipid = resources.getIdentifier(doc.data!!["position"].toString().toLowerCase(), "id", packageName)
            for(doc in userShipsSnapshot) {
                val id = resources.getIdentifier(doc.data!!["position"].toString().toLowerCase(), "id", packageName)
                if (opponentShipid==id){
                    hitCount++
                }
            }
        }
        if (hitCount >= 6)
        {

            val intent = Intent(this, victory_screen::class.java).apply {
            }
            startActivity(intent)
        }
        */



    }
    fun gotoMenu(v: View) {
        val intent = Intent(this, UserDashboard::class.java)
        startActivity(intent)


    }

    fun createNewGame(view: View) {
        val uid = auth.currentUser!!.uid
        val newMessage = mapOf(
            "user1" to uid,
            "user2" to "",
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

}

