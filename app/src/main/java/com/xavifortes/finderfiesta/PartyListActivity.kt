package com.xavifortes.finderfiesta

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class PartyListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPartyListBinding
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_party_list)
        binding = ActivityPartyListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Initialize and populate the party list here
        sharedPref = getSharedPreferences("finder-fiesta", Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", null)

        if (token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            fetchParties(token)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_party_list, menu)

        return true
    }

    /*
    private fun logout() {
        val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove(getString(R.string.auth_token_key))
            apply()
        }
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    */

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
                        displayParties(partyList)
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

    private fun displayParties(partyList: JSONArray) {
        val partyAdapter = PartyAdapter(partyList)
        binding.partyListRecyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = partyAdapter
        }
    }

    private inner class PartyAdapter(val partyList: JSONArray) :
        RecyclerView.Adapter<PartyAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val partyNameTextView: TextView = itemView.findViewById(R.id.party_name)
            val partyDescriptionTextView: TextView = itemView.findViewById(R.id.party_description)
            val partyLocationTextView: TextView = itemView.findViewById(R.id.party_location)
            val partyDateTextView: TextView = itemView.findViewById(R.id.party_date)
            val partyTimeTextView: TextView = itemView.findViewById(R.id.party_time)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_party, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val partyObj = partyList.getJSONObject(position)
            holder.partyNameTextView.text = partyObj.getString("name")
            holder.partyDescriptionTextView.text = partyObj.getString("description")
            holder.partyLocationTextView.text = partyObj.getString("location")
            val date = LocalDate.parse(partyObj.getString("date"), DateTimeFormatter.ISO_DATE)
            val time = LocalTime.parse(partyObj.getString("time"), DateTimeFormatter.ISO_TIME)
            val dateTime = LocalDateTime.of(date, time)
            val formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"))
            holder.partyDateTextView.text = formattedDateTime
        }

        override fun getItemCount(): Int {
            return partyList.length()
        }
    }
}
