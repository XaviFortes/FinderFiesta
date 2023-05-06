package com.xavifortes.finderfiesta

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AboutUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "About Us"

        // Listener buttons
        val xfLinkedinButton = findViewById<Button>(R.id.xflin)
        xfLinkedinButton.setOnClickListener {
            startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://www.linkedin.com/in/xavi-fortes/")))
        }
        val xfWebsiteButton = findViewById<Button>(R.id.xfweb)
        xfWebsiteButton.setOnClickListener {
            startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://xavifortes.com/")))
        }
        val chLinkedinButton = findViewById<Button>(R.id.chlin)
        chLinkedinButton.setOnClickListener {
            startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://www.linkedin.com/in/carlos-hinojosa-vaca-b57b74240/")))
        }
        val caLinkedinButton = findViewById<Button>(R.id.calin)
        caLinkedinButton.setOnClickListener {
            startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://www.linkedin.com/in/carlos-ant%C3%B3n-ibeas-92a2a9197/")))
        }
        // Set the degradado as the background of the ActionBar
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.fondo_register))
        supportActionBar?.setDisplayUseLogoEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    /*
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_about_us, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_go_back) {
            finish()
        }
        return true
    }
    */


}