package com.zdroba.multipitchbuddy.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.zdroba.multipitchbuddy.App
import com.zdroba.multipitchbuddy.MainActivity
import com.zdroba.multipitchbuddy.R
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.background = MainActivity.appBackground?.toDrawable(resources)

        val app = requireActivity().application as App
        val authService = app.authService

        lifecycleScope.launch {
            val isLoggedIn = authService.isLoggedIn()
            if (isLoggedIn) {
                showUserData()
            } else {
                showAuthTabs()
            }
        }
    }

    fun onAuthSuccess() {
        view?.findViewById<TabLayout>(R.id.tab_layout)?.visibility = View.GONE
        showFragment(UserDataFragment())
    }

    private fun showAuthTabs() {
        val tabLayout = view?.findViewById<TabLayout>(R.id.tab_layout)
        tabLayout?.visibility = View.VISIBLE
        tabLayout?.addTab(tabLayout.newTab().setText("Log In"))
        tabLayout?.addTab(tabLayout.newTab().setText("Sign Up"))

        showFragment(LoginFragment())

        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> showFragment(LoginFragment())
                    1 -> showFragment(RegisterFragment())
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun showUserData() {
        view?.findViewById<TabLayout>(R.id.tab_layout)?.visibility = View.GONE
        showFragment(UserDataFragment())
    }

    private fun showFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.auth_container, fragment)
            .commit()
    }
}