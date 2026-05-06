package com.zdroba.multipitchbuddy.ui

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zdroba.multipitchbuddy.App
import com.zdroba.multipitchbuddy.MainActivity
import com.zdroba.multipitchbuddy.R
import com.zdroba.multipitchbuddy.entity.ClimbEvent
import com.zdroba.multipitchbuddy.entity.Event
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.comparisons.compareBy

class SessionDetailsFragment : Fragment() {

    companion object {
        private const val ARG_SESSION_ID = "session_id"
        fun newInstance(sessionId: Long): SessionDetailsFragment {
            return SessionDetailsFragment().apply {
                arguments = Bundle().apply { putLong(ARG_SESSION_ID, sessionId) }
            }
        }
    }

    private lateinit var adapter: ClimbEventAdapter
    private val eventList = mutableListOf<ClimbEvent>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_session_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireActivity().application as App
        val crudSessionService = app.crudSessionService
        val crudClimbEventService = app.crudClimbEventService
        val sessionId = arguments?.getLong(ARG_SESSION_ID) ?: return

        view.background = MainActivity.appBackground?.toDrawable(resources)

        val dateText = view.findViewById<TextView>(R.id.session_date)
        val nameInput = view.findViewById<EditText>(R.id.session_name_input)
        val recyclerView = view.findViewById<RecyclerView>(R.id.event_list)

        adapter = ClimbEventAdapter(
            events = eventList,
            onEdit = { event ->
                EditEventDialog(
                    event = event,
                    onConfirm = { updated ->
                        lifecycleScope.launch {
                            crudClimbEventService.update(updated)
                            val idx = eventList.indexOfFirst { it.id == updated.id }
                            if (idx >= 0) {
                                eventList[idx] = updated
                                adapter.notifyItemChanged(idx)
                            }
                        }
                    }
                ).show(parentFragmentManager, "EditEventDialog")
            },
            onDelete = { event ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Event")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Delete") { _, _ ->
                        lifecycleScope.launch {
                            crudClimbEventService.delete(event.id)
                            val idx = eventList.indexOfFirst { it.id == event.id }
                            if (idx >= 0) {
                                eventList.removeAt(idx)
                                adapter.notifyItemRemoved(idx)
                            }
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        adapter.attachSwipeHelper(recyclerView)

        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, y, H:mm")
            .withZone(ZoneId.systemDefault())

        lifecycleScope.launch {
            val session = crudSessionService.getById(sessionId)
            dateText.text = formatter.format(session.start)
            nameInput.setText(session.name ?: "")

            val events = crudClimbEventService.getBySessionId(sessionId)
            val sorted = events.sortedWith(compareBy(
                { it.event != Event.SESSION_STARTED },
                { it.event == Event.SESSION_ENDED },
                { it.time }
            ))
            eventList.clear()
            eventList.addAll(sorted)
            adapter.notifyDataSetChanged()
        }

        nameInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // name change tracked locally, saved on submit
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        view.findViewById<ImageButton>(R.id.btn_submit).setOnClickListener {
            lifecycleScope.launch {
                val session = crudSessionService.getById(sessionId)
                crudSessionService.update(session.copy(name = nameInput.text.toString()))
                parentFragmentManager.popBackStack()
            }
        }

        view.findViewById<ImageButton>(R.id.btn_add_event).setOnClickListener {
            lifecycleScope.launch {
                val session = crudSessionService.getById(sessionId)
                AddEventDialog(
                    sessionId = sessionId,
                    sessionStart = session.start,
                    onConfirm = { newEvent ->
                        lifecycleScope.launch {
                            crudClimbEventService.save(newEvent)
                            eventList.add(newEvent)
                            eventList.sortWith(compareBy(
                                { it.event != Event.SESSION_STARTED },
                                { it.event == Event.SESSION_ENDED },
                                { it.time }
                            ))
                            adapter.notifyDataSetChanged()
                        }
                    }
                ).show(parentFragmentManager, "AddEventDialog")
            }
        }
    }
}