package com.example.firechattest

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    val COLLECTION_KEY = "Chat"
    val DOCUMENT_KEY = "Message"
    var NAME_FIELD = "Name"
    var TEXT_FIELD = "Text"


    private val firestoreChat by lazy {
        FirebaseFirestore.getInstance().collection(COLLECTION_KEY).document(DOCUMENT_KEY)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        realtimeUpdateListener()

        val buttonSendMessage = findViewById<Button>(R.id.send_message)

        buttonSendMessage.setOnClickListener{ sendMessage() }
    }

    private fun sendMessage() {
        val edit_name = findViewById<EditText>(R.id.edit_name)
        val edit_message = findViewById<EditText>(R.id.edit_message)
        val newMessage = mapOf(
            NAME_FIELD to edit_name.text.toString(),
            TEXT_FIELD to edit_message.text.toString())
        firestoreChat.set(newMessage)
            .addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Message Sent", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e -> Log.e("ERROR", e.message) }
    }

    @SuppressLint("SetTextI18n")
    private fun realtimeUpdateListener() {
        val textDisplay = findViewById<TextView>(R.id.text_display)
        val userDisplay = findViewById<TextView>(R.id.user_display)
        firestoreChat.addSnapshotListener { documentSnapshot, e ->
            when {
                e != null -> Log.e("ERROR", e.message)
                documentSnapshot != null && documentSnapshot.exists() -> {
                    Log.e("DOC DATA", documentSnapshot?.data.toString())
                    Log.e("DOC DATA", documentSnapshot.data?.get(NAME_FIELD).toString())
                    Log.e("DOC DATA", documentSnapshot.data?.get(TEXT_FIELD).toString())
                    with(documentSnapshot) {
                        textDisplay.text = "${documentSnapshot.data?.get(TEXT_FIELD)}"
                        userDisplay.text = "From: ${documentSnapshot.data?.get(NAME_FIELD)}"
                    }
                }
            }
        }
    }
}
