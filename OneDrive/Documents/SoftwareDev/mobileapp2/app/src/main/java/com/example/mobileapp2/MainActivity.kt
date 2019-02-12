package com.example.mobileapp2

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*

import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Math.random
import java.util.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase



const val EXTRA_MESSAGE = "This is my first message"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            // Write a message to the database
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("message")
            myRef.setValue("Hello, World!")
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }


        /*val toggleButton = findViewById<ToggleButton>(R.id.button2)
        toggleButton?.setOnCheckedChangeListener { buttonView, isChecked ->
            val msg = "Toggle Button is " + if (isChecked) "ON" else "OFF"
            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()

            val helpText = findViewById<TextView>(R.id.textView2)
            if(isChecked) helpText.visibility = View.VISIBLE else helpText.visibility = View.INVISIBLE
        }
    */

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                true
            }
            R.id.help_menu_item -> {
                val helpText = findViewById<TextView>(R.id.textView2)
                helpText.visibility = View.VISIBLE
                true
            }
            R.id.two_brothers -> {
                val brother1 = findViewById<Button>(R.id.brother1)
                brother1.visibility = View.VISIBLE
                val brother2 = findViewById<Button>(R.id.brother2)
                brother2.visibility = View.VISIBLE
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    /** Called when the user taps the Send button */
    /*fun sendMessage(view: View) {
        val editText = findViewById<EditText>(R.id.editText)
        val message = editText.text.toString()
        val intent = Intent(this, DisplayMessageActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, message)
        }
        startActivity(intent)
    }
    */
    /** Called when the user taps the Send button */
    fun chooseRock(view: View) {
        val intent = Intent(this, DisplayMessageActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, "You Chose Rock")
            var selection = (0..2).random();
            when(selection){
                0 -> putExtra(EXTRA_MESSAGE, "CPU chose rock. \n It's a draw, try again.")
                1 -> putExtra(EXTRA_MESSAGE, "CPU chose paper. \n CPU wins, try again.")
                2 -> putExtra(EXTRA_MESSAGE, "CPU chose scissors. \n You win!")
            }
        }
        startActivity(intent)
    }

    /** Called when the user taps the Send button */
    fun choosePaper(view: View) {
        val intent = Intent(this, DisplayMessageActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, "You Chose Paper")
            var selection = (0..2).random();
            when(selection){
                0 -> putExtra(EXTRA_MESSAGE, "CPU chose rock. \n You Win!")
                1 -> putExtra(EXTRA_MESSAGE, "CPU chose paper. \n It's a draw, try again.")
                2 -> putExtra(EXTRA_MESSAGE, "CPU chose scissors. \n CPU wins, try again.")
            }
        }
        startActivity(intent)
    }

    /** Called when the user taps the Send button */
    fun chooseScissors(view: View) {
        val intent = Intent(this, DisplayMessageActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, "You Chose Scissors")
            var selection = (0..2).random();
            when(selection){
                0 -> putExtra(EXTRA_MESSAGE, "CPU chose rock. \n CPU Wins, try again.")
                1 -> putExtra(EXTRA_MESSAGE, "CPU chose paper. \n You win!")
                2 -> putExtra(EXTRA_MESSAGE, "CPU chose scissors. \n It's a draw, try again.")
            }
        }
        startActivity(intent)
    }


    /** Called when the user taps the Send button */
    fun showHelp(view: View) {
        val helpText = findViewById<TextView>(R.id.textView2)
        helpText.visibility = View.VISIBLE
    }

    /** Called when the user taps the Send button */
    fun toggleBackground(view: View) {
        val toggleBgButton = findViewById<Button>(R.id.toggle_bg)
        Toast.makeText(this@MainActivity, "Background Color Changed", Toast.LENGTH_SHORT).show()

        val random = Random()
        val nextInt = random.nextInt(0xffffff + 1)
        val colorCode = String.format("#%06x", nextInt)

        val helpText = findViewById<TextView>(R.id.textView2)
        helpText.setBackgroundColor(Color.parseColor(colorCode))
    }


}
