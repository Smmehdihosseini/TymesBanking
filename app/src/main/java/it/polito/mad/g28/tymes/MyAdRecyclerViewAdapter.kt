package it.polito.mad.g28.tymes

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class MyAdRecyclerViewAdapter(
        private val values: ArrayList<Ad>, private val onAdClick: (advert: Ad, edit: Boolean) -> Unit
) : RecyclerView.Adapter<MyAdRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(v:View ): RecyclerView.ViewHolder(v) {

        val price: TextView = v.findViewById(R.id.rvitem_price)
        val date: TextView = v.findViewById(R.id.rvitem_date)
        val location: TextView = v.findViewById(R.id.rvitem_location)
        val btn : Button = v.findViewById(R.id.edit_ad_button)

        override fun toString(): String {
            return "$price, $date, $location"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vg = LayoutInflater.from(parent.context).inflate(R.layout.fragment_compact_ad, parent, false)
        return ViewHolder(vg)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.price.text = item.price
        holder.location.text = item.location
        holder.date.text = item.date

        holder.btn.setOnClickListener {
            onAdClick(item, true)
        }
        holder.itemView.setOnClickListener{
            onAdClick.invoke(item, false)
        }
    }

    override fun getItemCount(): Int = values.size
}