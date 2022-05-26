package it.polito.mad.g28.tymes

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class TimeSlotDetailsFragment : Fragment() {

    private val viewModel : AdVM by activityViewModels()

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

        val adID: String
        val authorID: String

        val tvAuthor = activity?.findViewById<TextView>(R.id.ad_author)
        val tvSkill = activity?.findViewById<TextView>(R.id.ad_skill)
        val tvAvailability = activity?.findViewById<TextView>(R.id.ad_availability)
        val tvDescription = activity?.findViewById<TextView>(R.id.ad_description)
        val tvLocation = activity?.findViewById<TextView>(R.id.ad_location)
        val tvPrice = activity?.findViewById<TextView>(R.id.ad_price)
        val tvDate = activity?.findViewById<TextView>(R.id.ad_date)


        viewModel.adInfo.observe(viewLifecycleOwner){
            tvAuthor?.text = it["Author"]
            tvSkill?.text = it["Skill"]
            tvAvailability?.text = it["Availability"]
            tvDescription?.text = it["Description"]
            tvLocation?.text = it["Location"]
            tvPrice?.text = it["Price"]
            tvDate?.text = it["Date"]
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menubar, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val tvAuthor = activity?.findViewById<TextView>(R.id.ad_author)?.text.toString()
        val tvSkill = activity?.findViewById<TextView>(R.id.ad_skill)?.text.toString()
        val tvAvailability = activity?.findViewById<TextView>(R.id.ad_availability)?.text.toString()
        val tvDescription = activity?.findViewById<TextView>(R.id.ad_description)?.text.toString()
        val tvLocation = activity?.findViewById<TextView>(R.id.ad_location)?.text.toString()
        val tvPrice = activity?.findViewById<TextView>(R.id.ad_price)?.text.toString()
        val tvDate = activity?.findViewById<TextView>(R.id.ad_date)?.text.toString()

        val database = Firebase.firestore
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)?: return true
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

//        val docRef = database.collection("ads")
//            .document(adID!!)


//        docRef.get()
//            .addOnSuccessListener { document ->
//            if (document != null) {
//                Log.d("lifecycle", "DocumentSnapshot data: ${document.data?.get("adID")}")
//                with(sharedPref.edit()){
//                    putString("Ad ID", adID)
//                    apply()
//                }
//            } else {
//                Log.d("lifecycle", "No such document")
//            }
//        }
//            .addOnFailureListener { exception ->
//                Log.d("lifecycle", "get failed with ", exception)
//            }






        return if (item.itemId==R.id.edit_pencil_button) {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction
                .replace(R.id.fragmentContainerView, TimeSlotEditFragment())
                .addToBackStack(null)
                .commit()
//            viewModel.updateAd(tvAuthor, tvSkill, tvAvailability, tvDescription, tvLocation, tvPrice, tvDate)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}