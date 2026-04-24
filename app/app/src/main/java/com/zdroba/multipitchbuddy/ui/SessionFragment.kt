package com.zdroba.multipitchbuddy.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zdroba.multipitchbuddy.R

class SessionFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_session, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.event_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        // TODO: attach adapter with events from sessionService

        view.findViewById<Button>(R.id.btn_end_session).setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirm End")
                .setMessage("Are you sure you want to end the session?")
                .setPositiveButton("End") { _, _ ->
                    // TODO: sessionService.end()
                    parentFragmentManager.popBackStack()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        view.findViewById<FloatingActionButton>(R.id.btn_add_note).setOnClickListener {
            val input = EditText(requireContext())
            input.hint = "Enter your note"
            AlertDialog.Builder(requireContext())
                .setTitle("Add Note")
                .setView(input)
                .setPositiveButton("Add") { _, _ ->
                    val note = input.text.toString()
                    // TODO: sessionService.recordEvent with note
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}