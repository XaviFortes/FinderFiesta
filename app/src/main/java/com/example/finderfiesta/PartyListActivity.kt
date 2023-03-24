package com.example.finderfiesta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finderfiesta.adapters.PartyAdapter
import com.example.finderfiesta.models.Party

class PartyListActivity : AppCompatActivity() {

    private lateinit var partyAdapter: PartyAdapter
    private lateinit var partyList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_party_list)

        partyList = findViewById(R.id.party_list)
        partyList.layoutManager = LinearLayoutManager(this)

        val parties = getPartiesFromApi()
        partyAdapter = PartyAdapter(parties)
        partyList.adapter = partyAdapter
    }

    private fun getPartiesFromApi(): List<Party> {
        val url = "http://127.0.0.1:3000"
        val parties = mutableListOf<Party>()

        // Realiza una solicitud HTTP a la API usando una biblioteca de cliente HTTP como Retrofit o Volley.
        // Procesa los datos de respuesta y crea objetos de fiesta y sus detalles.

        return parties
    }
}
