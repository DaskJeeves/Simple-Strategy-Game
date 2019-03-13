package com.example.rps_attempt

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class Gameplay : AppCompatActivity() {

    val TAG = "GAMEPLAY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gameplay)

        val info = intent.extras
        if (info != null) {
            if(info.containsKey("tag")){
                val game_name = findViewById<TextView>(R.id.game_name)
                game_name.text = intent.getStringExtra("tag")
            }

        }else{
            Log.i(TAG, "INTENT INFO: NULL")
        }
    }

    fun choose_rock(view: View){
        val text = "You Chose Rock"
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
    }

    fun choose_paper(view: View){
        val text = "You Chose Paper"
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
    }

    fun finish_activity(view: View){
        finish()
    }
}
