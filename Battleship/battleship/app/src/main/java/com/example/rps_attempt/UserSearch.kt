package com.example.rps_attempt

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_search.*

class UserSearch : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val firestoreUser by lazy {
        FirebaseFirestore.getInstance().collection("Users")
    }
    private val firestoreGame by lazy {
        FirebaseFirestore.getInstance().collection("Games")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_search)

        auth = FirebaseAuth.getInstance()
    }

    fun searchUsers(view: View){
        var count = 0
        val userOrEmail = name_input.text.toString()

        val docRef = firestoreUser.whereEqualTo("username", userOrEmail)
        val emailRef = firestoreUser.whereEqualTo("email", userOrEmail)

        if ((searched_users_ll).childCount > 0){
            (searched_users_ll).removeAllViews()
        }

        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(10, 10, 10, 0)


        docRef.get()
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
                        searched_users_ll.addView(activeUserButton, lp)
                        count += 1
                    }
                }
                emailRef.get()
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
                                searched_users_ll.addView(activeUserButton, lp)
                                count += 1
                            }
                        }
                        if(count == 0){
                            val noUserText = TextView(this)
                            noUserText.text = "No users found"
                            searched_users_ll.addView(noUserText, lp)
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
            }
            .addOnFailureListener { e -> Log.e("ERROR", e.message) }

        val intent = Intent(this, Gameplay::class.java)
        intent.putExtra("tag", newGame.id)
        startActivity(intent)
        finish()
    }
}
