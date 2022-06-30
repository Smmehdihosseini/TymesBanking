package it.polito.mad.g28.tymes

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SettingsFragment : Fragment() {

    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
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
                        "Successfully Disconnected From Your Google Account!",
                        Toast.LENGTH_SHORT
                    ).show()
                    setupNavMenu()
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
            // When the user authenticates, update DB
            profileVM.updateProfile(name.toString(), "", "", "", email.toString())
            database.collection("users").document(uid)
                .update(mapOf(
                    "fullname" to name,
                    "email" to email,
                ))
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


                                        setupNavMenu()

                                        val user = auth.currentUser
                                        createUser(user)
                                        Toast.makeText(
                                            context,
                                            "Successfully Connected With Your Google Account",
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

    private fun setupNavMenu(){
        drawerLayout = requireActivity().findViewById(R.id.drawerlayout)
        val navView: NavigationView = requireActivity().findViewById(R.id.navigationView)
        toggle = ActionBarDrawerToggle(activity, drawerLayout, R.string.nav_open, R.string.nav_close)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val currentUser = Firebase.auth.currentUser
        if (currentUser == null){
            navView.menu.findItem(R.id.my_profile_icon).setVisible(false)
            navView.menu.findItem(R.id.my_chat_icon).setVisible(false)
            navView.menu.findItem(R.id.favorites_icon).setVisible(false)
            navView.menu.findItem(R.id.scheduled_icon).setVisible(false)
            navView.menu.findItem(R.id.my_home).setVisible(false)
        } else{
            navView.menu.findItem(R.id.my_profile_icon).setVisible(true)
            navView.menu.findItem(R.id.my_chat_icon).setVisible(true)
            navView.menu.findItem(R.id.favorites_icon).setVisible(true)
            navView.menu.findItem(R.id.scheduled_icon).setVisible(true)
            navView.menu.findItem(R.id.my_home).setVisible(true)
        }

//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {

            it.isChecked = true
            val currentUser = Firebase.auth.currentUser


            when(it.itemId){

                R.id.my_profile_icon -> {

                    MainScope().launch{
                        delay(100)
                        changeFrag(ShowProfileActivity(), it.title.toString())
                    }
                    val database = Firebase.firestore

                    val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                    with(sharedPref!!.edit()){
                        putString("Author ID", currentUser?.uid)
                        apply()
                    }

                    database.collection("users").document(currentUser?.uid.toString()).get()
                        .addOnSuccessListener { document ->

                            val map = document?.data
                            Log.d("lifecycle", "navigation drawer fullname is ${map?.get("fullname").toString()}")
                            profileVM.updateProfile(
                                map?.get("fullname").toString(),
                                map?.get("biography").toString(),
                                map?.get("skills").toString(),
                                map?.get("location").toString(),
                                map?.get("email").toString(),)
                        }
                        .addOnFailureListener{
                            Log.d("lifecycle", "Failed DB get")
                        }



                }
                R.id.my_chat_icon -> {
                    changeFrag(ChannelFragment(), it.title.toString())
                }

                R.id.favorites_icon -> {
                    changeFrag(FavoritesFragment(), it.title.toString())
                }

                R.id.scheduled_icon -> {
                    changeFrag(ScheduledFragment(), it.title.toString())
                }

                R.id.ic_skill -> changeFrag(SkillListFragment(), it.title.toString())

                R.id.tymes_settings_icon -> changeFrag(SettingsFragment(), it.title.toString())

                R.id.my_home -> changeFrag(Home(), it.title.toString())

                R.id.tymes_about_icon -> changeFrag(aboutFragment(), it.title.toString())


            }
            true
        }
    }

    fun changeFrag(fragment: Fragment, title: String) {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction
            .replace(R.id.fragmentContainerView, fragment)
            .addToBackStack(null)
            .commit()
        drawerLayout.closeDrawers()
        activity?.setTitle(title)
    }
}