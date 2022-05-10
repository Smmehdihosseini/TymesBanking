package it.polito.mad.g28.tymes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
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
            //Retrieve the list of ads and put it in the adapter
            rv?.adapter = MyAdRecyclerViewAdapter(it) { view -> onAdClick(view)}
        }

        val fab: View? = activity?.findViewById(R.id.fab)
        fab?.setOnClickListener {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainerView, TimeSlotEditFragment()).commit()
        }

    }

    private fun onAdClick(view: View) {
//        Log.d("click", activity?.findViewById<TextView>(view.id).toString())
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerView, TimeSlotDetailsFragment()).commit()

    }

}