package it.polito.mad.g28.tymes

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController

class MyAdRecyclerViewAdapter(
        private val values: List<Advert>
) : RecyclerView.Adapter<MyAdRecyclerViewAdapter.ViewHolder>() {

interface AdvertClickListener{
    fun onAdvertClick(position: Int)
}

    inner class ViewHolder(v:View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.rvitem_title)
        val datetime: TextView = v.findViewById(R.id.rvitem_datetime)

        override fun toString(): String {
        return "$title activity is scheduled $datetime"
        }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vg = LayoutInflater.from(parent.context).inflate(R.layout.fragment_compact_ad, parent, false)
        return ViewHolder(vg)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = values[position]
        holder.title.text = item.title
        holder.datetime.text = item.datetime

//        holder.itemView.setOnClickListener { listener(item)}
        }

        override fun getItemCount(): Int = values.size
        }