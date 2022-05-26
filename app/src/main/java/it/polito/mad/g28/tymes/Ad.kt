package it.polito.mad.g28.tymes

data class Ad(
    val adID: String,
    val authorID: String,
    val skill: String,
    val availability: String,
    val description: String?,
    val location: String,
    val price: String,
    val date: String)