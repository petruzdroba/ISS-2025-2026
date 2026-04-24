package com.zdroba.multipitchbuddy.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zdroba.multipitchbuddy.App
import com.zdroba.multipitchbuddy.R
import kotlinx.coroutines.launch

class LogFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireActivity().application as App
        val sessionRepository = app.sessionRepository

        val recyclerView = view.findViewById<RecyclerView>(R.id.session_list)
        val emptyText = view.findViewById<TextView>(R.id.txt_empty)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            val sessions = sessionRepository.getAll()

            if (sessions.isEmpty()) {
                emptyText.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyText.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                recyclerView.adapter = SessionAdapter(
                    sessions,
                    onClick = { session ->
                        // TODO: navigate to session details
                    },
                    onLongClick = { session ->
                        AlertDialog.Builder(requireContext())
                            .setTitle("Confirm Delete")
                            .setMessage("Do you really want to delete this session?")
                            .setPositiveButton("Delete") { _, _ ->
                                lifecycleScope.launch {
                                    sessionRepository.delete(session.id)
                                    parentFragmentManager.beginTransaction()
                                        .replace(R.id.fragment_container, LogFragment())
                                        .commit()
                                }
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    }
                )
            }
        }
    }
}