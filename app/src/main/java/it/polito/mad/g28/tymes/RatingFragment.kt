package it.polito.mad.g28.tymes

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RatingFragment : Fragment() {


    val database = Firebase.firestore
    private val currentUser = Firebase.auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rating, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val comment = activity?.findViewById<TextInputEditText>(R.id.edit_rating)
        val ratingBar = activity?.findViewById<RatingBar>(R.id.ratingBar)

        val btnSubmit = activity?.findViewById<Button>(R.id.btn_submit)
        btnSubmit?.setOnClickListener{

            val rating = ratingBar?.rating

            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
            val adID = sharedPref?.getString("AdID", null)
            val authorID = sharedPref?.getString("Author ID", null)
            val askerID = sharedPref?.getString("askerID", null)
            Log.d("lifecycle", "onViewCreated: adID $adID")
            Log.d("lifecycle", "onViewCreated: askerID $askerID")
            Log.d("lifecycle", "onViewCreated: authorID $authorID")

            if (currentUser?.uid == authorID && authorID != null) {
                Log.d("lifecycle", "inelseif")
                database.collection("users").document(askerID!!).get().addOnSuccessListener {
                    Log.d("lifecycle", "data : ${it.data!!}")
                    var totalRatingWorker = it.data!!["totalWorkerRating"].toString().toFloat()
                    totalRatingWorker += rating!!.toFloat()
                    database.collection("users").document(askerID).update("totalWorkerRating", totalRatingWorker).addOnSuccessListener {
                        database.collection("users").document(askerID).get().addOnSuccessListener {
                            Log.d("lifecycle", "updating number of ratings of asker")
                            var nbRatingWorker = it.data!!["nbRatingWorker"].toString().toInt()
                            nbRatingWorker += 1
                            database.collection("users").document(askerID).update("nbRatingWorker", nbRatingWorker).addOnSuccessListener {
                                database.collection("users").document(askerID).update("workerRating", totalRatingWorker/nbRatingWorker).addOnSuccessListener {
                                    Log.d("lifecycle", "workerrating is set")
                                }
                            }
                        }

                        if (comment?.text.toString().isNotEmpty()){
                            val map = hashMapOf<String, String>("comment" to comment?.text.toString(), "ID" to authorID)
                            database.collection("users").document(askerID!!).collection("comments").add(map).addOnSuccessListener {
                                Log.d("lifecycle", "comment is not empty and is sent")
                            }
                        }
                    }
                }

            } else if (askerID != null){
                Log.d("lifecycle", "asker id is $askerID")
                database.collection("users").document(authorID!!).get().addOnSuccessListener {
                    var totalRatingProvider = it.data!!["totalProviderRating"].toString().toFloat()
                    totalRatingProvider += rating!!.toFloat()
                    database.collection("users").document(authorID).update("totalProviderRating", totalRatingProvider)
                    database.collection("users").document(authorID).get().addOnSuccessListener {
                        var nbRatingProvider = it.data!!["nbRatingProvider"].toString().toInt()
                        nbRatingProvider += 1
                        database.collection("users").document(authorID).update("nbRatingProvider", nbRatingProvider)
                        database.collection("users").document(authorID).update("providerRating", totalRatingProvider/nbRatingProvider)

                        if (comment?.text.toString().isNotEmpty()){
                            val map = hashMapOf<String, String>("comment" to comment?.text.toString(), "ID" to askerID)
                            database.collection("users").document(authorID!!).collection("comments").add(map)
                        }
                    }
                }
            }

            database.collection("ads").document(adID!!).update("isRated", true)

            Toast.makeText(context, "Rating was sent", Toast.LENGTH_SHORT).show()
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction
                .replace(R.id.fragmentContainerView, SkillListFragment())
                .addToBackStack(null)
                .commit()

        }

    }
}