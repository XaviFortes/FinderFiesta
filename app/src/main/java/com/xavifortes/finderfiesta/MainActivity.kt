package com.xavifortes.finderfiesta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val bLogin = findViewById<Button>(R.id.iniciar_ses)
        bLogin.setOnClickListener {
            // Code to be executed when the button is clicked
            // For example, show a toast message
            Toast.makeText(this, "Button clicked!", Toast.LENGTH_SHORT).show()
            
        }
    }
}