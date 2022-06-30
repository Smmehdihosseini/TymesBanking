package it.polito.mad.g28.tymes

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CommentFragment : Fragment() {

    private var columnCount = 1
    private val database = Firebase.firestore
    private var commentList = ArrayList<Comment>()
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
        val authorID = sharedPref?.getString("Comment ID", null)
        Log.d("lifecycle", "Comments of $authorID")
        val rv = activity?.findViewById<RecyclerView>(R.id.rv_list_comments)
        database.collection("users").document(authorID!!).collection("comments")
            .addSnapshotListener { comments, e->
                if (e != null) {
                    Log.w("error", "Listen failed in skill Adapter", e)
                    return@addSnapshotListener
                }
                commentList.clear()

                val tvNoComments = activity?.findViewById<TextView>(R.id.tv_no_comments)
                if (comments?.documents!!.isEmpty()){
                    tvNoComments?.visibility = View.VISIBLE
                } else{
                    tvNoComments?.visibility = View.GONE
                }

                for (comment in comments) {
                    if (comment != null){
                        Log.d("lifecycle", "comment data: ${comment.data}")
                        database.collection("users").document(comment.data.get("ID").toString()).get().addOnSuccessListener {
                            Log.d("lifecycle", "data in comments: ${it.data}")
                            val name = it.data!!["fullname"].toString() + ": "
                            commentList.add(Comment(name, comment.data.get("comment").toString()))
                            Log.d("lifecycle", "onViewCreated: loopadded")
                            adapter = MyCommentRecyclerViewAdapter(ArrayList(commentList))
                            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                            rv?.layoutManager = layoutManager
                            rv?.adapter = adapter
                        }
                    }
                }
            }
    }

    companion object {
        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"
    }
}