package com.example.finderfiesta

import com.example.finderfiesta.models.Attendee
import com.example.finderfiesta.models.Party
import java.util.*

class ApiService {
    private fun getPartiesFromApi(): List<Party> {
        val url = "http://127.0.0.1:3000"
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ApiService::class.java)

        val response = api.getParties().execute()
        val partyJsonArray = response.body() ?: return emptyList()

        return partyJsonArray.map { partyJson ->
            Party(
                id = partyJson["id"].asString,
                name = partyJson["name"].asString,
                createdBy = partyJson["created_by"].asString,
                date = Date(partyJson["date"].asLong),
                location = partyJson["location"].asString,
                maxAttendees = partyJson["max_attendees"].asInt,
                attendees = partyJson["attendees"].asJsonArray.map { attendeeJson ->
                    Attendee(
                        name = attendeeJson["name"].asString,
                        email = attendeeJson["email"].asString
                    )
                }
            )
        }
    }

}