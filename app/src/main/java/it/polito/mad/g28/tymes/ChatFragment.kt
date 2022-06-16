package it.polito.mad.g28.tymes

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firestore.v1.StructuredQuery
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QuerySort
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.Member
import io.getstream.chat.android.ui.message.input.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.header.viewmodel.MessageListHeaderViewModel
import io.getstream.chat.android.ui.message.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.viewmodel.factory.MessageListViewModelFactory
import it.polito.mad.g28.tymes.databinding.FragmentChatBinding

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val client = ChatClient.instance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        setupMessages()

        binding.messagesHeaderView.setBackButtonClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = Firebase.firestore
        val currentUser = Firebase.auth.currentUser

        database.collection("users").document(currentUser!!.uid).collection("userAds")
            .whereEqualTo("askerID", arguments?.getString("askerID"))
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("lifecycle", "${document.id} => ${document.data}")
                    if (document.data.get("status") == "Requested") {
                        Log.d(
                            "lifecycle",
                            "onViewCreated: document with id ${document.id} was requested"
                        )
                        _binding?.btnAccept?.visibility = View.VISIBLE
                        _binding?.btnReject?.visibility = View.VISIBLE
                        _binding?.tvRequestInfo?.visibility = View.VISIBLE
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("lifecycle", "Error getting documents: ", exception)
            }




        _binding?.btnReject?.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Reject Request")
                .setMessage("Are you sure you want to reject this request?")
                .setNegativeButton("CANCEL") { dialog, which ->
                    // Respond to negative button press
                }
                .setPositiveButton("REJECT") { dialog, which ->
                    // Respond to positive button press
                    database.collection("users").document(currentUser!!.uid).collection("userAds")
                        .whereEqualTo("askerID", arguments?.getString("askerID"))
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                document.data["status"] == "Available"
                                database.collection("users").document(currentUser!!.uid)
                                    .collection("userAds").document(document.id)
                                    .update("status", "Available")
                                Log.d("lifecycle", "${document.id} => ${document.data} is now available")

                            }
                        }
                    _binding?.btnAccept?.visibility = View.GONE
                    _binding?.btnReject?.visibility = View.GONE
                    _binding?.tvRequestInfo?.visibility = View.GONE
                }
                .show()

        _binding?.btnAccept?.setOnClickListener {
            Log.d("lifecycle", "clicked accept")
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Accept Request")
                .setMessage("Are you sure you want to accept this request?")
                .setNegativeButton("CANCEL") { dialog, which ->
                    // Respond to negative button press
                }
                .setPositiveButton("ACCEPT") { dialog, which ->
                    // Respond to positive button press

                    database.collection("users").document(currentUser!!.uid)
                        .collection("userAds")
                        .whereEqualTo("askerID", arguments?.getString("askerID"))
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                document.data["status"] = "Unavailable"
                                Log.d("lifecycle", "${document.id} => ${document.data} is now unavailable")
                                database.collection("users").document(currentUser!!.uid)
                                    .collection("userAds").document(document.id)
                                    .update("status", "Unavailable")
                            }
                        }
                    _binding?.btnAccept?.visibility = View.GONE
                    _binding?.btnReject?.visibility = View.GONE
                    _binding?.tvRequestInfo?.visibility = View.GONE
                }
                .show()

            }


        }
    }



    private fun setupMessages() {
        val factory = MessageListViewModelFactory(cid = arguments?.getString("cid")!!)
        Log.d("lifecycle", "setupMessages: cid: ${arguments?.getString("cid")}")

        val messageListHeaderViewModel: MessageListHeaderViewModel by viewModels { factory }
        val messageListViewModel: MessageListViewModel by viewModels { factory }
        val messageInputViewModel: MessageInputViewModel by viewModels { factory }

        messageListHeaderViewModel.bindView(binding.messagesHeaderView, viewLifecycleOwner)
        messageListViewModel.bindView(binding.messageList, viewLifecycleOwner)
        messageInputViewModel.bindView(binding.messageInputView, viewLifecycleOwner)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}