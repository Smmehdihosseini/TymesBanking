package it.polito.mad.g28.tymes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.Filters
import it.polito.mad.g28.tymes.databinding.FragmentChannelBinding
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.ui.channel.list.header.viewmodel.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.channel.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory

class ChannelFragment : Fragment() {

    private var _binding: FragmentChannelBinding? = null
    private val binding get() = _binding!!

    private val client = ChatClient.instance()
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChannelBinding.inflate(inflater, container, false)

        setupUser()

        binding.channelsView.setChannelItemClickListener { channel ->
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            var askerID = ""
            for (member in channel.members){
                if (member.user.id != client.getCurrentUser()?.id){
                    Log.d("lifecycle", "askerID: ${member.user.id}")
                    askerID = member.user.id
                }
            }
            val bundle = bundleOf(
                "cid" to channel.cid,
                "askerID" to askerID
            )

            val targetFragment = ChatFragment()
            targetFragment.setArguments(bundle)

            fragmentTransaction
                .replace(R.id.fragmentContainerView, targetFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.channelListHeaderView.setOnActionButtonClickListener{
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction
                .replace(R.id.fragmentContainerView, UsersListFragment())
                .addToBackStack(null)
                .commit()
        }
         return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupUser() {
        if (client.getCurrentUser() == null){
            Log.d("lifecycle", "setupUser: connecting user")
            val uid = Firebase.auth.uid.toString()
            val name = Firebase.auth.currentUser?.displayName ?: "Anonymous"

            user = User(id=uid, name=name) // TODO: complete
            val token = client.devToken(uid)
            client
                .connectUser(user, token)
                .enqueue {result ->
                    if (result.isSuccess) {
                        Log.d("lifecycle", "connecting user: ${client.getCurrentUser()}")
                        setupChannels()
                    } else {
                        Log.d("lifecycle", result.error().message.toString())
                    }
                }

        } else {
            setupChannels()
        }
    }
    private fun setupChannels() {
        val filters = Filters.and(
            Filters.eq("type", "messaging"),
            Filters.`in`("members", listOf(client.getCurrentUser()!!.id))
        )
        val viewModelFactory = ChannelListViewModelFactory(
            filters,
            ChannelListViewModel.DEFAULT_SORT
        )
        val listViewModel: ChannelListViewModel by viewModels { viewModelFactory }
        val listHeaderViewModel: ChannelListHeaderViewModel by viewModels()

        listHeaderViewModel.bindView(binding.channelListHeaderView, viewLifecycleOwner)
        listViewModel.bindView(binding.channelsView, viewLifecycleOwner)
    }
}