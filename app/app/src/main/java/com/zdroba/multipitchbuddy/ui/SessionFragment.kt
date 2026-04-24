package com.zdroba.multipitchbuddy.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import com.zdroba.multipitchbuddy.App
import com.zdroba.multipitchbuddy.R
import com.zdroba.multipitchbuddy.entity.ClimbEvent
import com.zdroba.multipitchbuddy.entity.Event
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SessionFragment : Fragment() {

    private lateinit var adapter: ClimbEventAdapter
    private val eventList = mutableListOf<ClimbEvent>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_session, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireActivity().application as App
        val sessionService = app.sessionService

        adapter = ClimbEventAdapter(eventList)
        val recyclerView = view.findViewById<RecyclerView>(R.id.event_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // collect events from session service
        lifecycleScope.launch {
            sessionService.events.collect { events ->
                eventList.clear()
                eventList.addAll(events)
                adapter.notifyDataSetChanged()
            }
        }

        view.findViewById<Button>(R.id.btn_end_session).setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirm End")
                .setMessage("Are you sure you want to end the session?")
                .setPositiveButton("End") { _, _ ->
                    lifecycleScope.launch {
                        sessionService.end()
                        parentFragmentManager.popBackStack()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        view.findViewById<ImageButton>(R.id.btn_add_note).setOnClickListener {
            val input = EditText(requireContext())
            input.hint = "Enter your note"
            AlertDialog.Builder(requireContext())
                .setTitle("Add Note")
                .setView(input)
                .setPositiveButton("Add") { _, _ ->
                    val note = input.text.toString()
                    lifecycleScope.launch {
                        sessionService.climbEventService.save(
                            ClimbEvent(
                                time = java.time.Instant.now(),
                                event = Event.MANUAL_NOTE,
                                notes = note,
                                sessionId = sessionService.currentSession.first()?.id ?: 0
                            )
                        )
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}