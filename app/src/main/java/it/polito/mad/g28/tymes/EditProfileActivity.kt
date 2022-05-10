package it.polito.mad.g28.tymes

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels


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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menubar, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val etFullName = activity?.findViewById<EditText>(R.id.edit_user_fullname)?.text.toString()
        val etNickname = activity?.findViewById<EditText>(R.id.edit_user_nickname)?.text.toString()
        val etUsername = activity?.findViewById<EditText>(R.id.edit_user_username)?.text.toString()
        val etBiography = activity?.findViewById<EditText>(R.id.edit_user_bio)?.text.toString()
        val etSkills = activity?.findViewById<EditText>(R.id.edit_user_skills)?.text.toString()
        val etLocation = activity?.findViewById<EditText>(R.id.edit_user_location)?.text.toString()
        val etEmail = activity?.findViewById<EditText>(R.id.edit_user_email)?.text.toString()
        val etWebpage = activity?.findViewById<EditText>(R.id.edit_user_webpage)?.text.toString()

        return if (item.itemId==R.id.edit_pencil_button) {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainerView, ShowProfileActivity()).commit()
            viewModel.updateProfile(etFullName,etNickname,etUsername,etBiography,etSkills,etLocation,etEmail,etWebpage)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val fullName = activity?.findViewById<EditText>(R.id.edit_user_fullname)?.text.toString()
        val nickname = activity?.findViewById<EditText>(R.id.edit_user_nickname)?.text.toString()
        val username = activity?.findViewById<EditText>(R.id.edit_user_username)?.text.toString()
        val biography = activity?.findViewById<EditText>(R.id.edit_user_bio)?.text.toString()
        val skills = activity?.findViewById<EditText>(R.id.edit_user_skills)?.text.toString()
        val location = activity?.findViewById<EditText>(R.id.edit_user_location)?.text.toString()
        val email = activity?.findViewById<EditText>(R.id.edit_user_email)?.text.toString()
        val webpage = activity?.findViewById<TextView>(R.id.edit_user_webpage)?.text.toString()

        viewModel.updateProfile(fullName,nickname,username,biography,skills,location,email,webpage)

    }
}