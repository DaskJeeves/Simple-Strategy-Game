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
    val hitColor = R.color.hit_color
    val moveColor = R.color.colorPrimary
    var userUid = ""
    var opponentUid = ""
    var user1 = ""
    var activeUser = ""
    var userShipsSet = false
    var userMoves = ArrayList<String>()
    var userShips = ArrayList<String>()
    var opponentShips = ArrayList<String>()
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

    // Test comment for pushing with gitkraken


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
                        userUid = auth.currentUser!!.uid
                        user1 = document.getString("user1").toString()
                        activeUser = document.getString("activeUser").toString()
                        opponentUid = if(document.getString("user2").toString() == auth.currentUser!!.uid){
                            document.getString("user1").toString()
                        }else{
                            document.getString("user2").toString()
                        }

                        loadSnapshots()
                        loadUserSnapshots()
                        realtimeUpdateListener()
                    }
                }
        }

        switchBoardView.setOnClickListener{
            if(currentBoardView == "user"){
                currentBoardView = "opponent"
                switchBoardView.text = "View My Board"
                loadOpponentSnapshots()
            }else{
                currentBoardView = "user"
                switchBoardView.text = "View Opponents Board"
                loadUserSnapshots()
            }
        }

//      realtimeUpdateListener()
    }

    fun clearButtons(){
        for(btn in allButtons){
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

    fun loadUserSnapshots(){
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

    fun loadOpponentSnapshots(){
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
            }
    }

    fun loadUserShips() {
        userShips = ArrayList<String>()
        if(userShipsSnapshot.size() > 5){
            userShipsSet = true
        }
        for(doc in userShipsSnapshot) {
            if (userShipsSnapshot != null) {
                val id = resources.getIdentifier(doc.data!!["position"].toString().toLowerCase(), "id", packageName)
                val shipButton = findViewById<TextView>(id)
                shipButton.setBackgroundResource(R.drawable.ship)
                userShips.add(doc.data!!["position"].toString().toLowerCase())
            }
        }
    }

    fun loadUserMoves() {
        userMoves = ArrayList<String>()
        for(doc in userMovesSnapshot) {
            if (userMovesSnapshot != null) {
                val id = resources.getIdentifier(doc.data!!["position"].toString().toLowerCase(), "id", packageName)
                val moveButton = findViewById<TextView>(id)
                moveButton.setBackgroundResource(moveColor)
                userMoves.add(doc.data!!["position"].toString().toLowerCase())
            }
        }
    }

    fun loadOpponentShips() {
        opponentShips = ArrayList<String>()
        Log.e("USER MOVES", userMoves.toString())
        for(doc in opponentShipsSnapshot) {
            Log.e("OPPONENT SHIP", doc.data.toString())
            if (opponentShipsSnapshot != null) {
                opponentShips.add(doc.data!!["position"].toString().toLowerCase())
                val id = resources.getIdentifier(doc.data!!["position"].toString().toLowerCase(), "id", packageName)
                val shipButton = findViewById<TextView>(id)
                Log.e("POSITION", doc.data!!["position"].toString().toLowerCase())
                if(userMoves.contains(doc.data!!["position"].toString().toLowerCase())){
                    shipButton.setBackgroundResource(R.drawable.ship_destroyed)
                }
            }
        }
    }

    fun loadOpponentMoves() {
        for(doc in opponentMovesSnapshot) {
            if (opponentMovesSnapshot != null) {
                val id = resources.getIdentifier(doc.data!!["position"].toString().toLowerCase(), "id", packageName)
                val moveButton = findViewById<TextView>(id)
                if(userShips.contains(doc.data!!["position"].toString().toLowerCase())){
                    moveButton.setBackgroundResource(R.drawable.ship_destroyed)
                }else{
                    moveButton.setBackgroundResource(R.drawable.miss)
                }
            }
        }
    }


    override fun onClick(v: View) {

        Log.e("USERSHIPSSET", userShipsSet.toString())
        Log.e("ACTIVEUSER", activeUser)
        Log.e("USERID", userUid)

        when (currentBoardView) {
            "user" ->

            if (!userShipsSet) {
                val newMessage = mapOf(
                    "position" to v.getTag().toString(),
                    "user" to auth.currentUser!!.uid,
                    "hit" to false,
                    "created" to FieldValue.serverTimestamp()
                )
                val firestoreShips = firestoreGame.collection("Ships")
                val userShips = firestoreShips.whereEqualTo("user", auth.currentUser!!.uid)
                userShips.get()
                    .addOnSuccessListener { document ->
                        Log.e("SIZE:", document.size().toString())
                        when (document.size()) {
                            in 0..4 ->
                                firestoreShips.document().set(newMessage)
                                .addOnSuccessListener {
                                    v.setBackgroundResource(R.drawable.ship)
                                }
                            5 ->
                                firestoreShips.document().set(newMessage)
                                .addOnSuccessListener {
                                    v.setBackgroundResource(R.drawable.ship)
                                    userShipsSet = true
                                    var setUserShips : Map<String, Any>
                                    if(user1 == userUid) {
                                         setUserShips = mapOf(
                                            "user1ShipsSet" to true
                                        )
                                    }
                                    else {
                                         setUserShips = mapOf(
                                            "user2ShipsSet" to true
                                            )
                                        }
                                    firestoreGame.set(setUserShips, SetOptions.merge())
                                }
                            else ->
                            userShipsSet = true
                        }
                    }

                }

        else ->
        if (activeUser == userUid && userShipsSet) {
            val newMessage = mapOf(
                "position" to v.getTag().toString(),
                "user" to auth.currentUser!!.uid,
                "created" to FieldValue.serverTimestamp()

            )
            val firestoreMoves = firestoreGame.collection("Moves")
                firestoreMoves.document().set(newMessage)
                    .addOnSuccessListener {
                        activeUser = opponentUid
                        val setActiveUser = mapOf (
                            "activeUser" to opponentUid
                        )
                        firestoreGame.set(setActiveUser, SetOptions.merge())
                        activePlayer = 0
                        if(opponentShips.contains(v.getTag().toString())){
                            v.setBackgroundResource(R.drawable.ship_destroyed)
                            var hitCount = 0
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

                                // WIN LOGIC GOES HERE
                            }
                        }else{
                            v.setBackgroundResource(R.drawable.miss)
                        }
                    }
                        .addOnFailureListener { e -> Log.e("ERROR", e.message) }
            }
        }
    }


    private fun realtimeUpdateListener() {

        firestoreGame.addSnapshotListener { document, e ->

            if(e != null) {
                Log.e("ERROR", e.message)
            }

            if(document != null && document.exists()) {
                activeUser = (document.getString("activeUser").toString())
            }
        }
    }
}


