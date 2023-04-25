package com.xavifortes.finderfiesta

data class Party(
    val attendees: List<String>,
    val date: String,
    val location: String,
    val creator: String,
    val updatedAt: Long,
    val createdAt: Long,
    val time: String,
    val description: String,
    val id: String,
    val name: String
)

