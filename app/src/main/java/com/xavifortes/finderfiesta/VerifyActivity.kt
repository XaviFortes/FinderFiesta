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

class VerifyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)

        if (intent.hasExtra("email")) {
            val email = intent.getStringExtra("email")
            val emailEditText = findViewById<EditText>(R.id.emailVerify)
            emailEditText.setText(email)
        }

        val verifyButton = findViewById<Button>(R.id.btn_verify)
        verifyButton.setOnClickListener {
            postVerifyData()
        }

        val loginButton = findViewById<Button>(R.id.btn_verify_2login)
        loginButton.setOnClickListener {
            startActivity(Intent(this@VerifyActivity, LoginActivity::class.java))
        }
    }

    private fun postVerifyData() {
        CoroutineScope(Dispatchers.IO).launch {
            val email = findViewById<EditText>(R.id.emailVerify).text.toString()
            val code = findViewById<EditText>(R.id.codeVerify).text.toString()
            try {
                val url = "https://api.android.xavifortes.com/auth/verify"
                val reqBody = JSONObject()
                reqBody.put("email", email)
                reqBody.put("verificationCode", code)
                val requestBody = reqBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val client = OkHttpClient()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {

                    // Launch the new activity
                    startActivity(Intent(this@VerifyActivity, PartyListActivity::class.java))

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