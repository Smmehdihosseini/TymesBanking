package it.polito.mad.g28.tymes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User
import android.text.format.DateFormat
import android.util.Log
import java.util.*


class UsersAdapter (private val usersList: ArrayList<User>, private val onUserClick: (user: User, cid:String) -> Unit):
    RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    private val usersListFull = ArrayList(usersList)
    private val client = ChatClient.instance()

    inner class ViewHolder(v:View ): RecyclerView.ViewHolder(v) {
        val user: TextView = v.findViewById(R.id.username_textView)
        val lastActive: TextView = v.findViewById(R.id.lastActive_textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vg = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return ViewHolder(vg)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentUser = usersList[position]
        holder.user.text = currentUser.name
        holder.lastActive.text = convertDate(currentUser.lastActive!!.time)

        holder.itemView.setOnClickListener{
            createNewChannel(currentUser, holder)
        }
    }

    override fun getItemCount(): Int = usersList.size

    private fun convertDate(milliseconds: Long): String {
        return DateFormat.format("dd/MM/yyyy hh:mm a", milliseconds).toString()
    }

    private fun createNewChannel(selectedUser: User, holder: ViewHolder) {
        client.createChannel(
            channelType = "messaging",
            channelId= "",
            memberIds = listOf(client.getCurrentUser()!!.id, selectedUser.id),
            extraData = emptyMap()
        ).enqueue { result ->
            if (result.isSuccess) {
                onUserClick.invoke(selectedUser, result.data().cid)
                Log.d("lifecycle", "createNewChannel: with cid  ")
                Log.d("lifecycle", "createNewChannel: selecteduserid: ${selectedUser.id}  ")
                //navigateToChatFragment(holder, result.data().cid)
            } else {
                Log.e("lifecycle", result.error().message.toString())
            }
        }
    }

}