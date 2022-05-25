package it.polito.mad.g28.tymes

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class EditProfileActivity : Fragment() {

    private val viewModel : ProfileVM by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_edit_profile_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etFullName = activity?.findViewById<EditText>(R.id.edit_user_fullname)
        val etNickname = activity?.findViewById<EditText>(R.id.edit_user_nickname)
        val etUsername = activity?.findViewById<EditText>(R.id.edit_user_username)
        val etBiography = activity?.findViewById<EditText>(R.id.edit_user_bio)
        val etSkills = activity?.findViewById<EditText>(R.id.edit_user_skills)
        val etLocation = activity?.findViewById<EditText>(R.id.edit_user_location)
        val etEmail = activity?.findViewById<EditText>(R.id.edit_user_email)
        val etWebpage = activity?.findViewById<EditText>(R.id.edit_user_webpage)

        viewModel.profileInfo.observe(viewLifecycleOwner){
            etFullName?.setText(it["Full Name"])
            etNickname?.setText(it["Nickname"])
            etUsername?.setText(it["Username"])
            etBiography?.setText(it["Biography"])
            etSkills?.setText(it["Skills"])
            etLocation?.setText(it["Location"])
            etEmail?.setText(it["Email"])
            etWebpage?.setText(it["Webpage"])

        }
    }

    override fun onPause() {
        // When back button is pressed or we leave fragment through menu, fragment is on pause
        super.onPause()

        val etFullName = activity?.findViewById<EditText>(R.id.edit_user_fullname)?.text.toString()
        val etNickname = activity?.findViewById<EditText>(R.id.edit_user_nickname)?.text.toString()
        val etUsername = activity?.findViewById<EditText>(R.id.edit_user_username)?.text.toString()
        val etBiography = activity?.findViewById<EditText>(R.id.edit_user_bio)?.text.toString()
        val etSkills = activity?.findViewById<EditText>(R.id.edit_user_skills)?.text.toString()
        val etLocation = activity?.findViewById<EditText>(R.id.edit_user_location)?.text.toString()
        val etEmail = activity?.findViewById<EditText>(R.id.edit_user_email)?.text.toString()
        val etWebpage = activity?.findViewById<EditText>(R.id.edit_user_webpage)?.text.toString()

        // Update viewModel when leaving the edit profile fragment
        Log.d("lifecycle", "vm update")
        viewModel.updateProfile(etFullName,etNickname,etUsername,etBiography,etSkills,etLocation,etEmail,etWebpage)

        val currentUser = Firebase.auth.currentUser
        if (currentUser != null){
            // Persist user data to database when onBackPressed
            val database = Firebase.firestore
            val uid = currentUser.uid
            val user = User(uid,etFullName, etNickname, etUsername, etBiography, etSkills, etLocation, etEmail, etWebpage)
            database.collection("users").document(uid).set(user)
                .addOnSuccessListener {Log.d("lifecycle", "Successfully edited profile of $etFullName")}
                .addOnFailureListener {Log.d("lifecycle", "Did not edit profile of $etFullName properly")}
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menubar, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return if (item.itemId==R.id.edit_pencil_button) {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction
                .replace(R.id.fragmentContainerView, ShowProfileActivity())
                .addToBackStack(null)
                .commit()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}