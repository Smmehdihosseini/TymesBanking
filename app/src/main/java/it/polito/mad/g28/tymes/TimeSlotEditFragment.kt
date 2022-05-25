package it.polito.mad.g28.tymes

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import java.util.*
import kotlin.concurrent.thread


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

        Log.d("onviewcreated", "")

        val etTitle = activity?.findViewById<EditText>(R.id.edit_ad_title)
        val etAuthor = activity?.findViewById<EditText>(R.id.edit_ad_full_name)
        val etLocation = activity?.findViewById<EditText>(R.id.edit_ad_location)
        val etDatetime = activity?.findViewById<EditText>(R.id.edit_ad_issuetime)
        val etDescription = activity?.findViewById<EditText>(R.id.edit_ad_description)
        val etPrice = activity?.findViewById<EditText>(R.id.edit_ad_price_bid)
        val etService = activity?.findViewById<EditText>(R.id.edit_ad_service_bid)
        val etTime = activity?.findViewById<TextView>(R.id.edit_ad_issuetime)
        val btnDatetime = activity?.findViewById<Button>(R.id.datepicker_button)

        viewModel.adInfo.observe(viewLifecycleOwner){
            etTitle?.setText(it["Title"])
            etAuthor?.setText(it["Author"])
            etLocation?.setText(it["Location"])
            etDatetime?.setText(it["Datetime"])
            etDescription?.setText(it["Description"])
            etPrice?.setText(it["Price"])
            etService?.setText(it["Service"])
            etTime?.text = it["Time"]
        }

        btnDatetime?.setOnClickListener(View.OnClickListener {
            val cldr: Calendar = Calendar.getInstance()
            val day: Int = cldr.get(Calendar.DAY_OF_MONTH)
            val month: Int = cldr.get(Calendar.MONTH)
            val year: Int = cldr.get(Calendar.YEAR)
            val hourOfDay: Int = cldr.get(Calendar.HOUR_OF_DAY)
            val minute: Int = cldr.get(Calendar.MINUTE)
            val is24HourView = true
            // date picker dialog
            val datepicker = DatePickerDialog(
                view.context,

                { _, year, monthOfYear, dayOfMonth -> etDatetime?.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year) },
                year,
                month,
                day
            )

            val timepicker = TimePickerDialog(
                view.context,
                {_, hourOfDay: Int, minute: Int -> etDatetime?.setText(etDatetime?.text.toString() + " " + hourOfDay.toString()+":"+minute.toString())},
                hourOfDay,
                minute,
                is24HourView
            )
            //Reset datetime
            etDatetime?.setText("")
            timepicker.show()
            datepicker.show()
        })


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menubar_save, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val etTitle = activity?.findViewById<EditText>(R.id.edit_ad_title)?.text.toString()
        val etAuthor = activity?.findViewById<EditText>(R.id.edit_ad_full_name)?.text.toString()
        val etLocation = activity?.findViewById<EditText>(R.id.edit_ad_location)?.text.toString()
        val etDatetime = activity?.findViewById<EditText>(R.id.edit_ad_issuetime)?.text.toString()
        val etDescription = activity?.findViewById<EditText>(R.id.edit_ad_description)?.text.toString()
        val etPrice = activity?.findViewById<EditText>(R.id.edit_ad_price_bid)?.text.toString()
        val etService = activity?.findViewById<EditText>(R.id.edit_ad_service_bid)?.text.toString()
        val tvTime = Calendar.getInstance().time.toString()

        return if (item.itemId==R.id.edit_pencil_button) {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction
                .replace(R.id.fragmentContainerView, TimeSlotDetailsFragment())
                .addToBackStack(null)
                .commit()
            viewModel.updateAd(etTitle,etAuthor,etLocation,etDatetime,etDescription,etPrice,etService,tvTime)
            true

        } else if(item.itemId==R.id.ic_add) {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction
                .replace(R.id.fragmentContainerView, TimeSlotListFragment())
                .addToBackStack(null)
                .commit()
            viewModel.add(
                etTitle,
                etAuthor,
                etLocation,
                etDatetime,
                etDescription,
                etPrice,
                etService,
                tvTime
            )
            true

        }else if (item.itemId==R.id.ic_update){
            Log.d("title", etTitle)
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction
                .replace(R.id.fragmentContainerView, TimeSlotListFragment())
                .addToBackStack(null)
                .commit()
            viewModel.sub(etTitle)
            viewModel.add(etTitle,etAuthor,etLocation,etDatetime,etDescription,etPrice,etService,tvTime)
//            viewModel.updateAd(etTitle,etAuthor,etLocation,etDatetime,etDescription,etPrice,etService,tvTime)
            true

        } else{
            super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val title = activity?.findViewById<EditText>(R.id.edit_ad_title)?.text.toString()
        val author = activity?.findViewById<EditText>(R.id.edit_ad_full_name)?.text.toString()
        val location = activity?.findViewById<EditText>(R.id.edit_ad_location)?.text.toString()
        val datetime = activity?.findViewById<EditText>(R.id.edit_ad_when)?.text.toString()
        val description = activity?.findViewById<EditText>(R.id.edit_ad_description)?.text.toString()
        val price = activity?.findViewById<EditText>(R.id.edit_ad_price_bid)?.text.toString()
        val service = activity?.findViewById<EditText>(R.id.edit_ad_service_bid)?.text.toString()
        val time = activity?.findViewById<TextView>(R.id.edit_ad_issuetime)?.text.toString()

        viewModel.updateAd(title,author,location,datetime,description,price,service,time)

    }

}