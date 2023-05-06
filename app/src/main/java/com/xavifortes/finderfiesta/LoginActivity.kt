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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.btn_login)
        loginButton.setOnClickListener {
            postLoginData()
        }

        val verifyButton = findViewById<Button>(R.id.btn_login_2verify)
        verifyButton.setOnClickListener {
            startActivity(Intent(this@LoginActivity, VerifyActivity::class.java))
        }

        val signupButton = findViewById<Button>(R.id.btn_verify_2login)
        signupButton.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
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
                    prefs.edit().putString("email", findViewById<EditText>(R.id.email).text.toString()).apply()

                    // Launch the new activity
                    startActivity(Intent(this@LoginActivity, PartyListActivity::class.java))

                    // Finish the current activity
                    finish()
                } else {
                    // Handle error
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Invalid email or password", Toast.LENGTH_LONG).show()
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