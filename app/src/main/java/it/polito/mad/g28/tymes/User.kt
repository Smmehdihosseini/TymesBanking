package it.polito.mad.g28.tymes

import android.os.Bundle
import io.getstream.chat.android.client.models.User

data class User(val uid: String,
                val fullname: String? = null,
                val biography: String? = null,
                val skills: String? = null,
                val location: String? = null,
                val email: String? = null,
                val userChatData: User? = null)

