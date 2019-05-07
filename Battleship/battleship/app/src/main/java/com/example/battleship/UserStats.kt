package com.example.battleship

import android.annotation.SuppressLint
import android.util.TypedValue
import android.view.Gravity
import android.widget.*
import com.google.firebase.firestore.DocumentReference
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
import java.math.BigDecimal
import java.math.RoundingMode
import kotlinx.android.synthetic.main.activity_user_dashboard.*
import kotlinx.android.synthetic.main.activity_user_stats.*


class UserStats : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val firestoreUser by lazy {
        FirebaseFirestore.getInstance().collection("Users")
    }
    private val firestoreGame by lazy {
        FirebaseFirestore.getInstance().collection("Games")
    }

    fun goBack(view: View) {
        val intent = Intent(this, UserDashboard::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.battleship.R.layout.activity_user_stats)

        auth = FirebaseAuth.getInstance()

        val UserID = auth.currentUser!!.uid
        val user = auth.currentUser

        val docRef = firestoreUser.document(user!!.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val welcomeText = findViewById<TextView>(com.example.battleship.R.id.welcome_text)
                    welcomeText.text = """     ${document.data!!["username"].toString()}"""

                    val startedL = arrayOf(firestoreGame.whereEqualTo("user1", UserID)).size
                    val joinedL = arrayOf(firestoreGame.whereEqualTo("user2", UserID).get()).size
                    val totalGames = startedL + joinedL

                    val wins = 0
                    val losses = 0

                    if(document.data!!["wins"] != null){
                        val wins = document.data!!["wins"].toString()
                    }
                    if(document.data!!["losses"] != null){
                        val losses = document.data!!["losses"].toString()
                    }

                    val total = wins + losses

                    winsDisplay.text = "Games Won: " + wins.toString()
                    gamesDisplay.text = "Games Played: " + total

                    //won't display if no wins for a more positive user experience. don't want people quiting after a loss or 2
                    if(wins>0 && losses>0){
                        lifetimeRatio.text = "Win Ratio (lifetime): " + BigDecimal(wins/losses).setScale(2, RoundingMode.HALF_EVEN)
                    }
                    else{
                        lifetimeRatio.text = "Win Ratio (lifetime): --"
                    }

                    if(totalGames>0){
                        startedRatio.text = "Started Ratio: " + BigDecimal(startedL/totalGames).setScale(2, RoundingMode.HALF_EVEN)
                    }
                    else{
                            startedRatio.text = "Started Ratio: --"
                    }

                } else {

                }
            }

        var winsText = "Game Wins:"
        var gameWins = "0"

        /*validateStats(firestoreGame,firestoreUser, UserID)

        val docRef = firestoreUser.document(user!!.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val gameWins = document.data!!["gameWins"].toString()
                }
            }

        winsText = "Game Wins:" + gameWins

        winsDisplay.text = winsText


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