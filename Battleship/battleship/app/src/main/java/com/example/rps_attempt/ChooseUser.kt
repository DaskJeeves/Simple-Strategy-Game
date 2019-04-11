package com.example.rps_attempt

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ChooseUser : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val firestoreUser by lazy {
        FirebaseFirestore.getInstance().collection("Users")
    }
    private val firestoreGame by lazy {
        FirebaseFirestore.getInstance().collection("Games")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_user)

        auth = FirebaseAuth.getInstance()

        loadActiveUsers()
    }

    fun loadActiveUsers(){

        val active_users_ll = findViewById<LinearLayout>(R.id.active_users_ll)

        if ((active_users_ll).childCount > 0){
            (active_users_ll).removeAllViews()
        }

        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(10, 10, 10, 0)


        firestoreUser.get()
            .addOnSuccessListener { document ->
                for (doc in document) {
                    if (document != null) {
                        val activeUserButton = Button(this)
                        activeUserButton.text = doc.data["username"].toString()
                        activeUserButton.tag = doc.id
                        activeUserButton.setBackgroundColor(
                            resources.getColor(R.color.colorAccent)
                        )
                        activeUserButton.setTextColor(
                            resources.getColor(R.color.white)
                        )
                        activeUserButton.setOnClickListener {
                            createNewGame(it.tag.toString())
                        }
                        active_users_ll.addView(activeUserButton, lp)
                    }
                }
            }
    }


    /** Called when going to a gameplay page */
    fun createNewGame(opponent_uid: String) {
        val uid = auth.currentUser!!.uid
        val newMessage = mapOf(
            "user1" to uid,
            "user2" to opponent_uid,
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

    fun goToUserSearch(view: View){
        val intent = Intent(this, UserSearch::class.java)
        startActivity(intent)
        finish()
    }
}
