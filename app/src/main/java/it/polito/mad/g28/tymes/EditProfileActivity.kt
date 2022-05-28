package it.polito.mad.g28.tymes

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
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

        Log.d("list", "onviewcreated")
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

        val btn_add = activity?.findViewById<Button>(R.id.btn_add_skill)
        val chip1 = activity?.findViewById<Chip>(R.id.chip1)
        val chip2 = activity?.findViewById<Chip>(R.id.chip2)
        val chip3 = activity?.findViewById<Chip>(R.id.chip3)
        var availableList = mutableListOf<Int>(1,2,3)

        Log.d("list", "$availableList")
        btn_add?.setOnClickListener {
            val skill = activity?.findViewById<EditText>(R.id.edit_user_skills)
            val skillText = skill?.text.toString()
            if (skillText == null || skillText.isEmpty()){
                Toast.makeText(context, "Please enter a skill!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            when(availableList.elementAt(0)){
                1 -> {
                    chip1?.text = skillText
                    chip1?.visibility = View.VISIBLE
                    availableList.remove(1)
                }
                2 -> {
                    chip2?.text = skillText
                    chip2?.visibility = View.VISIBLE
                    availableList.remove(2)
                }
                3 -> {
                    chip3?.text = skillText
                    chip3?.visibility = View.VISIBLE
                    availableList.remove(3)

                }
            }
            if (availableList.isEmpty()){
                btn_add.setEnabled(false)
            } else{
                btn_add.setEnabled(true)
            }
            skill?.setText("")
            it.hideKeyboard()
        }

        chip1?.setOnCloseIconClickListener{
            chip1.visibility = View.GONE
            chip1.text = ""
            availableList.add(1)
            btn_add?.setEnabled(true)
        }
        chip2?.setOnCloseIconClickListener{
            chip2.visibility = View.GONE
            chip2.text = ""
            availableList.add(2)
            btn_add?.setEnabled(true)
        }
        chip3?.setOnCloseIconClickListener{
            chip3.visibility = View.GONE
            chip3.text = ""
            availableList.add(3)
            btn_add?.setEnabled(true)
        }

    }

    override fun onPause() {
        // When back button is pressed or we leave fragment through menu, fragment is on pause
        super.onPause()

        val etFullName = activity?.findViewById<EditText>(R.id.edit_user_fullname)?.text.toString()
        val etNickname = activity?.findViewById<EditText>(R.id.edit_user_nickname)?.text.toString()
        val etUsername = activity?.findViewById<EditText>(R.id.edit_user_username)?.text.toString()
        val etBiography = activity?.findViewById<EditText>(R.id.edit_user_bio)?.text.toString()
        val etLocation = activity?.findViewById<EditText>(R.id.edit_user_location)?.text.toString()
        val etEmail = activity?.findViewById<EditText>(R.id.edit_user_email)?.text.toString()
        val etWebpage = activity?.findViewById<EditText>(R.id.edit_user_webpage)?.text.toString()
        val chip1 = activity?.findViewById<Chip>(R.id.chip1)?.text.toString()
        val chip2 = activity?.findViewById<Chip>(R.id.chip2)?.text.toString()
        val chip3 = activity?.findViewById<Chip>(R.id.chip3)?.text.toString()
        val etSkills = "$chip1 $chip2 $chip3"

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

    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
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