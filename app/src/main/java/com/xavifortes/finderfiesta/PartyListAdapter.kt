package com.xavifortes.finderfiesta

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class PartyListAdapter(private val partyList: JSONArray, context: Context) :
    RecyclerView.Adapter<PartyListAdapter.ViewHolder>() {

    private var sharedPref: SharedPreferences
    private var email: String

    init {
        sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        email = sharedPref.getString("email", "").toString()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val partyName: TextView = view.findViewById(R.id.party_name)
        val partyDate: TextView = view.findViewById(R.id.party_date)
        val partyTime: TextView = view.findViewById(R.id.party_time)
        val partyLocation: TextView = view.findViewById(R.id.party_location)
        val partyDesc: TextView = view.findViewById(R.id.party_description)
        val partyJoin: Button = view.findViewById(R.id.btn_join_party)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.party_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val party = partyList.getJSONObject(position)
        holder.partyName.text = party.getString("name")
        holder.partyDate.text = party.getString("date")
        holder.partyTime.text = party.getString("time")
        holder.partyLocation.text = party.getString("location")
        holder.partyDesc.text = party.getString("description")

        // If the user is already in the party, disable the join button
        val attendees = party.getJSONArray("attendees")
        for (i in 0 until attendees.length()) {
            if (attendees.getString(i) == email) {
                holder.partyJoin.isEnabled = false
                holder.partyJoin.text = "Ya estás dentro"
                break
            } else {
                holder.partyJoin.isEnabled = true
                holder.partyJoin.text = "Unirse"
            }
        }

        // Join the party
        holder.partyJoin.setOnClickListener {
            println("Joining party " + party.getString("id"))
            attendees.put(email)
            party.put("attendees", attendees)
            joinParty(party)

            notifyDataSetChanged()
        }

        holder.itemView.setOnClickListener {
            showAttendeesModal(holder.itemView.context, attendees)
        }
    }

    private fun joinParty(party: JSONObject) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = sharedPref.getString("token", null) ?: return@launch
                val url = "https://api.android.xavifortes.com/party/join"
                val reqBody = JSONObject()
                    .put("partyId", party.getString("id"))

                val requestBody = reqBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer $token")
                    .put(requestBody)
                    .build()

                val client = OkHttpClient()
                val response = client.newCall(request).execute()
                println("Response: $response")
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                } else {
                    println("Error: ${response.code}")
                }
            } catch (e: Exception) {
                println("Error: $e")
            }
        }
    }
    override fun getItemCount() = partyList.length()

    // Show the attendees in a modal
    private fun showAttendeesModal(context: Context, attendees: JSONArray) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Usuarios asistentes")

        val attendeesList = mutableListOf<String>()
        for (i in 0 until attendees.length()) {
            attendeesList.add(attendees.getString(i))
        }

        builder.setItems(attendeesList.toTypedArray(), null)

        builder.setPositiveButton("Perfe") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}
