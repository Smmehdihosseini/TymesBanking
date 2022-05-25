package it.polito.mad.g28.tymes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = Firebase.auth.currentUser
        val btn =activity?.findViewById<Button>(R.id.btn_auth)

        if (user != null) {
            // If user is authenticated, the button should enable him to disconnect
            btn?.setOnClickListener {
                    Firebase.auth.signOut()
                    if(Firebase.auth.currentUser == null) {
                        Toast.makeText(context, "Successfully disconnected from your Google Account", Toast.LENGTH_SHORT).show()
                    }
                }
        } else{
            // Do not show the disconnect button if user is not connected
            btn!!.visibility = View.GONE
        }
    }
}