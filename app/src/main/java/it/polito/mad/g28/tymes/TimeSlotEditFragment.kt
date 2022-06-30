package it.polito.mad.g28.tymes

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class TimeSlotEditFragment : Fragment() {

    private val viewModel : AdVM by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_time_slot_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etAuthor = activity?.findViewById<TextInputEditText>(R.id.edit_ad_author)
        val etSkill = activity?.findViewById<TextInputEditText>(R.id.edit_ad_skill)
        val etAvailability = activity?.findViewById<TextView>(R.id.edit_ad_availability)
        val etDescription = activity?.findViewById<TextInputEditText>(R.id.edit_ad_description)
        val etLocation = activity?.findViewById<TextInputEditText>(R.id.edit_ad_location)
        val etPrice = activity?.findViewById<TextInputEditText>(R.id.edit_ad_price)
        val etDate = activity?.findViewById<TextInputEditText>(R.id.edit_ad_date)
        val btnDatetime = activity?.findViewById<Button>(R.id.datepicker_button)

        etAuthor?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etAuthor.hideKeyboard()
            }
        }
        etSkill?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etSkill.hideKeyboard()
            }
        }
        etDescription?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etDescription.hideKeyboard()
            }
        }
        etLocation?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etLocation.hideKeyboard()
            }
        }
        etPrice?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etPrice.hideKeyboard()
            }
        }


        viewModel.adInfo.observe(viewLifecycleOwner){
            etAuthor?.setText(it["Author"])
            etSkill?.setText(it["Skill"])
            etAvailability?.setText(it["Availability"])
            etDescription?.setText(it["Description"])
            etLocation?.setText(it["Location"])
            etPrice?.setText(it["Price"])
            etDate?.setText(it["Date"])
        }

        btnDatetime?.setOnClickListener{
            val cldr: Calendar = Calendar.getInstance()
            val day: Int = cldr.get(Calendar.DAY_OF_MONTH)
            val month: Int = cldr.get(Calendar.MONTH)
            val year: Int = cldr.get(Calendar.YEAR)
            val hourOfDay: Int = cldr.get(Calendar.HOUR_OF_DAY)
            val minute: Int = cldr.get(Calendar.MINUTE)
            val is24HourView = true
            // date picker dialog
            val datePicker = DatePickerDialog(
                view.context,
                { _, year, monthOfYear, dayOfMonth -> etDate?.setText(year.toString() + "/" + (monthOfYear + 1) + "/" + dayOfMonth.toString())},
                year,
                month,
                day
            )

            val timepicker = TimePickerDialog(
                view.context,
                {_, hourOfDay: Int, minute: Int -> etDate?.setText(etDate.text.toString() + " " + hourOfDay.toString()+":"+minute.toString())},
                hourOfDay,
                minute,
                is24HourView
            )
            //Reset datetime

            etDate?.setText("")
            timepicker.show()
            datePicker.show()
        }


    }

    override fun onPause() {
        super.onPause()

        val etAuthor = activity?.findViewById<TextInputEditText>(R.id.edit_ad_author)?.text.toString()
        val etSkill = activity?.findViewById<TextInputEditText>(R.id.edit_ad_skill)?.text.toString()
        val etAvailability = activity?.findViewById<TextView>(R.id.edit_ad_availability)?.text.toString()
        val etDescription = activity?.findViewById<TextInputEditText>(R.id.edit_ad_description)?.text.toString()
        val etLocation = activity?.findViewById<TextInputEditText>(R.id.edit_ad_location)?.text.toString()
        val etPrice = activity?.findViewById<TextInputEditText>(R.id.edit_ad_price)?.text.toString()
        val etDate = activity?.findViewById<TextInputEditText>(R.id.edit_ad_date)?.text.toString()



        viewModel.updateAd(etAuthor, etSkill, etAvailability, etDescription, etPrice + " TYC", etLocation, etDate)

        val database = Firebase.firestore
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
        val authorID = Firebase.auth.currentUser!!.uid
        val author = Firebase.auth.currentUser!!.displayName ?: "Anonymous"

        val re = Regex("[^0-9.]")
        var price = etPrice
        price = re.replace(price, "")

        val ad = Ad(adID, author, authorID, etSkill, etAvailability, etDescription, etLocation, price + " TYC", etDate)
        database.collection("skills").document(etSkill).set(SkillItem(etSkill))
            .addOnSuccessListener {Log.d("lifecycle", "Successfully added $etSkill")}
            .addOnFailureListener {Log.d("lifecycle", "Did not successfully add $etSkill")}
        database.collection("ads").document(adID).set(ad)
            .addOnSuccessListener {Log.d("lifecycle", "Successfully edited ad with id: $adID")}
            .addOnFailureListener {Log.d("lifecycle", "Did not edit the ad: $adID properly")}
        database.collection("users").document(authorID).collection("userAds").document(adID)
            .set(
                mapOf("status" to "Available",
                    "askerID" to "none"
            ))


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menubar, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return if (item.itemId==R.id.edit_pencil_button) {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction
                .replace(R.id.fragmentContainerView, TimeSlotDetailsFragment())
                .addToBackStack(null)
                .commit()
            true

        } else{
            super.onOptionsItemSelected(item)
        }
    }

    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

}