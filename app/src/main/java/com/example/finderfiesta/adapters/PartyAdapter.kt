package com.example.finderfiesta.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finderfiesta.R
import com.example.finderfiesta.models.Party

class PartyAdapter(private val parties: List<Party>) : RecyclerView.Adapter<PartyAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val partyName: TextView = itemView.findViewById(R.id.party_name)
        val partyLocation: TextView = itemView.findViewById(R.id.party_location)
        val partyDate: TextView = itemView.findViewById(R.id.party_date)
        val partyAttendees: TextView = itemView.findViewById(R.id.party_attendees)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.party_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val party = parties[position]
        holder.partyName.text = party.name
        holder.partyLocation.text = party.location
        holder.partyDate.text = party.date.toString()
        holder.partyAttendees.text = party.attendees.joinToString { it.name }
    }

    override fun getItemCount() = parties.size
}
