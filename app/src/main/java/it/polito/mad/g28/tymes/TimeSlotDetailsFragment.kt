package it.polito.mad.g28.tymes

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

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

        val tvTitle = activity?.findViewById<TextView>(R.id.ad_title)
        val tvAuthor = activity?.findViewById<TextView>(R.id.ad_full_name)
        val tvLocation = activity?.findViewById<TextView>(R.id.ad_location)
        val tvDatetime = activity?.findViewById<TextView>(R.id.ad_when)
        val tvDescription = activity?.findViewById<TextView>(R.id.ad_description)
        val tvPrice = activity?.findViewById<TextView>(R.id.ad_price_bid)
        val tvService = activity?.findViewById<TextView>(R.id.ad_service_bid)
        val tvTime = activity?.findViewById<TextView>(R.id.ad_issuetime)

        viewModel.adInfo.observe(viewLifecycleOwner){
            tvTitle?.text = it["Title"]
            tvAuthor?.text = it["Author"]
            tvLocation?.text = it["Location"]
            tvDatetime?.text = it["Datetime"]
            tvDescription?.text = it["Description"]
            tvPrice?.text = it["Price"]
            tvService?.text = it["Service"]
            tvTime?.text = it["Time"]
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menubar, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val tvTitle = activity?.findViewById<TextView>(R.id.ad_title)?.text.toString()
        val tvAuthor = activity?.findViewById<TextView>(R.id.ad_full_name)?.text.toString()
        val tvLocation = activity?.findViewById<TextView>(R.id.ad_location)?.text.toString()
        val tvDatetime = activity?.findViewById<TextView>(R.id.ad_when)?.text.toString()
        val tvDescription = activity?.findViewById<TextView>(R.id.ad_description)?.text.toString()
        val tvPrice = activity?.findViewById<TextView>(R.id.ad_price_bid)?.text.toString()
        val tvService = activity?.findViewById<TextView>(R.id.ad_service_bid)?.text.toString()
        val tvTime = activity?.findViewById<TextView>(R.id.ad_issuetime)?.text.toString()

        return if (item.itemId==R.id.edit_pencil_button) {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction
                .replace(R.id.fragmentContainerView, TimeSlotEditFragment())
                .addToBackStack(null)
                .commit()
            viewModel.updateAd(tvTitle,tvAuthor,tvLocation,tvDatetime,tvDescription,tvPrice,tvService,tvTime)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}