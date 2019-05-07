package com.example.battleship

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
import android.content.Intent




class Gameplay : AppCompatActivity(), View.OnClickListener {

    var activePlayer = 1

    var MOVE_FIELD = "Move"

    lateinit var auth: FirebaseAuth

    lateinit var firestoreGame: DocumentReference
    lateinit var firestoreUser: DocumentReference

    val shipColor = R.color.colorAccent
    val hitColor = R.color.hit_color
    val moveColor = R.color.colorPrimary
    var userUid = ""
    var opponentUid = ""
    var user1 = ""
    var activeUser = ""
    var userShipsSet = false
    var opponentShipsSet = false
    var userMoves = ArrayList<String>()
    var userShips = ArrayList<String>()
    var opponentShips = ArrayList<String>()
    var opponentMoves = ArrayList<String>()
    lateinit var userShipsSnapshot: QuerySnapshot
    lateinit var userMovesSnapshot: QuerySnapshot
    lateinit var opponentShipsSnapshot: QuerySnapshot
    lateinit var opponentMovesSnapshot: QuerySnapshot

    var currentBoardView = "user"

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
        val root = setContentView(R.layout.activity_gameplay)


        //Initialize Auth Instance
        auth = FirebaseAuth.getInstance()

        //display yout name
        val uid = auth.currentUser!!.uid
        firestoreUser = FirebaseFirestore.getInstance().collection("Users").document(uid)
        firestoreUser.get()
            .addOnSuccessListener { document ->
                val Username = document.getString("username").toString()
                user1Name.text = """${Username}"""
            }

