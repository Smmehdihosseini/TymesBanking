package it.polito.mad.g28.tymes

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class TimeSlotListFragment : Fragment() {

    private val vm : AdVM by activityViewModels()
    var ads = ArrayList<Ad>()
    var adapter = MyAdRecyclerViewAdapter(ads) { advert, edit -> onAdClick(advert, true) }
    val itemsOrder = hashMapOf<String, Boolean>("Date" to false, "Price" to false, "Location" to false)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
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

        database.collectionGroup("ads").whereEqualTo("skill", skill)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w("lifecycle", "Listen failed.", e)
                    return@addSnapshotListener
                }

                ads.clear()
                for (doc in value!!) {
                    val data = doc.data
                    val ad = Ad(
                        data.get("adID").toString(),
                        data.get("author").toString(),
                        data.get("authorID").toString(),
                        data.get("skill").toString(),
                        data.get("availability").toString(),
                        data.get("description").toString(),
                        data.get("location").toString(),
                        data.get("price").toString(),
                        data.get("date").toString())
                    ads.add(ad)
                }

                adapter = MyAdRecyclerViewAdapter(ads) { ad, edit-> onAdClick(ad, edit)}
                rv?.adapter = adapter
                Log.d("lifecycle", "Shown ads in timeslotlist: $ads")

            }

        val fab: View? = activity?.findViewById(R.id.fab)
        fab?.setOnClickListener {
            val currentUser = Firebase.auth.currentUser
            val name = currentUser?.displayName ?: ""

            vm.updateAd(name, skill!!, "Available", "", "", "", "")
            with(sharedPref.edit()){
                putString("Ad ID", null)
                apply()
            }
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction
                .replace(R.id.fragmentContainerView, TimeSlotEditFragment())
                .addToBackStack(null)
                .commit()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sort_menu, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        Log.d("lifecycle", "onoptionsitemselected")
        val items = arrayOf("Date", "Price", "Location")


        MaterialAlertDialogBuilder(requireContext())

            .setTitle("Order by:")
            .setIcon(R.drawable.ic_swap_light)
            .setItems(items) { _, which ->
                when (which){

                    0 -> {
                        sortByDate(itemsOrder.get("Date")!!)
                        itemsOrder.put("Date", !(itemsOrder.get("Date")!!))
                    }
                    1 -> {
                        sortByPrice(itemsOrder.get("Price")!!)
                        itemsOrder.put("Price", !(itemsOrder.get("Price")!!))
                    }
                    2 -> {
                        sortByLocation(itemsOrder.get("Location")!!)
                        itemsOrder.put("Location", !(itemsOrder.get("Location")!!))
                    }
                }
            }
            .show()
        super.onOptionsItemSelected(item)
        return true
    }

    fun sortByLocation(currentlyAscending: Boolean){
        if (currentlyAscending) {
            ads.sortWith(compareByDescending{ it.location })
        } else{
            ads.sortWith(compareBy { it.location })
        }
        adapter.notifyDataSetChanged()
    }

    fun sortByPrice(currentlyAscending: Boolean){
        if (currentlyAscending) {
            ads.sortWith(compareByDescending { it.price.filter { it -> it.isDigit() }.toInt() })
        } else{
            ads.sortWith(compareBy { it.price.filter { it -> it.isDigit() }.toInt() })
        }
        adapter.notifyDataSetChanged()
    }

    fun sortByDate(currentlyAscending: Boolean){

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        if (currentlyAscending) {
            ads.sortWith(compareByDescending { sdf.parse(it.date)})
        } else{
            ads.sortWith(compareBy { sdf.parse(it.date) })
        }
        adapter.notifyDataSetChanged()
    }


    private fun onAdClick(ad: Ad, edit: Boolean) {

        val database = Firebase.firestore
        val docRef = database.collection("ads").document(ad.adID)
        val availabilityRef = database.collection("users").document(ad.authorID).collection("userAds").document(ad.adID)

        runBlocking {
            GlobalScope.launch {
                delay(50)
                changeFrag(ad)

            }
            docRef.addSnapshotListener { document, e ->
                if (e != null) {
                    Log.w("error", "Listen failed in ad Adapter", e)
                    return@addSnapshotListener
                }
                if (document != null) {
                    val map = document.data
                    vm.updateAd(
                        map?.get("author").toString(),
                        map?.get("skill").toString(),
                        map?.get("availability").toString(),
                        map?.get("description").toString(),
                        map?.get("price").toString(),
                        map?.get("location").toString(),
                        map?.get("date").toString())

                    availabilityRef.addSnapshotListener { document, _ ->
                        Log.d("lifecycle", "onAdClick: updating availability")
                        vm.updateAvailability(document?.data?.get("status").toString())

                    }

                } else {
                    Log.d("lifecycle", "No such document")
                }
            }
        }

    }
    fun changeFrag(ad: Ad){
        val fragmentTransaction = parentFragmentManager.beginTransaction()

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        with(sharedPref!!.edit()){
            putString("Ad ID", ad.adID)
            putString("Author ID", ad.authorID)
            apply()
        }

        fragmentTransaction
            .replace(R.id.fragmentContainerView, TimeSlotDetailsFragment())
            .addToBackStack(null)
            .commit()

    }

}