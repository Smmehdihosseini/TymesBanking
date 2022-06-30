package it.polito.mad.g28.tymes

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.File


class EditProfileActivity : Fragment() {

    private val viewModel : ProfileVM by activityViewModels()
    private val PICK_IMAGE: Int = 80

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

        val etFullName = activity?.findViewById<TextInputEditText>(R.id.edit_user_fullname)
        val etBiography = activity?.findViewById<TextInputEditText>(R.id.edit_user_bio)
        val etSkills = activity?.findViewById<TextInputEditText>(R.id.edit_user_skills)
        val etLocation = activity?.findViewById<TextInputEditText>(R.id.edit_user_location)
        val etEmail = activity?.findViewById<TextInputEditText>(R.id.edit_user_email)
        val btn_add = activity?.findViewById<Button>(R.id.btn_add_skill)
        val chip1 = activity?.findViewById<Chip>(R.id.chip1)
        val chip2 = activity?.findViewById<Chip>(R.id.chip2)
        val chip3 = activity?.findViewById<Chip>(R.id.chip3)
        val userPicture = activity?.findViewById<ImageView>(R.id.user_picture)
        val editPictureButton = activity?.findViewById<ImageButton>(R.id.edit_picture_button)
        var skillList : List<String>
        val availableList = mutableListOf<Int>()

        val progressDialog = ProgressDialog(requireContext())
        /*progressDialog.setMessage("Get Profile Picture ... ")
        progressDialog.setCancelable(true)
        progressDialog.show()*/
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val authorID = sharedPref?.getString("Author ID", null)
        Log.d("lifecycle", "author id $authorID")
        val storageRef = Firebase.storage.reference.child("${authorID}/profilePic.jpg")

        val localFile = File.createTempFile("tempImage", "jpg")
        storageRef.getFile(localFile).addOnSuccessListener {

            if (progressDialog.isShowing)
                progressDialog.dismiss()
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            userPicture?.setImageBitmap(bitmap)
            Log.d("lifecycle", "success!!!!")
        }

        editPictureButton?.setOnClickListener {
            Log.d("lifecycle", "adding button")
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
        }

        etFullName?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etFullName.hideKeyboard()
            }
        }
        etBiography?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etBiography.hideKeyboard()
            }
        }
        etSkills?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etSkills.hideKeyboard()
            }
        }
        etEmail?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etEmail.hideKeyboard()
            }
        }
        etLocation?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etLocation.hideKeyboard()
            }
        }

        viewModel.profileInfo.observe(viewLifecycleOwner){
            etFullName?.setText(it["Full Name"])
            etBiography?.setText(it["Biography"])
            etSkills?.setText(it["Skills"])
            etLocation?.setText(it["Location"])
            etEmail?.setText(it["Email"])
            skillList = etSkills?.text.toString().split(" ")
            if(skillList.isNotEmpty()) chip1?.text = skillList.elementAt(0).filter { !it.isWhitespace() }
            if(skillList.size>1)chip2?.text = skillList.elementAt(1).filter { !it.isWhitespace() }
            if(skillList.size>2)chip3?.text = skillList.elementAt(2).filter { !it.isWhitespace() }
            etSkills?.setText("")
            if (chip1?.text!!.isEmpty()){
                availableList.add(1)
            } else{
                chip1.visibility = View.VISIBLE
            }
            if (chip2?.text!!.isEmpty()){
                availableList.add(2)
            }else{
                chip2.visibility = View.VISIBLE
            }
            if (chip3?.text!!.isEmpty()){
                availableList.add(3)
            }else{
                chip3.visibility = View.VISIBLE
            }

            if (availableList.isEmpty()){
                btn_add?.setEnabled(false)
            }
        }

        btn_add?.setOnClickListener {
            val skillText = etSkills?.text.toString()
            if (skillText.isEmpty()){
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
            etSkills?.setText("")
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

        val etFullName = activity?.findViewById<TextInputEditText>(R.id.edit_user_fullname)?.text.toString()
        val etBiography = activity?.findViewById<TextInputEditText>(R.id.edit_user_bio)?.text.toString()
        val etLocation = activity?.findViewById<TextInputEditText>(R.id.edit_user_location)?.text.toString()
        val etEmail = activity?.findViewById<TextInputEditText>(R.id.edit_user_email)?.text.toString()
        val tvProviderRating = activity?.findViewById<TextView>(R.id.tv_provider_rate)?.text.toString()
        val tvWorkerRating = activity?.findViewById<TextView>(R.id.tv_worker_rate)?.text.toString()
        val chip1 = activity?.findViewById<Chip>(R.id.chip1)?.text.toString()
        val chip2 = activity?.findViewById<Chip>(R.id.chip2)?.text.toString()
        val chip3 = activity?.findViewById<Chip>(R.id.chip3)?.text.toString()
        val etSkills = "$chip1 $chip2 $chip3"

        // Update viewModel when leaving the edit profile fragment
        viewModel.updateProfile(etFullName,etBiography,etSkills,etLocation,etEmail)

        val currentUser = Firebase.auth.currentUser
        if (currentUser != null){
            // Persist user data to database when onBackPressed
            val database = Firebase.firestore
            val uid = currentUser.uid
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
            with(sharedPref!!.edit()){
                putString("Author ID", currentUser.uid)
                apply()
            }

            database.collection("users").document(uid)
                .update(mapOf(
                    "fullname" to etFullName,
                    "email" to etEmail,
                    "biography" to etBiography,
                    "skills" to etSkills,
                    "location" to etLocation,
                ))
                .addOnSuccessListener {Log.d("lifecycle", "Successfully edited profile of $etFullName")}
                .addOnFailureListener {Log.d("lifecycle", "Did not edit profile of $etFullName properly")}
        }
    }

    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE){
            val userPicture = activity?.findViewById<ImageView>(R.id.user_picture)

            userPicture?.setImageURI(data?.data) // handle chosen image
            val uid = Firebase.auth.currentUser?.uid
            val storageRef = Firebase.storage.reference.child("${uid}/profilePic.jpg")

            val editBitmap: Bitmap = Bitmap.createBitmap(userPicture!!.drawToBitmap())
            val baos = ByteArrayOutputStream()
            editBitmap.compress(Bitmap.CompressFormat.JPEG,100, baos)
            val bytesData = baos.toByteArray()
            val uploadTask = storageRef.putBytes(bytesData)

            uploadTask.addOnFailureListener {
                // Handle unsuccessful uploads
            }.addOnSuccessListener {
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                Log.d("lifecycle", "successfully added image to Storage")

            }

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("lifecycle", "Saving editprofile state")
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val authorID = sharedPref?.getString("Author ID", null)
        val storageRef = Firebase.storage.reference.child("${authorID}/profilePic.jpg")
        // If there's an upload in progress, save the reference so you can query it later
        outState.putString("reference", storageRef.toString())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        Log.d("lifecycle", "Restoring state")
        // If there was an upload in progress, get its reference and create a new StorageReference
        val stringRef = savedInstanceState?.getString("reference") ?: return
        val storageRef = Firebase.storage.getReferenceFromUrl(stringRef)
        // Find all UploadTasks under this StorageReference (in this example, there should be one)
        val tasks = storageRef.activeUploadTasks

        if (tasks.size > 0) {
            // Get the task monitoring the upload
            val task = tasks[0]
            // Add new listeners to the task using an Activity scope
            task.addOnSuccessListener {
                // Success!
                Log.d("lifecycle", "Successfully restored state and finished upload")
                // ...
            }
        }
    }
}
