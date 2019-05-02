package com.example.rps_attempt

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import android.R
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_user_stats.*


class UserStats : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val firestoreUser by lazy {
        FirebaseFirestore.getInstance().collection("Users")
    }
    private val firestoreGame by lazy {
        FirebaseFirestore.getInstance().collection("Games")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.rps_attempt.R.layout.activity_user_stats)

        auth = FirebaseAuth.getInstance()

        var winsText = "Game Wins:"
        var gameWins = "0"

        val UserID = auth.currentUser!!.uid
        val user = auth.currentUser

        //validateStats(firestoreGame,firestoreUser, UserID)

        val docRef = firestoreUser.document(user!!.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val gameWins = document.data!!["gameWins"].toString()
                }
            }

        winsText = "Game Wins:" + gameWins

        winsDisplay.text = winsText

        /*
        val docRef = firestoreUser.whereEqualTo("username", username)
        docRef.get()
            .addOnSuccessListener { document ->
                for (doc in document) {
                }
        }*/
    }
}

fun validateStats(firestoreGame : CollectionReference ,firestoreUser : CollectionReference , uid : String){

    firestoreUser.get()
        .addOnSuccessListener { document ->
            if(document != null){
                //val username = document.getString("username").toString()
            }
        }

    val newStats = mapOf(
        "gamesplayed" to 0,
        "gamewins" to 0
    )

    firestoreUser.document(uid).set(newStats)
        .addOnSuccessListener {

        }
        .addOnFailureListener { e -> Log.e("ERROR", e.message) }
}
