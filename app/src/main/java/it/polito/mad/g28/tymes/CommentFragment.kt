package it.polito.mad.g28.tymes

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.mad.g28.tymes.placeholder.PlaceholderContent

class CommentFragment : Fragment() {

    private var columnCount = 1
    private val database = Firebase.firestore
    var commentList = ArrayList<Comment>()
    var adapter = MyCommentRecyclerViewAdapter(commentList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.comment_item_list, container, false)}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val authorID = sharedPref?.getString("Author ID", null)
        val rv = activity?.findViewById<RecyclerView>(R.id.rv_list_comments)
        database.collection("users").document(authorID!!).collection("comments")
            .addSnapshotListener { comments, e->
                if (e != null) {
                    Log.w("error", "Listen failed in skill Adapter", e)
                    return@addSnapshotListener
                }
                commentList.clear()

                for (comment in comments!!) {
                    commentList.add(Comment(comment.data.get("name").toString(), comment.data.get("comment").toString()))
                }

                adapter = MyCommentRecyclerViewAdapter(ArrayList(commentList))
                val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                rv?.layoutManager = layoutManager
                rv?.adapter = adapter

//                if (commentList.isEmpty()){
//                    activity?.findViewById<TextView>(R.id.no_item_const)?.visibility = View.VISIBLE
//                }
            }

    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            CommentFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}