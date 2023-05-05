package com.xavifortes.finderfiesta

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

class PartyListAdapter(private val partyList: JSONArray) :
    RecyclerView.Adapter<PartyListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val partyName: TextView = view.findViewById(R.id.party_name)
        val partyDate: TextView = view.findViewById(R.id.party_date)
        val partyTime: TextView = view.findViewById(R.id.party_time)
        val partyLocation: TextView = view.findViewById(R.id.party_location)
        val partyDesc: TextView = view.findViewById(R.id.party_description)
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

        holder.itemView.setOnClickListener {
            showAttendeesModal(holder.itemView.context, party.getJSONArray("attendees"))
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
