package com.xavifortes.finderfiesta

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class SignupActivity  : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val signinButton = findViewById<Button>(R.id.btn_2login)
        signinButton.setOnClickListener {
            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
        }

        val signupButton = findViewById<Button>(R.id.btn_signup)
        signupButton.setOnClickListener {
            postSignupData()
        }
    }


    private fun postSignupData() {
        CoroutineScope(Dispatchers.IO).launch {
            val email = findViewById<EditText>(R.id.email).text.toString()
            val password = findViewById<EditText>(R.id.password).text.toString()
            val password2 = findViewById<EditText>(R.id.repeatpassword).text.toString()
            if (password != password2) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Passwords don't match", Toast.LENGTH_LONG).show()
                }
                return@launch
            }
            try {
                val url = "https://api.android.xavifortes.com/auth/signup"
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

                    // Launch the new activity
                    startActivity(Intent(this@SignupActivity, PartyListActivity::class.java))

                    // Finish the current activity
                    finish()
                } else {
                    // Handle error
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Error: ${response.body?.string()}", Toast.LENGTH_LONG).show()
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