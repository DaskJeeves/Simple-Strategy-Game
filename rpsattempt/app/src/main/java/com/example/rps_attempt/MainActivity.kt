package com.example.rps_attempt

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser

        if (user != null) {
            // User is signed in
            Log.e("USER IS", user.toString())
        } else {
            // No user is signed in
            Log.e("USER IS", "NULL")
        }
    }


    /** Called when the user taps the login button */
    fun login(view: View) {
        val username = findViewById<EditText>(R.id.username_input)
        val uname = username.text.toString()
        val password = findViewById<EditText>(R.id.password_input)
        val pword = password.text.toString()

        if(uname != "" && pword != ""){
            auth.signInWithEmailAndPassword(uname, pword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val intent = Intent(this, UserDashboard::class.java).apply {
                            putExtra(EXTRA_MESSAGE, uname)
                        }
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(baseContext, "Invalid Email or Password.",
                            Toast.LENGTH_LONG).show()
                    }
                }
        }else{
            Toast.makeText(baseContext, "Please enter username and password.",
                Toast.LENGTH_LONG).show()
        }


    }

    /** Called when the user taps the create user button */
    fun createUser(view: View) {
        val intent = Intent(this, CreateUserActivity::class.java)
        startActivity(intent)
    }

}
