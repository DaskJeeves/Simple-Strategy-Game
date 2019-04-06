package com.example.rps_attempt

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_gameplay.*


class Gameplay : AppCompatActivity(), View.OnClickListener {

    var activePlayer = 1

    var MOVE_FIELD = "Move"

    lateinit var auth: FirebaseAuth

    lateinit var firestoreGame: DocumentReference

    val shipColor = R.color.colorAccent
    val moveColor = R.color.colorPrimary
    var userUid = ""
    var opponentUid = ""
    lateinit var userShipsSnapshot: QuerySnapshot
    lateinit var userMovesSnapshot: QuerySnapshot
    lateinit var opponentShipsSnapshot: QuerySnapshot
    lateinit var opponentMovesSnapshot: QuerySnapshot

    var currentBoardView = "user"

    val allButtons = listOf("a0", "a1", "a2", "a3", "a4", "a5",
        "b0", "b1", "b2", "b3", "b4", "b5",
        "c0", "c1", "c2", "c3", "c4", "c5",
        "d0", "d1", "d2", "d3", "d4", "d5",
        "e0", "e1", "e2", "e3", "e4", "e5",
        "f0", "f1", "f2", "f3", "f4", "f5" )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = setContentView(R.layout.activity_gameplay)


        //Initialize Auth Instance
        auth = FirebaseAuth.getInstance()


        val tag = intent.getStringExtra("tag")
        if(tag!=null){
            findViewById<TextView>(R.id.game_tag).text = tag
            firestoreGame = FirebaseFirestore.getInstance().collection("Games").document(tag)

            Log.e("TAG", tag)


            firestoreGame.get()
                .addOnSuccessListener { document ->
                    if(document != null){
                        Log.e("DARA", document.data.toString())
                        Log.e("USER", auth.currentUser!!.uid)
                        Log.e("USER2", document.data!!["user2"].toString())
                        Log.e("USER1", document.data!!["user1"].toString())
                        opponentUid = if(document.getString("user2").toString() == auth.currentUser!!.uid){
                            document.getString("user1").toString()
                        }else{
                            document.getString("user2").toString()
                        }

                        loadSnapshots()
                    }
                }
        }

        switchBoardView.setOnClickListener{
            clearButtons()
            if(currentBoardView == "user"){
                currentBoardView = "opponent"
                switchBoardView.text = "View My Board"
                loadOpponent()
            }else{
                currentBoardView = "user"
                switchBoardView.text = "View Opponents Board"
                loadUser()
            }
        }


//        realtimeUpdateListener()
    }

    fun clearButtons(){
        for(btn in allButtons){
            val button = findViewById<Button>(getResources().getIdentifier(btn, "id", packageName))
            button.setBackgroundResource(android.R.drawable.btn_default)
        }
    }

    fun loadUser() {
        loadUserShips()
        loadOpponentMoves()
    }

    fun loadOpponent() {
        Log.e("LOAD","OPPONENT")
        loadOpponentShips()
        loadUserMoves()
        Log.e("LOAD","OPPONENT2")
    }

    fun loadSnapshots() {
        val firestoreShips = firestoreGame.collection("Ships")
        val firestoreMoves = firestoreGame.collection("Moves")

        //USER SHIPS
        val userShips = firestoreShips.whereEqualTo("user", auth.currentUser!!.uid)
        userShips.get()
            .addOnSuccessListener { document ->
                userShipsSnapshot = document
                loadUserShips()
            }

        //USER MOVES
        val userMoves = firestoreMoves.whereEqualTo("user", auth.currentUser!!.uid)
        userMoves.get()
            .addOnSuccessListener { document ->
                userMovesSnapshot = document
            }

        //OPPONENT SHIPS
        val opponentShips = firestoreShips.whereEqualTo("user", opponentUid)
        opponentShips.get()
            .addOnSuccessListener { document ->
                opponentShipsSnapshot = document
            }

        //OPPONENT MOVES
        val opponentMoves = firestoreMoves.whereEqualTo("user", opponentUid)
        opponentMoves.get()
            .addOnSuccessListener { document ->
                opponentMovesSnapshot = document
                loadOpponentMoves()
            }
    }

    fun loadUserShips() {
        for(doc in userShipsSnapshot) {
            if (userShipsSnapshot != null) {
                val id = resources.getIdentifier(doc.data!!["position"].toString().toLowerCase(), "id", packageName)
                val shipButton = findViewById<TextView>(id)
                shipButton.setBackgroundResource(shipColor)
            }
        }
    }

    fun loadUserMoves() {
        for(doc in userMovesSnapshot) {
            if (userMovesSnapshot != null) {
                val id = resources.getIdentifier(doc.data!!["position"].toString().toLowerCase(), "id", packageName)
                val moveButton = findViewById<TextView>(id)
                moveButton.setBackgroundResource(moveColor)
            }
        }
    }

    fun loadOpponentShips() {
        Log.e("LOAD OPPONENT SHIPS", "")
        for(doc in opponentShipsSnapshot) {
            Log.e("OPPONENT SHIP", doc.data.toString())
            if (opponentShipsSnapshot != null) {
                val id = resources.getIdentifier(doc.data!!["position"].toString().toLowerCase(), "id", packageName)
                val shipButton = findViewById<TextView>(id)
                shipButton.setBackgroundResource(shipColor)
            }
        }
    }

    fun loadOpponentMoves() {
        for(doc in opponentMovesSnapshot) {
            if (opponentMovesSnapshot != null) {
                val id = resources.getIdentifier(doc.data!!["position"].toString().toLowerCase(), "id", packageName)
                val moveButton = findViewById<TextView>(id)
                moveButton.setBackgroundResource(moveColor)
            }
        }
    }


    override fun onClick(v: View) {

        if (activePlayer == 1) {
            val newMessage = mapOf(
                "position" to v.getTag().toString(),
                "user" to auth.currentUser!!.uid,
                "created" to FieldValue.serverTimestamp()
            )
            val firestoreMoves = firestoreGame.collection("Moves")
            val firestoreShips = firestoreGame.collection("Ships")
            val userShips = firestoreShips.whereEqualTo("user", auth.currentUser!!.uid)
            userShips.get()
                .addOnSuccessListener { document ->
                    Log.e("SIZE:", document.size().toString())
                    if (document.size() < 6) {
                        firestoreShips.document().set(newMessage)
                            .addOnSuccessListener {
                                v.setBackgroundResource(shipColor)
                            }
                            .addOnFailureListener { e -> Log.e("ERROR", e.message) }
                    } else {
                        firestoreMoves.document().set(newMessage)
                            .addOnSuccessListener {
                                activePlayer = 0
                                v.setBackgroundResource(moveColor)
                            }
                            .addOnFailureListener { e -> Log.e("ERROR", e.message) }
                    }
                }
                .addOnFailureListener { e -> Log.e("ERROR", e.message) }
        }
    }

/**
    private fun realtimeUpdateListener() {

        firestoreGame.addSnapshotListener { documentSnapshot, e ->

            when {

                e != null -> Log.e("ERROR", e.message)

                documentSnapshot != null && documentSnapshot.exists() -> {

                    with(documentSnapshot) {
                        if (activePlayer == 0){
                            var text = this!!.data?.get(MOVE_FIELD).toString() + " was selected"
                            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show()
                            activePlayer = 1
                        }


                    }

                }

            }

        }

    } */
}


