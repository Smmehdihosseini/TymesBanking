package it.polito.mad.g28.tymes

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ShowProfileActivity : Fragment() {

    private val viewModel : ProfileVM by activityViewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_profile_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val tvFullName = activity?.findViewById<TextView>(R.id.user_fullname)
        val tvBiography = activity?.findViewById<TextView>(R.id.user_bio)
        val tvSkills = activity?.findViewById<TextView>(R.id.user_skills)
        val tvLocation = activity?.findViewById<TextView>(R.id.user_location)
        val tvEmail = activity?.findViewById<TextView>(R.id.user_email)



        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val authorID = sharedPref?.getString("Author ID", null)
        val storageRef = Firebase.storage.reference.child("${authorID}/profilePic.jpg")

        Thread {
            Thread.sleep(2000)
            val ONE_MEGABYTE: Long = 1024 * 1024
            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                // Data for "images/island.jpg" is returned, use this as needed
                val userPicture = activity?.findViewById<ImageView>(R.id.user_picture)
                userPicture?.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
                Log.d("lifecycle", "done with image")
            }.addOnFailureListener {
                // Handle any errors
            }
        }.start()
        val user = Firebase.auth.currentUser
        if (user != null){
            // User is authenticated
            viewModel.profileInfo.observe(viewLifecycleOwner){
                tvFullName?.text = it["Full Name"]
                tvBiography?.text = it["Biography"]
                tvSkills?.text = it["Skills"]
                tvLocation?.text = it["Location"]
                tvEmail?.text = it["Email"]
            }
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