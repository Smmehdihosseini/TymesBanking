package it.polito.mad.g28.tymes

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SettingsFragment : Fragment() {

    private var oneTapClient: SignInClient? = null
    private var signInRequest: BeginSignInRequest? = null
    private val REQ_ONE_TAP: Int = 1338
    private val profileVM: ProfileVM by viewModels()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        val user = auth.currentUser
        val btnDisconnect = activity?.findViewById<Button>(R.id.btn_disconnect)
        val btnConnect = activity?.findViewById<Button>(R.id.btn_google_auth)


        if (user != null) {
            btnDisconnect?.visibility = View.VISIBLE
            // If user is authenticated, the button should enable him to disconnect
            btnDisconnect?.setOnClickListener {
                Firebase.auth.signOut()
                if (Firebase.auth.currentUser == null) {
                    Toast.makeText(
                        context,
                        "Successfully disconnected from your Google Account",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            // Do not show the disconnect button if user is not connected
            btnDisconnect!!.visibility = View.GONE
            btnConnect!!.visibility = View.VISIBLE

            btnConnect.setOnClickListener {
                oneTapClient = Identity.getSignInClient(requireActivity())
                signInRequest = BeginSignInRequest.builder()
                    .setPasswordRequestOptions(
                        BeginSignInRequest.PasswordRequestOptions.builder()
                            .setSupported(true)
                            .build()
                    )
                    .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                            .setSupported(true)
                            // Your server's client ID, not your Android client ID.
                            .setServerClientId("912277110592-3oo1mgepmkfjmr8qde7cjhe1coo8fvrf.apps.googleusercontent.com")
                            // Only show accounts previously used to sign in.
                            .setFilterByAuthorizedAccounts(false)
                            .build()
                    )
                    // Automatically sign in when exactly one credential is retrieved.
                    .setAutoSelectEnabled(true)
                    .build()

                oneTapClient!!.beginSignIn(signInRequest!!)
                    .addOnSuccessListener(requireActivity()) { result ->
                        try {
                            Log.d("lifecycle", "success listener")
                            startIntentSenderForResult(
                                result.pendingIntent.intentSender, REQ_ONE_TAP,
                                null, 0, 0, 0, null
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            Log.e("lifecycle", "Couldn't start One Tap UI: ${e.localizedMessage}")
                        }
                    }
                    .addOnFailureListener(requireActivity()) { e ->
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.d("Sign In", e.localizedMessage)
                    }
            }
        }
    }

    private fun createUser(currentUser: FirebaseUser?) {
        // When user authenticates via Google
        if (currentUser != null) {
            Log.d("lifecycle", "Creating user in DB with uid : ${currentUser.uid}")
            val database = Firebase.firestore
            val uid = currentUser.uid
            val name = currentUser.displayName
            val email = currentUser.email
            val user = User(uid, name, "", "", "", email)
            // When the user authenticates, update DB
            profileVM.updateProfile(name.toString(), "", "", "", email.toString())
            database.collection("users").document(uid).set(user)
                .addOnSuccessListener {
                    Log.d(
                        "lifecycle",
                        "successfully created user with uid: $uid"
                    )
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient!!.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    val password = credential.password
                    when {
                        idToken != null -> {
                            val firebaseCredential =
                                GoogleAuthProvider.getCredential(idToken, null)
                            auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(requireActivity()) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("lifecycle", "signInWithCredential:success")
                                        val user = auth.currentUser
                                        createUser(user)
                                        Toast.makeText(
                                            context,
                                            "Successfully Connected from your Google Account",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.d(
                                            "lifecycle",
                                            "signInWithCredential:failure",
                                            task.exception
                                        )
//                                        createUser(null)
                                    }
                                }

                            Log.d("lifecycle", "Got ID token.")
                        }
                        password != null -> {
                            // Got a saved username and password. Use them to authenticate
                            // with your backend.
                            Log.d("Auth", "Got password.")
                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d("Auth", "No ID token or password!")
                        }
                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d("lifecycle", "One-tap dialog was closed.")
                            // Don't re-prompt the user.
                            var showOneTapUI = false
                        }
                        CommonStatusCodes.NETWORK_ERROR -> {
                            Log.d("lifecycle", "One-tap encountered a network error.")
                            // Try again or just ignore.
                        }
                        else -> {
                            Log.d(
                                "lifecycle", "Couldn't get credential from result." +
                                        " (${e.localizedMessage})"
                            )
                        }
                    }

                }
            }
        }
    }
}