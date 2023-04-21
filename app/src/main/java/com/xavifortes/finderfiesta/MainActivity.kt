package com.xavifortes.finderfiesta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val bLogin = findViewById<Button>(R.id.iniciar_ses)
        bLogin.setOnClickListener {
            // Code to be executed when the button is clicked
            // For example, show a toast message
            // Toast.makeText(this, "Button clicked!", Toast.LENGTH_SHORT).show()
            postLoginData()

        }
    }

    private fun postLoginData() {
        Toast.makeText(this, "a", Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "https://api.android.xavifortes.com/auth/login"
                val requestBody = FormBody.Builder()
                    .add("email", findViewById<EditText>(R.id.email).text.toString())
                    .add("password", findViewById<EditText>(R.id.password).text.toString())
                    .build()

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val client = OkHttpClient()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    // Process the response data
                    println(responseData)
                } else {
                    // Handle error
                }
            } catch (e: Exception) {
                // Handle exception
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
        postData.put("password", password)

        // Write the data to the request body
        val outputStream = connection.outputStream
        outputStream.write(postData.toString().toByteArray())

        // Read the response from the API and display a toast message
        val inputStream = connection.inputStream
        val response = inputStream.bufferedReader().use { it.readText() }
        Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
    }
}