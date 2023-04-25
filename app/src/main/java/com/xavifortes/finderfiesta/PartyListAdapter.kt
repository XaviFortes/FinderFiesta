package com.xavifortes.finderfiesta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray

class PartyListAdapter(private val partyList: JSONArray) :
    RecyclerView.Adapter<PartyListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val partyName: TextView = view.findViewById(R.id.party_name)
        val partyDate: TextView = view.findViewById(R.id.party_date)
        val partyLocation: TextView = view.findViewById(R.id.party_location)
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
        holder.partyLocation.text = party.getString("location")
    }

    override fun getItemCount() = partyList.length()
}