package it.polito.mad.g28.tymes

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.ktx.auth
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

        val user = Firebase.auth.currentUser

        if (user != null){
            // User is authenticated
            viewModel.profileInfo.observe(viewLifecycleOwner){
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