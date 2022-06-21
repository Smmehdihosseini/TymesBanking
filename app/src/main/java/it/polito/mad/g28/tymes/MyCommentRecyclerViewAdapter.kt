package it.polito.mad.g28.tymes

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import it.polito.mad.g28.tymes.placeholder.PlaceholderContent.PlaceholderItem
import it.polito.mad.g28.tymes.databinding.CommentItemBinding

class MyCommentRecyclerViewAdapter(
    private val values: List<Comment>
) : RecyclerView.Adapter<MyCommentRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            CommentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.author.text = item.name
        holder.comment.text = item.comment
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: CommentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val author: TextView = binding.tvAuthor
        val comment: TextView = binding.tvComment

        override fun toString(): String {
            return super.toString() + " '" + comment.text + "'"
        }
    }

}