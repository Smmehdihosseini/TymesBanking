package it.polito.mad.g28.tymes

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RatingFragment : Fragment() {

    val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
    val database = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rating, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ratingBar = activity?.findViewById<RatingBar>(R.id.ratingBar)
        val rating = ratingBar?.rating
        val btnSubmit = activity?.findViewById<Button>(R.id.btn_submit)
        btnSubmit?.setOnClickListener{

            val authorID = sharedPref?.getString("Author ID", null)
            val askerID = sharedPref?.getString("askerID", null)

            if (authorID != null) {
                database.collection("users").document(authorID).get().addOnSuccessListener {
                    var totalRatingProvider = it.data!!["totalRating"].toString().toFloat()
                    totalRatingProvider += rating!!.toFloat()
                    database.collection("users").document(authorID).update("totalRating", totalRatingProvider)
                    database.collection("users").document(authorID).get().addOnSuccessListener {
                        var nbRatingProvider = it.data!!["nbRatings"].toString().toInt()
                        nbRatingProvider += 1
                        database.collection("users").document(authorID).update("nbRating", nbRatingProvider)
                        database.collection("users").document(authorID).update("providerRating", totalRatingProvider/nbRatingProvider)
                    }
                }
            } else if (askerID != null){
                database.collection("users").document(askerID).get().addOnSuccessListener {
                    var totalRatingWorker = it.data!!["totalRating"].toString().toFloat()
                    totalRatingWorker += rating!!.toFloat()
                    database.collection("users").document(askerID).update("totalRating", totalRatingWorker)
                    database.collection("users").document(askerID).get().addOnSuccessListener {
                        var nbRatingWorker = it.data!!["nbRatings"].toString().toInt()
                        nbRatingWorker += 1
                        database.collection("users").document(askerID).update("nbRating", nbRatingWorker)
                        database.collection("users").document(askerID).update("providerRating", totalRatingWorker/nbRatingWorker)
                    }
                }
            }
        }

    }
}