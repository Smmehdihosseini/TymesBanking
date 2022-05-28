package it.polito.mad.g28.tymes

import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.concurrent.thread


class SkillListFragment : Fragment() {


    var skillList = ArrayList<SkillItem>()
    var adapter = SkillRecyclerViewAdapter(ArrayList(skillList)) {skill: SkillItem -> onSkillClick(skill)  }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_skill_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateAdapter()
    }

    private fun populateAdapter(){

        val database = Firebase.firestore
        val rv = activity?.findViewById<RecyclerView>(R.id.recycler_view)

        database.collection("skills")
            .addSnapshotListener { skills, e->
                if (e != null) {
                    Log.w("error", "Listen failed in skill Adapter", e)
                    return@addSnapshotListener
                }

                for (skill in skills!!) {
                    skillList.add(SkillItem(skill.data.get("skill").toString()))
                }

                adapter = SkillRecyclerViewAdapter(ArrayList(skillList)) {skillItem ->  onSkillClick(skillItem)}
                val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)

                rv?.layoutManager = layoutManager
                rv?.adapter = adapter

                if (skillList.isEmpty()){
                    activity?.findViewById<TextView>(R.id.no_item_const)?.visibility = View.VISIBLE
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem: MenuItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem.getActionView() as SearchView
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.getFilter()?.filter(newText)

                // TODO: Show a message if no skill matches the search
//                if (adapter.itemCount == 0){
//                    activity?.findViewById<TextView>(R.id.no_item_const)?.visibility = View.VISIBLE
//                } else{
//                    // Show the recycler view
//
//                }
                return false
            }
        })

    }

    private fun onSkillClick(skill: SkillItem) {

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)?: return
        with(sharedPref!!.edit()){
            putString("skill", skill.skill)
            apply()
        }

        val fragmentTransaction = parentFragmentManager.beginTransaction()

        Log.d("lifecycle", skill.skill)
        fragmentTransaction
            .replace(R.id.fragmentContainerView, TimeSlotListFragment())
            .addToBackStack(null)
            .commit()
    }




}