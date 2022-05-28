package it.polito.mad.g28.tymes

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class SkillRecyclerViewAdapter (private val skillList: ArrayList<SkillItem>, private val onSkillClick: (skill: SkillItem) -> Unit):
    RecyclerView.Adapter<SkillRecyclerViewAdapter.ViewHolder>(), Filterable {

    private val skillListFull = ArrayList<SkillItem>(skillList)

    inner class ViewHolder(v:View ): RecyclerView.ViewHolder(v) {

        val skill: TextView = v.findViewById(it.polito.mad.g28.tymes.R.id.item_skill)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vg = LayoutInflater.from(parent.context).inflate(it.polito.mad.g28.tymes.R.layout.skill_item, parent, false)
        return ViewHolder(vg)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = skillList[position]
        holder.skill.text = item.skill

//        holder.btn.setOnClickListener {
//        holder.btn.setOnClickListener {
//            onAdClick(item, true)
//        }
        holder.itemView.setOnClickListener{
            onSkillClick.invoke(item)
        }
    }

    override fun getItemCount(): Int = skillList.size


    override fun getFilter(): Filter? {
        return exampleFilter
    }

    private val exampleFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults? {
//            Log.d("lifecycle", "constraint: $constraint")
//            Log.d("lifecycle", "fulllist: $skillListFull")
//            Log.d("lifecycle", "list: $skillList")
            val filteredList: MutableList<SkillItem> = ArrayList()
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(skillListFull)
            } else {
                val filterPattern =
                    constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (item in skillListFull) {
                    if (item.skill.toLowerCase().contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
//                Log.d("lifecycle", "filtered List: $filteredList")

            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            skillList.clear()
            skillList.addAll(results.values as List<SkillItem>)
            notifyDataSetChanged()

        }
    }


}