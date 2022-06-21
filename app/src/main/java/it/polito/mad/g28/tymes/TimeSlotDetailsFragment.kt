package it.polito.mad.g28.tymes

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class TimeSlotDetailsFragment : Fragment() {

    private val viewModel : AdVM by activityViewModels()
    private val profileVM: ProfileVM by activityViewModels()
    private val client = ChatClient.instance()
    private val currentUser = Firebase.auth.currentUser

    private lateinit var user: User


    private val database = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_time_slot_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val adID = sharedPref?.getString("Ad ID", null)
        val authorID = sharedPref?.getString("Author ID", null)

        val tvAuthor = activity?.findViewById<TextView>(R.id.ad_author)
        val tvSkill = activity?.findViewById<TextView>(R.id.ad_skill)
        val tvAvailability = activity?.findViewById<TextView>(R.id.ad_availability)
        val tvDescription = activity?.findViewById<TextView>(R.id.ad_description)
        val tvLocation = activity?.findViewById<TextView>(R.id.ad_location)
        val tvPrice = activity?.findViewById<TextView>(R.id.ad_price)
        val tvDate = activity?.findViewById<TextView>(R.id.ad_date)

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())




        viewModel.adInfo.observe(viewLifecycleOwner){
            tvAuthor?.text = it["Author"]
            tvSkill?.text = it["Skill"]
            tvAvailability?.text = it["Availability"]
            tvDescription?.text = it["Description"]
            tvLocation?.text = it["Location"]
            tvPrice?.text = it["Price"]
            tvDate?.text = it["Date"]

            Log.d("lifecycle", "onViewCreated: tvDate ${tvDate?.text.toString()} , currentDate: $currentDate")
            val cmp = currentDate.compareTo(tvDate?.text.toString())
            if(cmp > 0) {
                Log.d("lifecycle", "onViewCreated: currentDate is after tvDate, ad status is completed")
                database.collection("ads").document(adID!!).update("availability", "Completed")
//            .addOnSuccessListener {Log.d("lifecycle", "Successfully edited ad with id: $adID")}
//            .addOnFailureListener {Log.d("lifecycle", "Did not edit the ad: $adID properly")}
                database.collection("users").document(authorID!!).collection("userAds").document(adID)
                    .update("status","Completed")
                    .addOnSuccessListener {
                        Log.d("lifecycle", "Successfully set status to completed for adID: $adID")
                        tvAvailability?.text = "Completed"}
            }
        }



        tvAuthor?.setOnClickListener{

            viewLifecycleOwner.lifecycleScope.launch{
                delay(500)
//                (activity as MainActivity).changeFrag(ShowProfileActivity(), "Profile")
                val fragmentTransaction = parentFragmentManager.beginTransaction()
                fragmentTransaction
                    .replace(R.id.fragmentContainerView, ShowProfileActivity())
                    .addToBackStack(null)
                    .commit()
            }
            Log.d("lifecycle", "passing author id: $authorID")
            database.collection("users").document(authorID.toString()).get()
                .addOnSuccessListener { document ->

                    val map = document?.data
                    Log.d("lifecycle", "fullname is ${map?.get("fullname").toString()}")
                    profileVM.updateProfile(
                        map?.get("fullname").toString(),
                        map?.get("biography").toString(),
                        map?.get("skills").toString(),
                        map?.get("location").toString(),
                        map?.get("email").toString(),
                    )
                }
        }

        if (tvAvailability?.text.toString() == "Completed") {

            if (currentUser?.uid == authorID) {
                val ratingBtn = activity?.findViewById<Button>(R.id.btn_rating)
                ratingBtn?.visibility = View.VISIBLE
                ratingBtn?.setOnClickListener {
                    database.collection("users").document(authorID!!).collection("userAds")
                        .document(adID!!)
                        .get()
                        .addOnSuccessListener {

                            Log.d("lifecycle", "rating -> askerID: ${it.get("askerID")}")
                            with(sharedPref!!.edit()) {
                                putString("askerID", it.get("askerID").toString())
                                apply()
                            }
                            val fragmentTransaction = parentFragmentManager.beginTransaction()
                            fragmentTransaction
                                .replace(R.id.fragmentContainerView, RatingFragment())
                                .addToBackStack(null)
                                .commit()
                        }
                }
            }

            if (authorID != null && adID != null) {
                database.collection("users").document(authorID)
                    .collection("userAds").document(adID)
                    .addSnapshotListener { value, e ->
                        Log.d("lifecycle", "rating -> askerID: ${value?.get("askerID")}")
                        if (value!!["askerID"] == currentUser?.uid) {
                            val ratingBtn = activity?.findViewById<Button>(R.id.btn_rating)
                            ratingBtn?.visibility = View.VISIBLE
                            ratingBtn?.setOnClickListener {

                                with(sharedPref!!.edit()) {
                                    putString("Author ID", authorID)
                                    apply()
                                }
                                val fragmentTransaction = parentFragmentManager.beginTransaction()
                                fragmentTransaction
                                    .replace(R.id.fragmentContainerView, RatingFragment())
                                    .addToBackStack(null)
                                    .commit()
                            }
                        }
                    }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val user = Firebase.auth.currentUser
        val availability = activity?.findViewById<TextView>(R.id.ad_availability)?.text.toString()
        Log.d("lifecycle", "onCreateOptionsMenu: availability: $availability")
        if (sharedPref?.getString("Author ID", "notanid") == user?.uid){
            inflater.inflate(R.menu.menubar, menu)
        } else if (sharedPref?.getString("Author ID", "notanid") != "notanid" && availability == "Available"){
            inflater.inflate(R.menu.worker_menu, menu)
        }else if (sharedPref?.getString("Author ID", "notanid") != "notanid" && availability != "Available"){
            inflater.inflate(R.menu.worker_unavailable_menu, menu)
        }
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        var adID = sharedPref?.getString("Ad ID", null)

        if (adID == null){
            // Generate random ID from the ad if it is new (no shared pref)
            Log.d("lifecycle", "Generating a new ad ID")
            adID = UUID.randomUUID().toString()
        }
        with(sharedPref!!.edit()){
            putString("Ad ID", adID)
            apply()
        }


        return when(item.itemId) {
            R.id.edit_pencil_button -> {
                val fragmentTransaction = parentFragmentManager.beginTransaction()
                fragmentTransaction
                    .replace(R.id.fragmentContainerView, TimeSlotEditFragment())
                    .addToBackStack(null)
                    .commit()
                true
            }

            R.id.ic_fav -> {
                // User clicks on Fav icon in menu

                // Check if the document is in the DB
                val docRef =
                    database.collection("users").document(Firebase.auth.currentUser?.uid.toString())
                        .collection("favorites").document(adID)
                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document.data != null) {
                            // If the document is in the DB, remove it
                            database.collection("users")
                                .document(Firebase.auth.currentUser?.uid.toString())
                                .collection("favorites").document(adID).delete()
                                .addOnSuccessListener { Toast.makeText(context, "Successfully removed ad from favorites", Toast.LENGTH_SHORT).show()}
                        } else {
                            // If the document is in the DB, remove it
                            val map = hashMapOf("adID" to adID)
                            database.collection("users")
                                .document(Firebase.auth.currentUser?.uid.toString())
                                .collection("favorites").document(adID).set(map)
                                .addOnSuccessListener { Toast.makeText(context, "Successfully added ad to favorites", Toast.LENGTH_SHORT).show()}
                        }
                    }
                true
            }

            R.id.ic_chats -> {

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Send Request")
                    .setMessage("Are you sure you want to send a request for this Ad?")
                    .setNegativeButton("CANCEL") { dialog, which ->
                        // Respond to negative button press
                    }
                    .setPositiveButton("SEND") { dialog, which ->
                        // Respond to positive button press
                        val progressDialog = ProgressDialog(requireContext())
                        progressDialog.setMessage("Setting up the Chat")
                        progressDialog.setCancelable(true)
                        progressDialog.show()
                        val authorID = sharedPref?.getString("Author ID", "")
                        setupChannel(authorID!!, progressDialog)
                    }
                    .show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        val tvAuthor = activity?.findViewById<TextView>(R.id.ad_author)
        val tvSkill = activity?.findViewById<TextView>(R.id.ad_skill)
        val tvAvailability = activity?.findViewById<TextView>(R.id.ad_availability)
        Log.d("lifecycle", "onPause: ${tvAvailability?.text.toString()}")
        val tvDescription = activity?.findViewById<TextView>(R.id.ad_description)
        val tvLocation = activity?.findViewById<TextView>(R.id.ad_location)
        val tvPrice = activity?.findViewById<TextView>(R.id.ad_price)
        val tvDate = activity?.findViewById<TextView>(R.id.ad_date)

        val re = Regex("[^0-9.]")
        var price = tvPrice?.text.toString()
        price = re.replace(price, "")
        viewModel.updateAd(
            tvAuthor?.text.toString(),
            tvSkill?.text.toString(),
            tvAvailability?.text.toString(),
            tvDescription?.text.toString(),
            price,
            tvLocation?.text.toString(),
            tvDate?.text.toString())
    }

    private fun setupChannel(selectedUserID: String, progressDialog: ProgressDialog){
        if (client.getCurrentUser() == null) {
            // Connecting User to Stream Chat
            Log.d("lifecycle", "setupUser: connecting user")
            val uid = Firebase.auth.uid.toString()
            val name = Firebase.auth.currentUser?.displayName ?: "Anonymous"

            user = User(id = uid, name = name) // TODO: complete
            val token = client.devToken(uid)
            client
                .connectUser(user, token)
                .enqueue { result ->
                    if (result.isSuccess) {
                        Log.d("lifecycle", "connecting user: ${client.getCurrentUser()}")
                        createNewChannel(selectedUserID, progressDialog)
                    } else {
                        Log.d("lifecycle", result.error().message.toString())
                    }
                }
        } else{
            //User already connected, skipping connection step
            createNewChannel(selectedUserID, progressDialog)
        }
    }
    private fun createNewChannel(selectedUserID: String, progressDialog: ProgressDialog) {

            client.createChannel(
            channelType = "messaging",
            channelId= "",
            memberIds = listOf(client.getCurrentUser()!!.id, selectedUserID),
            extraData = emptyMap()
        ).enqueue { result ->
            if (result.isSuccess) {
                Log.d("lifecycle", "createNewChannel: with cid  ")
                Log.d("lifecycle", "createNewChannel: selecteduserid: ${selectedUserID}  ")
                val bundle = bundleOf("cid" to result.data().cid)
                val skill = activity?.findViewById<TextView>(R.id.ad_skill)?.text.toString()
                val tvAvailability = activity?.findViewById<TextView>(R.id.ad_availability)
                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                var adID = sharedPref?.getString("Ad ID", null)
                if (adID == null){
                    // Generate random ID from the ad if it is new (no shared pref)
                    Log.d("lifecycle", "Generating a new ad ID")
                    adID = UUID.randomUUID().toString()
                }
                database.collection("users").document(selectedUserID).collection("userAds").document(adID)
                    .set(mapOf(
                        "status" to "Requested",
                        "askerID" to client.getCurrentUser()!!.id
                    ))
                val adRef = database.collection("skills").document(skill).collection(skill).document(adID)
                adRef.update("availability", "Requested")
                viewModel.updateAvailability("Requested")
                tvAvailability?.setText("Requested")
                Log.d("lifecycle", "createNewChanneltvAvailability: ${tvAvailability?.text.toString()}")

                val targetFragment = ChatFragment()
                targetFragment.setArguments(bundle)
                val fragmentTransaction = parentFragmentManager.beginTransaction()

                fragmentTransaction
                    .replace(R.id.fragmentContainerView, targetFragment)
                    .addToBackStack(null)
                    .commit()

                if (progressDialog.isShowing) progressDialog.dismiss()

            } else {
                Log.e("lifecycle", result.error().message.toString())
            }
        }
    }

}