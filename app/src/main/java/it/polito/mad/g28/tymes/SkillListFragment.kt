package it.polito.mad.g28.tymes

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
    var adapter = SkillRecyclerViewAdapter(ArrayList(skillList))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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

        fillExampleList()

    }

    fun fillExampleList(){
//        skillList = ArrayList<SkillItem>()
//        skillList.add(SkillItem("Babysitting"))
//        skillList.add(SkillItem("Cooking"))

        val database = Firebase.firestore
        val rv = activity?.findViewById<RecyclerView>(R.id.recycler_view)

        database.collection("skills")
            .get()
            .addOnSuccessListener { skills ->
                for (skill in skills!!) {
                    Log.d("lifecycle", "data: ${skill.data}")
                    skillList.add(SkillItem(skill.data.get("skill").toString()))
                }
                rv?.adapter = SkillRecyclerViewAdapter(skillList)
                Log.d("lifecycle", "Current cites in CA: $skillList")
            }

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        Log.d("lifecycle", "setup recyclerview: $skillList")
        val recyclerView: RecyclerView = requireActivity().findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        Log.d("lifecycle", "skill list: $skillList")

        adapter = SkillRecyclerViewAdapter(skillList)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        if (skillList.isEmpty()){
            activity?.findViewById<TextView>(R.id.no_item_const)?.visibility = View.VISIBLE
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


                // Not working properly below
                // Show a message if no skill matches the search
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
}