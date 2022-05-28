package it.polito.mad.g28.tymes

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TimeSlotListFragment : Fragment() {

    private val vm : AdVM by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_slot_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = Firebase.firestore
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val skill = sharedPref?.getString("skill", null)
        val rv = activity?.findViewById<RecyclerView>(R.id.rv)
        rv?.layoutManager = LinearLayoutManager(context)

        database.collection("skills").document(skill!!).collection(skill)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w("lifecycle", "Listen failed.", e)
                    return@addSnapshotListener
                }

                val ads = ArrayList<Ad>()
                for (doc in value!!) {
                    val data = doc.data
                    val ad = Ad(
                        data.get("adID").toString(),
                        data.get("authorID").toString(),
                        data.get("skill").toString(),
                        data.get("availability").toString(),
                        data.get("description").toString(),
                        data.get("location").toString(),
                        data.get("price").toString(),
                        data.get("date").toString())
                    ads.add(ad)
                }
                rv?.adapter = MyAdRecyclerViewAdapter(ads) { ad, edit-> onAdClick(ad, edit)}
                Log.d("lifecycle", "Current cites in CA: $ads")
            }


//        vm.adverts.observe(this.viewLifecycleOwner) {
//            rv?.adapter = MyAdRecyclerViewAdapter(it) { advert, edit-> onAdClick(advert, edit)}
//        }

        val fab: View? = activity?.findViewById(R.id.fab)
        fab?.setOnClickListener {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction
                .replace(R.id.fragmentContainerView, TimeSlotEditFragment())
                .addToBackStack(null)
                .commit()
        }

    }

    private fun onAdClick(ad: Ad, edit: Boolean) {

        val database = Firebase.firestore
        val docRef = database.collection("skills")
            .document(ad.adID)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val map = document.data
                    vm.updateAd(
                        map?.get("authorID").toString(),
                        map?.get("skill").toString(),
                        map?.get("availability").toString(),
                        map?.get("description").toString(),
                        map?.get("price").toString(),
                        map?.get("location").toString(),
                        map?.get("date").toString())
                    Log.d("lifecycle", "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d("lifecycle", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("lifecycle", "get failed with ", exception)
            }

        val fragmentTransaction = parentFragmentManager.beginTransaction()
        if (edit){
            fragmentTransaction
                .replace(R.id.fragmentContainerView, TimeSlotEditFragment())
                .addToBackStack(null)
                .commit()
        } else{
            Log.d("itemid", ad.adID)
            fragmentTransaction
                .replace(R.id.fragmentContainerView, TimeSlotDetailsFragment())
                .addToBackStack(null)
                .commit()
        }
    }

}