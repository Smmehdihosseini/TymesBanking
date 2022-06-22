package it.polito.mad.g28.tymes

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
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
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.offline.model.message.attachments.UploadAttachmentsNetworkType
import io.getstream.chat.android.offline.plugin.configuration.Config
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private var oneTapClient: SignInClient? = null
    private var signInRequest: BeginSignInRequest? = null
    private val REQ_ONE_TAP: Int = 1337
    private lateinit var auth: FirebaseAuth
    private val profileVM : ProfileVM by viewModels()
    private lateinit var client: ChatClient
    private lateinit var user: io.getstream.chat.android.client.models.User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupChatClient()
        // Set Google Auth if user is not already connected
        auth = Firebase.auth
        if (auth.currentUser == null ) {
            Log.d("lifecycle", "auth")
            auth()
        }

        drawerLayout = findViewById(R.id.drawerlayout)
        val navView: NavigationView = findViewById(R.id.navigationView)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

                    val sharedPref = getPreferences(Context.MODE_PRIVATE)
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

            }
            true
        }


    }

    private fun setupChatClient() {
        val offlinePluginFactory = StreamOfflinePluginFactory(
            config = Config(
                // Enables the background sync which is performed to sync user actions done without the Internet connection.
                backgroundSyncEnabled = true,
                // Enables the ability to receive information about user activity such as last active date and if they are online right now.
                userPresence = true,
                // Enables using the database as an internal caching mechanism.
                persistenceEnabled = true,
                // An enumeration of various network types used as a constraint inside upload attachments worker.
                uploadAttachmentsNetworkType = UploadAttachmentsNetworkType.NOT_ROAMING,
            ),
            appContext = this,
        )

        val client =
            ChatClient.Builder(getString(R.string.api_key), this)
                .withPlugin(offlinePluginFactory)
                .logLevel(ChatLogLevel.ALL)
                .build()
        Log.d("lifecycle","streamchat id: ${client.getCurrentUser()?.id.toString()}")
    }

    private fun auth() {
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId("912277110592-3oo1mgepmkfjmr8qde7cjhe1coo8fvrf.apps.googleusercontent.com")
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()


            oneTapClient!!.beginSignIn(signInRequest!!)
                .addOnSuccessListener(this) { result ->
                    try {
                        Log.d("lifecycle", "success listener")
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQ_ONE_TAP,
                            null, 0, 0, 0, null)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e("lifecycle", "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener(this) { e ->
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d("Sign In", e.localizedMessage)
                }
    }

    override fun onStart() {
        super.onStart()

        Log.d("lifecycle", "Starting the Application")

        // Restore user data from DB and update viewModel
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            val database = Firebase.firestore
            val uid = currentUser.uid
            val name = currentUser.displayName
            val email = currentUser.email

            val docRef = database.collection("users").document(uid)
            docRef.get()
                .addOnSuccessListener {  document ->
                    val map = document.data
                    val biography = map?.get("biography").toString()
                    val skills = map?.get("skills").toString()
                    val location = map?.get("location").toString()

                    Log.d("lifecycle", "Updating user profile")
                    profileVM.updateProfile(name.toString(), biography, skills, location, email.toString())
                }
        }
    }

    private fun createUser(currentUser: FirebaseUser?){
        // When user authenticates via Google
        if (currentUser != null){
            Log.d("lifecycle", "Creating user in DB")
            val database = Firebase.firestore
            val uid = currentUser.uid
            val name = currentUser.displayName
            val email = currentUser.email
            val user = User(uid,name, "", "", "", email)
            // When the user authenticates, update DB
            profileVM.updateProfile(name.toString(), "", "", "", email.toString())
            database.collection("users").document(uid).set(user)
                .addOnSuccessListener {Log.d("lifecycle", "successfully created user with uid: $uid")}
        }
    }


    fun changeFrag(fragment: Fragment, title: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction
            .replace(R.id.fragmentContainerView, fragment)
            .addToBackStack(null)
            .commit()
        drawerLayout.closeDrawers()
        setTitle(title)


    }

        override fun onBackPressed() {
            val count = supportFragmentManager.backStackEntryCount
            if (count == 0) {
                super.onBackPressed()
                //additional code
            } else {
                // Go to previous fragment
                supportFragmentManager.popBackStack()
            }
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            if (toggle.onOptionsItemSelected(item)) {
                return true
            }
            return super.onOptionsItemSelected(item)
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
                                    .addOnCompleteListener(this) { task ->
                                        if (task.isSuccessful) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d("lifecycle", "signInWithCredential:success")
                                            val user = auth.currentUser
                                            createUser(user)

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