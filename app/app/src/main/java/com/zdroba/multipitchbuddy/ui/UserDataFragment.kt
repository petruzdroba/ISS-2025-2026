package com.zdroba.multipitchbuddy.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.zdroba.multipitchbuddy.App
import com.zdroba.multipitchbuddy.R
import kotlinx.coroutines.launch

class UserDataFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_user_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireActivity().application as App
        val authService = app.authService

        lifecycleScope.launch {
            try {
                val user = authService.me()
                view.findViewById<TextView>(R.id.txt_email).text = user.email
                view.findViewById<TextView>(R.id.txt_username).text = user.username
            } catch (e: Exception) {
                // handle error
            }
        }

        view.findViewById<Button>(R.id.btn_logout).setOnClickListener {
            lifecycleScope.launch {
                authService.logout()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.auth_container, LoginFragment())
                    .commit()
                val tabLayout = requireParentFragment().view
                    ?.findViewById<com.google.android.material.tabs.TabLayout>(R.id.tab_layout)
                tabLayout?.visibility = View.VISIBLE
            }
        }
    }
}