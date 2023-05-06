package com.xavifortes.finderfiesta

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class PartyListActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_party_list)

        // Get the token from shared preferences
        sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val token = sharedPref.getString("token", "")

        // Fetch the parties
        fetchParties(token)

        // Get a reference to the SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        // Set the listener for the SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            // Fetch the parties again
            fetchParties(token)
        }

        // Set the degradado as the background of the ActionBar
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.fondo_register))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setLogo(R.drawable.logo4)
        supportActionBar?.setDisplayUseLogoEnabled(true)
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

        if (id == R.id.action_add_party) {
            // startActivity(Intent(this@PartyListActivity, CreatePartyActivity::class.java))
            showCreatePartyDialog()
            return true
        }
        if (id == R.id.action_about_us) {
            startActivity(Intent(this@PartyListActivity, AboutUsActivity::class.java))
            return true
        }
        if (id == R.id.action_logout) {
            // Remove the token from shared preferences
            sharedPref.edit().remove("token").apply()

            // Launch the new activity
            startActivity(Intent(this@PartyListActivity, MainActivity::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun fetchParties(token: String?) {
        if (token.isNullOrEmpty()) {
            // If token is null or empty, launch the login activity
            startActivity(Intent(this@PartyListActivity, LoginActivity::class.java))
            finish()
            return
        }

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
                        recyclerView.adapter = PartyListAdapter(partyList, applicationContext)
                    }
                } else {
                    runOnUiThread {
                        when (response.code) {
                            401 -> {
                                Toast.makeText(applicationContext, "You need to login first", Toast.LENGTH_LONG).show()
                            }
                            429 -> {
                                Toast.makeText(applicationContext, "Too many requests in a minute", Toast.LENGTH_LONG).show()
                            }
                            else -> {
                                Toast.makeText(applicationContext, "Error: ${response.code}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
            runOnUiThread {
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun showCreatePartyDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_party, null)

        val editTextPartyName = dialogView.findViewById<EditText>(R.id.editTextPartyName)
        val editTextPartyDate = dialogView.findViewById<EditText>(R.id.editTextPartyDate)
        val editTextPartyTime = dialogView.findViewById<EditText>(R.id.editTextPartyTime)
        val editTextPartyLocation = dialogView.findViewById<EditText>(R.id.editTextPartyLocation)
        val editTextPartyDescription = dialogView.findViewById<EditText>(R.id.editTextPartyDescription)
        val buttonCreateParty = dialogView.findViewById<Button>(R.id.buttonCreateParty)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        buttonCreateParty.setOnClickListener {
            val partyName = editTextPartyName.text.toString()
            val partyDate = editTextPartyDate.text.toString()
            val partyTime = editTextPartyTime.text.toString()
            val partyLocation = editTextPartyLocation.text.toString()
            val partyDescription = editTextPartyDescription.text.toString()

            // Do something with the party details (e.g. send to server)

            createParty(partyName, partyDescription, partyLocation, partyDate, partyTime)

            dialog.dismiss()
            fetchParties(sharedPref.getString("token", ""))
        }

        dialog.show()
    }

    private fun createParty(name: String, description: String, location: String, date: String, time: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "https://api.android.xavifortes.com/party/create"
                val reqBody = JSONObject()
                reqBody.put("name", name)
                reqBody.put("description", description)
                reqBody.put("location", location)
                reqBody.put("date", date)
                reqBody.put("time", time)
                val requestBody = reqBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                val token = prefs.getString("token", null)
                if (token == null) {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "You need to login first", Toast.LENGTH_LONG).show()
                    }
                    return@launch
                }

                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer $token")
                    .post(requestBody)
                    .build()

                val client = OkHttpClient()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    // val responseData = response.body?.string()

                    // Extract the party ID from the JSON response
                    // val jsonObject = JSONObject(responseData)
                    // val partyId = jsonObject.getString("id")

                    // Launch the new activity with the party ID
                    // val intent = Intent(this@PartyListActivity, PartyDetailActivity::class.java)
                    // intent.putExtra("partyId", partyId)
                    // startActivity(intent)

                    // Finish the current activity
                    // finish()
                } else {
                    // Handle error
                    runOnUiThread {

                        when (response.code) {
                            401 -> {
                                Toast.makeText(applicationContext, "You need to login first", Toast.LENGTH_LONG).show()
                            }
                            429 -> {
                                Toast.makeText(applicationContext, "Too many requests in a minute, try again", Toast.LENGTH_LONG).show()
                            }
                            else -> {
                                Toast.makeText(applicationContext, "Error: ${response.code}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle exception
                runOnUiThread {
                    Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


}

