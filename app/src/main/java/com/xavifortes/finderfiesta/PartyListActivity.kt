package com.xavifortes.finderfiesta

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

class PartyListActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_party_list)


        
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_party_list, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_logout) {
            // Remove the token from shared preferences
            val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            prefs.edit().remove("token").apply()

            // Launch the new activity
            startActivity(Intent(this@PartyListActivity, MainActivity::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun fetchParties(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "https://api.android.xavifortes.com/party/"
                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer $token")
                    .build()

                val client = OkHttpClient()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val partyList = JSONArray(response.body?.string())
                    runOnUiThread {
                        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.party_list)
                        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
                        recyclerView.adapter = PartyListAdapter(partyList)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Error: ${response.code}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
