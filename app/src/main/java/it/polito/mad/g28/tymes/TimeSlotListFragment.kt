package it.polito.mad.g28.tymes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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

        val rv = activity?.findViewById<RecyclerView>(R.id.rv)
        rv?.layoutManager = LinearLayoutManager(context)
        vm.adverts.observe(this.viewLifecycleOwner) {
            rv?.adapter = MyAdRecyclerViewAdapter(it) { advert, edit-> onAdClick(advert, edit)}
        }

        val fab: View? = activity?.findViewById(R.id.fab)
        fab?.setOnClickListener {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction
                .replace(R.id.fragmentContainerView, TimeSlotEditFragment())
                .addToBackStack(null)
                .commit()
        }

    }

    private fun onAdClick(advert: Advert, edit: Boolean) {
        vm.updateAd(advert.title, advert.author, advert.location, advert.datetime, advert.description, advert.price, advert.service, advert.time)
//        vm.updateAdDB(advert.id, advert.title, advert.author, advert.location, advert.datetime, advert.description, advert.price, advert.service, advert.time)

        val fragmentTransaction = parentFragmentManager.beginTransaction()
        if (edit){
            fragmentTransaction
                .replace(R.id.fragmentContainerView, TimeSlotEditFragment())
                .addToBackStack(null)
                .commit()
        } else{
            Log.d("itemid", advert.id.toString())
            fragmentTransaction
                .replace(R.id.fragmentContainerView, TimeSlotDetailsFragment())
                .addToBackStack(null)
                .commit()
        }
    }

}