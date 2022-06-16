package it.polito.mad.g28.tymes

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryUsersRequest
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.User


class UsersListFragment : Fragment() {

    var usersList = ArrayList<User>()
    var adapter = UsersAdapter(ArrayList(usersList)) {user: User, cid: String -> onUserClick(user, cid)  }

    private val client = ChatClient.instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment




        return inflater.inflate(R.layout.fragment_users_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = activity?.findViewById<RecyclerView>(R.id.rv_users)
        rv?.layoutManager = LinearLayoutManager(context)
        queryAllUsers()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chat_users_menu, menu)
        val searchItem: MenuItem = menu.findItem(R.id.menu_search)
        val searchView: SearchView = searchItem.getActionView() as SearchView
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE)
        if (!searchView.hasFocus()){
            searchView.clearFocus()
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isEmpty()){
                    queryAllUsers()
                }
                else {
                    searchUser(newText)
                }

                // TODO: Show a message if no skill matches the search
//                if (adapter.itemCount == 0){
//                    activity?.findViewById<TextView>(R.id.no_item_const)?.visibility = View.VISIBLE
//                } else{
//                    // Show the recycler view
//
//                }
                return true
            }

        })
        searchView?.setOnCloseListener {
            queryAllUsers()
            false
        }

    }

    private fun queryAllUsers() {
        val request = QueryUsersRequest(
            filter = Filters.eq("banned", false),
            //filter = Filters.ne("id", client.getCurrentUser()!!.id),
            offset = 0,
            limit = 100
        )
        client.queryUsers(request).enqueue { result ->
            if (result.isSuccess) {
                Log.d("lifecycle", "querying all users")
                val users: List<User> = result.data()
                Log.d("lifecycle", "queryAllUsers: users ${ArrayList(users)}")
                val rv = activity?.findViewById<RecyclerView>(R.id.rv_users)
                adapter = UsersAdapter(ArrayList(users)) {user: User, cid: String -> onUserClick(user, cid)}
                rv?.adapter = adapter
                adapter.notifyDataSetChanged()
                Log.d("lifecycle", "queryAllUsers: adapter items: ${adapter.itemCount}")
            } else {
                Log.e("UsersFragment", result.error().message.toString())
            }
        }
    }

    private fun searchUser(query: String) {
        val filters = Filters.and(
            Filters.autocomplete("id", query),
            Filters.ne("id", client.getCurrentUser()!!.id)
        )
        val request = QueryUsersRequest(
            filter = Filters.eq("banned", false),
            offset = 0,
            limit = 100
        )
        client.queryUsers(request).enqueue { result ->
            if (result.isSuccess) {
                val users: List<User> = result.data()
                Log.d("lifecycle", "querying users: $users")
                adapter = UsersAdapter(ArrayList(users)) {user: User, cid: String -> onUserClick(user, cid)  }
            } else {
                Log.e("UsersFragment", result.error().message.toString())
            }
        }
    }


    private fun onUserClick(user: User, cid: String) {

        //val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)?: return
        //with(sharedPref.edit()){
            //putString("users", user.skill)
            //    apply()
        //}

        val bundle = bundleOf("cid" to cid)
        val targetFragment = ChatFragment()
        targetFragment.setArguments(bundle)
        val fragmentTransaction = parentFragmentManager.beginTransaction()

        fragmentTransaction
            .replace(R.id.fragmentContainerView, targetFragment)
            .addToBackStack(null)
            .commit()
    }




}