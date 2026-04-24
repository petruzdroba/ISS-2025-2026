package com.zdroba.multipitchbuddy.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.zdroba.multipitchbuddy.App
import com.zdroba.multipitchbuddy.R
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionService = (requireActivity().application as App).sessionService

        view.findViewById<Button>(R.id.btn_start_session).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SessionFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}