        val tag = intent.getStringExtra("tag")
        if (tag != null) {
            findViewById<TextView>(R.id.user2Name).text = tag
            firestoreGame = FirebaseFirestore.getInstance().collection("Games").document(tag)

            firestoreGame.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        userUid = auth.currentUser!!.uid
                        user1 = document.getString("user1").toString()
                        activeUser = document.getString("activeUser").toString()
                        opponentUid = if (document.getString("user2").toString() == auth.currentUser!!.uid) {
                            document.getString("user1").toString()
                        } else {
                            document.getString("user2").toString()
                        }
                        loadSnapshots()
                        loadUserSnapshots()

                        if(opponentUid == "COMPUTER"){
                            user2Name.text = """Computer"""
                        }else{
                            firestoreUser = FirebaseFirestore.getInstance().collection("Users").document(opponentUid)
                            firestoreUser.get()
                                .addOnSuccessListener { document ->
                                    val opponentUsername = document.getString("username").toString()
                                    user2Name.text = """Game with ${opponentUsername}"""
                                }
                        }

                        realtimeUpdateListener()
                    }
                }
        }

        switchBoardView.setOnClickListener {
            if (currentBoardView == "user") {
                currentBoardView = "opponent"
                switchBoardView.text = "View My Board"
                loadOpponentSnapshots()
            } else {
                currentBoardView = "user"
                switchBoardView.text = "View Opponents Board"
                loadUserSnapshots()
            }
        }

        Log.e("OPPONENT UID", opponentUid)

    }

    fun clearButtons() {
        for (btn in allButtons) {
            val button = findViewById<Button>(getResources().getIdentifier(btn, "id", packageName))
            button.setBackgroundResource(R.drawable.empty)
        }
    }

    fun loadUser() {
        clearButtons()
        loadUserShips()
        loadOpponentMoves()
    }

    fun loadOpponent() {
        clearButtons()
        loadUserMoves()
        loadOpponentShips()
    }

    fun loadUserSnapshots() {
        val firestoreShips = firestoreGame.collection("Ships")
        val firestoreMoves = firestoreGame.collection("Moves")

        //USER SHIPS
        val userShips = firestoreShips.whereEqualTo("user", auth.currentUser!!.uid)
        userShips.get()
            .addOnSuccessListener { document ->
                userShipsSnapshot = document
                //OPPONENT MOVES
                val opponentMoves = firestoreMoves.whereEqualTo("user", opponentUid)
                opponentMoves.get()
                    .addOnSuccessListener { document ->
                        opponentMovesSnapshot = document
                        loadUser()
                    }
            }
    }

    fun loadOpponentSnapshots() {
        val firestoreShips = firestoreGame.collection("Ships")
        val firestoreMoves = firestoreGame.collection("Moves")


        //OPPONENT SHIPS
        val opponentShips = firestoreShips.whereEqualTo("user", opponentUid)
        opponentShips.get()
            .addOnSuccessListener { document ->
                opponentShipsSnapshot = document
                //USER MOVES
                val userMoves = firestoreMoves.whereEqualTo("user", auth.currentUser!!.uid)
                userMoves.get()
                    .addOnSuccessListener { document ->
                        userMovesSnapshot = document
                        loadOpponent()
                    }
            }
    }

    fun loadSnapshots() {
        val firestoreShips = firestoreGame.collection("Ships")
        val firestoreMoves = firestoreGame.collection("Moves")

        //USER SHIPS
        val userShips = firestoreShips.whereEqualTo("user", auth.currentUser!!.uid)
        userShips.get()
            .addOnSuccessListener { document ->
                userShipsSnapshot = document

                //USER MOVES
                val userMoves = firestoreMoves.whereEqualTo("user", auth.currentUser!!.uid)
                userMoves.get()
                    .addOnSuccessListener { document ->
                        userMovesSnapshot = document

                        //OPPONENT SHIPS
                        val opponentShips = firestoreShips.whereEqualTo("user", opponentUid)
                        opponentShips.get()
                            .addOnSuccessListener { document ->
                                opponentShipsSnapshot = document

                                //OPPONENT MOVES
                                val opponentMoves = firestoreMoves.whereEqualTo("user", opponentUid)
                                opponentMoves.get()
                                    .addOnSuccessListener { document ->
                                        opponentMovesSnapshot = document
                                        loadAll()
                                        checkForWin()
                                    }
                            }
                    }
            }
    }

    fun loadUserShips() {
        userShips = ArrayList<String>()
        if (userShipsSnapshot.size() > 5) {
            userShipsSet = true
        }
        for (doc in userShipsSnapshot) {
            if (userShipsSnapshot != null) {
                val id = resources.getIdentifier(doc.data!!["position"].toString().toLowerCase(), "id", packageName)
                val shipButton = findViewById<TextView>(id)
                shipButton.setBackgroundResource(R.drawable.ship)
                userShips.add(doc.data!!["position"].toString().toLowerCase())
            }
        }
    }

    fun loadUserMoves() {
        userMoves.clear()
        for (doc in userMovesSnapshot) {
            if (userMovesSnapshot != null) {
                val id = resources.getIdentifier(doc.data!!["position"].toString().toLowerCase(), "id", packageName)
                val moveButton = findViewById<TextView>(id)
                if (opponentShips.contains(doc.data!!["position"].toString().toLowerCase())) {
                    moveButton.setBackgroundResource(R.drawable.ship_destroyed)
                } else {
                    moveButton.setBackgroundResource(R.drawable.miss)
                }
                userMoves.add(doc.data!!["position"].toString().toLowerCase())
            }
        }
    }

    fun loadOpponentShips() {
        opponentShips = ArrayList<String>()
        if (opponentShipsSnapshot.size() > 5) {
            opponentShipsSet = true
        }
        for (doc in opponentShipsSnapshot) {
            if (opponentShipsSnapshot != null) {
                opponentShips.add(doc.data!!["position"].toString().toLowerCase())
                val id = resources.getIdentifier(doc.data!!["position"].toString().toLowerCase(), "id", packageName)
                val shipButton = findViewById<TextView>(id)
                if (userMoves.contains(doc.data!!["position"].toString().toLowerCase())) {
                    shipButton.setBackgroundResource(R.drawable.ship_destroyed)
                }
            }
        }
    }

    fun loadOpponentMoves() {
        opponentMoves.clear()
        for (doc in opponentMovesSnapshot) {
            if (opponentMovesSnapshot != null) {
                opponentMoves.add(doc.data!!["position"].toString().toLowerCase())
                val id = resources.getIdentifier(doc.data!!["position"].toString().toLowerCase(), "id", packageName)
                val moveButton = findViewById<TextView>(id)
                if (userShips.contains(doc.data!!["position"].toString().toLowerCase())) {
                    moveButton.setBackgroundResource(R.drawable.ship_destroyed)
                } else {
                    moveButton.setBackgroundResource(R.drawable.miss)
                }
            }
        }
    }


    override fun onClick(v: View) {

        when (currentBoardView) {
            "user" ->
                if (userShipsSet) { Toast.makeText(this, "All your ships are set", Toast.LENGTH_LONG).show() }
                else {
                    var position = v.getTag().toString()
                    if (position in userShips) { Toast.makeText(this, "You already have a ship there!", Toast.LENGTH_LONG).show() }
                    else {
                        val newMessage = mapOf(
                            "position" to position,
                            "user" to auth.currentUser!!.uid,
                            "hit" to false,
                            "created" to FieldValue.serverTimestamp()
                        )
                        val firestoreShips = firestoreGame.collection("Ships")
                        val userPlacements = firestoreShips.whereEqualTo("user", auth.currentUser!!.uid)
                        userPlacements.get()
                            .addOnSuccessListener { document ->
                                when (document.size()) {
                                    in 0..4 -> {
                                        userShips.add(position)
                                        firestoreShips.document().set(newMessage)
                                            .addOnSuccessListener {
                                                v.setBackgroundResource(R.drawable.ship)
                                            }
                                    }
                                    5 -> {
                                        userShips.add(position)
                                        firestoreShips.document().set(newMessage)
                                            .addOnSuccessListener {
                                                v.setBackgroundResource(R.drawable.ship)
                                                userShipsSet = true
                                                var setUserShips: Map<String, Any>
                                                if (user1 == userUid) {
                                                    setUserShips = mapOf(
                                                        "user1ShipsSet" to true
                                                    )
                                                } else {
                                                    setUserShips = mapOf(
                                                        "user2ShipsSet" to true
                                                    )
                                                }
                                                firestoreGame.set(setUserShips, SetOptions.merge())
                                            }
                                    }
                                    else -> {
                                        userShipsSet = true
                                        Toast.makeText(this, "All your ships are set", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                    }
                }
            "opponent" ->
                if (!userShipsSet) {Toast.makeText(this, "Set your ships before making your move!", Toast.LENGTH_LONG).show()}
                else if (!opponentShipsSet) {Toast.makeText(this, "Opponent hasn't set their ships!", Toast.LENGTH_LONG).show()}
                else if (activeUser != auth.currentUser!!.uid) {Toast.makeText(this, "It's not your turn!", Toast.LENGTH_LONG).show()}
                else {
                    var position = v.getTag().toString()
                    if (position in userMoves) {Toast.makeText(this, "You already made a move there!", Toast.LENGTH_LONG).show()}
                    else {
                        val newMessage = mapOf(
                            "position" to position,
                            "user" to auth.currentUser!!.uid,
                            "created" to FieldValue.serverTimestamp()
                        )
                        userMoves.add(position)
                        val firestoreMoves = firestoreGame.collection("Moves")
                        firestoreMoves.document().set(newMessage)
                            .addOnSuccessListener {
                                if (opponentShips.contains(v.getTag().toString())) {
                                    v.setBackgroundResource(R.drawable.ship_destroyed)
                                    checkForWin()
                                } else {
                                    v.setBackgroundResource(R.drawable.miss)
                                }
                                if (opponentUid == "COMPUTER") {
                                    setComputerMove()
                                } else {
                                    activeUser = opponentUid
                                    val setActiveUser = mapOf(
                                        "activeUser" to opponentUid
                                    )
                                    firestoreGame.set(setActiveUser, SetOptions.merge())
                                    activePlayer = 0
                                }

                            }.addOnFailureListener { e -> Log.e("ERROR", e.message) }
                    }
                }
        }

    }

    fun loadAll() {
        userShips.clear()
        for (doc in userShipsSnapshot) {
            if (userShipsSnapshot != null) {
                userShips.add(doc.data!!["position"].toString().toLowerCase())
            }
        }
        userMoves.clear()
        for (doc in userMovesSnapshot) {
            if (userMovesSnapshot != null) {
                userMoves.add(doc.data!!["position"].toString().toLowerCase())
            }
        }
        opponentShips.clear()
        for (doc in opponentShipsSnapshot) {
            if (opponentShipsSnapshot != null) {
                opponentShips.add(doc.data!!["position"].toString().toLowerCase())
            }
        }
        opponentMoves.clear()
        for (doc in opponentMovesSnapshot) {
            if (opponentMovesSnapshot != null) {
                opponentMoves.add(doc.data!!["position"].toString().toLowerCase())

            }
        }

    }

    fun updateHits(){
        var hitCount = 0
        for(move in userMoves){
            if(move in opponentShips){
                hitCount++
            }
        }
        var opponentHitCount = 0
        for(move in opponentMoves){
            if(move in userShips){
                opponentHitCount++
            }
        }

        user1Ships.text = hitCount.toString() + "/6"
        user2Ships.text = opponentHitCount.toString() + "/6"

    }


    fun checkForWin(){

        updateHits()

        var hitCount = 0
        for(move in userMoves){
            if(move in opponentShips){
                hitCount++
            }
        }
        if (hitCount > 5) {
            firestoreGame.update("status", "inactive")
            val intent = Intent(this, victory_screen::class.java)
            intent.putExtra("opponent", opponentUid)
            startActivity(intent)
            finish()
        }
        var opponentHitCount = 0
        for(move in opponentMoves){
            if(move in userShips){
                opponentHitCount++
            }
        }
        if (opponentHitCount > 5) {
            // LOSS SCREEN
            firestoreGame.update("status", "inactive")
            val intent = Intent(this, lose_screen::class.java)
            intent.putExtra("opponent", opponentUid)
            startActivity(intent)
            finish()
        }

    }

    fun setComputerMove(){

        val firestoreMoves = firestoreGame.collection("Moves")
        var randomPosition = allButtons.random()
        while(randomPosition in opponentMoves){
            randomPosition = allButtons.random()
        }
        val newMessage = mapOf(
            "position" to randomPosition,
            "user" to "COMPUTER",
            "created" to FieldValue.serverTimestamp()
        )
        opponentMoves.add(randomPosition)
        firestoreMoves.document().set(newMessage)
        checkForWin()
    }


    fun realtimeUpdateListener() {

        firestoreGame.addSnapshotListener { document, e ->
            if (e != null) {
                Log.e("ERROR", e.message)
            }

            if (document != null && document.exists()) {
                activeUser = (document.getString("activeUser").toString())
                if (user1 == auth.currentUser!!.uid) {
                    opponentShipsSet = (document.getBoolean("user2ShipsSet")!!)
                } else {
                    opponentShipsSet = (document.getBoolean("user1ShipsSet")!!)
                }
                if (currentBoardView == "user") loadUserSnapshots()
                loadSnapshots()
            }
        }
    }

}

