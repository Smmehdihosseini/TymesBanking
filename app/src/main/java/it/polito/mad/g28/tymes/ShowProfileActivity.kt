package it.polito.mad.g28.tymes

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
        val tvNickname = activity?.findViewById<TextView>(R.id.user_nickname)
        val tvUsername = activity?.findViewById<TextView>(R.id.user_username)
        val tvBiography = activity?.findViewById<TextView>(R.id.user_bio)
        val tvSkills = activity?.findViewById<TextView>(R.id.user_skills)
        val tvLocation = activity?.findViewById<TextView>(R.id.user_location)
        val tvEmail = activity?.findViewById<TextView>(R.id.user_email)
        val tvWebpage = activity?.findViewById<TextView>(R.id.user_webpage)

        val database = Firebase.firestore
        val user = Firebase.auth.currentUser

        if (user != null){
            // User is authenticated
            val bundle = arguments
            val edited = bundle?.getBoolean("edited")
            Log.d("edit", "$edited")
            if (edited == true){
                Log.d("temp", "in edited")
                viewModel.profileInfo.observe(viewLifecycleOwner){
                    Log.d("temp", "full name ${it["Full Name"]}")
                    tvFullName?.text = it["Full Name"]
                    tvNickname?.text = it["Nickname"]
                    tvUsername?.text = it["Username"]
                    tvBiography?.text = it["Biography"]
                    tvSkills?.text = it["Skills"]
                    tvLocation?.text = it["Location"]
                    tvEmail?.text = it["Email"]
                    tvWebpage?.text = it["Webpage"]
                }
            } else {
                val userData = database.collection("users").document(user.uid)
                userData.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val map = document.data
                            tvFullName?.text = map?.get("fullname").toString()
                            tvNickname?.text = map?.get("nickname").toString()
                            tvUsername?.text = map?.get("username").toString()
                            tvBiography?.text = map?.get("biography").toString()
                            tvSkills?.text = map?.get("skills").toString()
                            tvLocation?.text = map?.get("location").toString()
                            tvEmail?.text = map?.get("email").toString()
                            tvWebpage?.text = map?.get("webpage").toString()
                            Log.d("lifecycle", "User is authenticated and TVs are updated")
                        } else {
                            Log.d("lifecycle", "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("lifecycle", "get failed with ", exception)
                    }
            }
        } else{
            // User is not authenticated
            Log.d("temp", "in edited")
            viewModel.profileInfo.observe(viewLifecycleOwner){
                Log.d("temp", "full name ${it["Full Name"]}")
                tvFullName?.text = it["Full Name"]
                tvNickname?.text = it["Nickname"]
                tvUsername?.text = it["Username"]
                tvBiography?.text = it["Biography"]
                tvSkills?.text = it["Skills"]
                tvLocation?.text = it["Location"]
                tvEmail?.text = it["Email"]
                tvWebpage?.text = it["Webpage"]
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menubar, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val tvFullName = activity?.findViewById<TextView>(R.id.user_fullname)?.text.toString()
        val tvNickname = activity?.findViewById<TextView>(R.id.user_nickname)?.text.toString()
        val tvUsername = activity?.findViewById<TextView>(R.id.user_username)?.text.toString()
        val tvBiography = activity?.findViewById<TextView>(R.id.user_bio)?.text.toString()
        val tvSkills = activity?.findViewById<TextView>(R.id.user_skills)?.text.toString()
        val tvLocation = activity?.findViewById<TextView>(R.id.user_location)?.text.toString()
        val tvEmail = activity?.findViewById<TextView>(R.id.user_email)?.text.toString()
        val tvWebpage = activity?.findViewById<TextView>(R.id.user_webpage)?.text.toString()

        return if (item.itemId==R.id.edit_pencil_button) {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction
                .replace(R.id.fragmentContainerView, EditProfileActivity())
                .addToBackStack(null)
                .commit()
            viewModel.updateProfile(tvFullName,tvNickname,tvUsername,tvBiography,tvSkills,tvLocation,tvEmail,tvWebpage)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

}