package com.example.rps_attempt

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.view.View
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    /** Called when the user taps the login button */
    fun login(view: View) {
        val username = findViewById<EditText>(R.id.username_input)
        val uname = username.text.toString()
        val intent = Intent(this, UserDashboard::class.java).apply {
            putExtra(EXTRA_MESSAGE, uname)
        }
        startActivity(intent)
    }

}
