package it.polito.mad.g28.tymes

data class User(val uid: String,
                val fullname: String? = null,
                val nickname: String? = null,
                val username: String? = null,
                val biography: String? = null,
                val skills: String? = null,
                val location: String? = null,
                val email: String? = null,
                val webpage: String? = null)

