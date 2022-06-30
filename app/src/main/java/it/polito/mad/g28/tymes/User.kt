package it.polito.mad.g28.tymes

import io.getstream.chat.android.client.models.User

data class User(val uid: String,
                val fullname: String? = "",
                val biography: String? = "",
                val skills: String? = "",
                val location: String? = "",
                val email: String? = "",
                val userChatData: User? = null,
                val providerRating: String? = "0",
                val workerRating: String? = "0",
                val totalProviderRating: String? = "0",
                val totalWorkerRating: String? = "0",
                val nbRatingProvider: String? = "0",
                val nbRatingWorker: String? = "0",
                val credits: Int? = 0)

