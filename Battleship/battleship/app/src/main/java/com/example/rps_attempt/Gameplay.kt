package com.example.rps_attempt

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore






class Gameplay : AppCompatActivity(), View.OnClickListener {

    var activePlayer = 1

    val COLLECTION_KEY = "Game"

    val DOCUMENT_KEY = "Room"

    var MOVE_FIELD = "Move"

    val firestoreChat by lazy{

        FirebaseFirestore.getInstance().collection(COLLECTION_KEY).document(DOCUMENT_KEY)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = setContentView(R.layout.activity_gameplay)
        realtimeUpdateListener()


        val tag = intent.getStringExtra("tag")
        findViewById<TextView>(R.id.game_tag).text = tag

    }



    override fun onClick(v: View) {

        Log.e("a","error occured")

        val newMessage = mapOf(

            MOVE_FIELD to v.getTag().toString())

        firestoreChat.set(newMessage)

            .addOnSuccessListener {

                //Toast.makeText(this@Gameplay, "Message Sent", Toast.LENGTH_SHORT).show()
                activePlayer = 0
            }

            .addOnFailureListener { e -> Log.e("ERROR", e.message) }

    }


    private fun realtimeUpdateListener() {

        firestoreChat.addSnapshotListener { documentSnapshot, e ->

            when {

                e != null -> Log.e("ERROR", e.message)

                documentSnapshot != null && documentSnapshot.exists() -> {

                    with(documentSnapshot) {
                        if (activePlayer == 0){
                            var text = this!!.data?.get(MOVE_FIELD).toString() + " was selected"
                            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                            activePlayer = 1
                        }


                    }

                }

            }

        }

    }
}


