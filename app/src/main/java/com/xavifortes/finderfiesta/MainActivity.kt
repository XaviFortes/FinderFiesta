package com.xavifortes.finderfiesta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Check if the user is authenticated on startup
        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val token = prefs.getString("token", null)
        if (token != null) {
            checkAuthentication(token)
        } else {
            // Launch the login activity if no token is saved
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun checkAuthentication(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "https://api.android.xavifortes.com/auth/isAuthenticated"
                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer $token")
                    .build()

                val client = OkHttpClient()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    // User is authenticated, continue to party list activity
                    println("User is authenticated")
                    println(response.code)
                    println(response.body?.string())
                    startActivity(Intent(this@MainActivity, PartyListActivity::class.java))
                    finish()
                } else {
                    // User is not authenticated, launch login activity
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
            } catch (e: Exception) {
                // Handle exception
                runOnUiThread {
                    Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun postLoginData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "https://api.android.xavifortes.com/auth/login"
                val reqBody = JSONObject()
                reqBody.put("email", findViewById<EditText>(R.id.email).text.toString())
                reqBody.put("password", findViewById<EditText>(R.id.password).text.toString())
                val requestBody = reqBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val client = OkHttpClient()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseData = response.body?.string()

                    // Extract the token from the JSON response
                    val jsonObject = JSONObject(responseData)
                    val token = jsonObject.getString("token")

                    // Store the token in shared preferences
                    val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                    prefs.edit().putString("token", token).apply()

                    // Process the response data
                    println(responseData)

                    // Launch the new activity
                    //startActivity(Intent(this@LoginActivity, PartyListActivity::class.java))

                    // Finish the current activity
                    finish()
                } else {
                    // Handle error
                    println("Error " + response.code)
                    println(response.body?.string())
                }
            } catch (e: Exception) {
                // Handle exception
                println("Error" + e.message)
            }
        }
    }


    private fun postLoginData2() {
        // Get the text from the email and password fields
        val email = findViewById<EditText>(R.id.email).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString()

        // Define the API endpoint URL and create a connection
        val url = URL("https://api.android.xavifortes.com/auth/login")
        val connection = url.openConnection() as HttpURLConnection

        // Set the request method and headers
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")

        // Create the JSON data to be sent in the request body
        val postData = JSONObject()
        postData.put("email", email)
        println("Email: $email")
        postData.put("password", password)
        println("Password: $password")

        // Write the data to the request body
        val outputStream = connection.outputStream
        outputStream.write(postData.toString().toByteArray())

        // Read the response from the API and display a toast message
        val inputStream = connection.inputStream
        val response = inputStream.bufferedReader().use { it.readText() }
        Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
    }
}