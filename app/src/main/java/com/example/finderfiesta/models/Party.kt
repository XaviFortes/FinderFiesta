package com.example.finderfiesta.models

import java.util.*

data class Party(
    val id: String,
    val name: String,
    val createdBy: String,
    val date: Date,
    val location: String,
    val maxAttendees: Int,
    val attendees: List<Attendee>
)
