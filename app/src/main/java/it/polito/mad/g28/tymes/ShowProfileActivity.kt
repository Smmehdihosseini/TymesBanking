package it.polito.mad.g28.tymes

import android.app.ProgressDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.math.RoundingMode

class ShowProfileActivity : Fragment() {

    private val viewModel : ProfileVM by activityViewModels()
    private val database = Firebase.firestore
    private val currentUser = Firebase.auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_show_profile_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvFullName = activity?.findViewById<TextView>(R.id.user_fullname)
        val tvBiography = activity?.findViewById<TextView>(R.id.user_bio)
        val tvSkills = activity?.findViewById<TextView>(R.id.user_skills)
        val tvLocation = activity?.findViewById<TextView>(R.id.user_location)
        val tvEmail = activity?.findViewById<TextView>(R.id.user_email)
        val userPicture = activity?.findViewById<ImageView>(R.id.user_picture)
        val tvProviderRating = activity?.findViewById<TextView>(R.id.tv_provider_rate)
        val tvWorkerRating = activity?.findViewById<TextView>(R.id.tv_worker_rate)
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val authorID = sharedPref?.getString("Author ID", null)
        val tvCredits = activity?.findViewById<TextView>(R.id.tv_credits)

        if (authorID != null){
            database.collection("users").document(authorID).get()
                .addOnSuccessListener {
                    Log.d("lifecycle", "data in showprofile ${it.data!!}")
                    if (it.data!!["totalWorkerRating"] == "0"){
                        tvWorkerRating?.setText("Worker rating: None")
                    }else{
                        val number: Float? = it.data!!["workerRating"].toString().toFloat()
                        if (number==null){
                            tvWorkerRating?.setText("Worker rating: " + number.toString() + "/5")
                        }  else {
                            val rounded = number.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
                            tvWorkerRating?.setText("Worker rating: " + rounded.toString() + "/5")
                        }
                    }

                    Log.d("lifecycle", "totprovrat ${it.data!!["totalProviderRating"]}")
                    if (it.data!!["totalProviderRating"] == "0"){
                        tvWorkerRating?.setText("Provider rating: None")
                    }else{
                        val number = it.data!!["providerRating"].toString().toFloat()
                        val rounded = number.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
                        tvProviderRating?.setText("Provider rating: " + rounded.toString() + "/5")
                    }

                    database.collection("users").document(authorID).get().addOnSuccessListener {
                        tvCredits?.setText("Credits: " + it.data!!["credits"] + "TYC")
                    }

                }
        }


        viewModel.profileInfo.observe(viewLifecycleOwner){
            Log.d("lifecycle", "observing full name: ${it["Full Name"]}")
            tvFullName?.text = it["Full Name"]
            tvBiography?.text = it["Biography"]
            tvSkills?.text = it["Skills"]
            tvLocation?.text = it["Location"]
            tvEmail?.text = it["Email"]

            if (currentUser != null) {

                val progressDialog = ProgressDialog(requireContext())

                val storageRef = Firebase.storage.reference.child("${authorID}/profilePic.jpg")
                val localFile = File.createTempFile("tempImage", ".jpg")
                storageRef.getFile(localFile).addOnSuccessListener {

                    if (progressDialog.isShowing) progressDialog.dismiss()
                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                    userPicture?.setImageBitmap(bitmap)
                }
            }
        }

        val btnComments = activity?.findViewById<Button>(R.id.btn_comments)
        btnComments?.setOnClickListener {

            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction
                .replace(R.id.fragmentContainerView, CommentFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val user = Firebase.auth.currentUser
        if (sharedPref?.getString("Author ID", "notanid") == user?.uid){
            inflater.inflate(R.menu.menubar, menu)
        }

        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPause() {
        super.onPause()
        Log.d("lifecycle", "pause")
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val authorID = sharedPref?.getString("Author ID", null)
        with(sharedPref!!.edit()){
            putString("Author ID", Firebase.auth.currentUser?.uid)
            putString("Comment ID", authorID)
            apply()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val tvFullName = activity?.findViewById<TextView>(R.id.user_fullname)?.text.toString()
        val tvBiography = activity?.findViewById<TextView>(R.id.user_bio)?.text.toString()
        val tvSkills = activity?.findViewById<TextView>(R.id.user_skills)?.text.toString()
        val tvLocation = activity?.findViewById<TextView>(R.id.user_location)?.text.toString()
        val tvEmail = activity?.findViewById<TextView>(R.id.user_email)?.text.toString()

        return if (item.itemId==R.id.edit_pencil_button) {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction
                .replace(R.id.fragmentContainerView, EditProfileActivity())
                .addToBackStack(null)
                .commit()
            viewModel.updateProfile(tvFullName,tvBiography,tvSkills,tvLocation,tvEmail)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }


}