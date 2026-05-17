package com.zdroba.multipitchbuddy.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zdroba.multipitchbuddy.App
import com.zdroba.multipitchbuddy.MainActivity
import com.zdroba.multipitchbuddy.R
import kotlinx.coroutines.launch

class LogFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var crudSessionService: com.zdroba.multipitchbuddy.service.CrudSessionService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireActivity().application as App
        crudSessionService = app.crudSessionService

        view.background = MainActivity.appBackground?.toDrawable(resources)

        recyclerView = view.findViewById(R.id.session_list)
        emptyText = view.findViewById(R.id.txt_empty)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadSessions()
    }

    override fun onResume() {
        super.onResume()
        loadSessions()
    }

    private fun loadSessions() {
        lifecycleScope.launch {
            val sessions = crudSessionService.getAll()

            if (!isAdded) return@launch

            if (sessions.isEmpty()) {
                emptyText.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                recyclerView.adapter = null
                return@launch
            }

            emptyText.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

            recyclerView.adapter = SessionAdapter(
                sessions,
                onClick = { session ->
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SessionDetailsFragment.newInstance(session.id))
                        .addToBackStack(null)
                        .commit()
                },
                onLongClick = { session ->
                    AlertDialog.Builder(requireContext())
                        .setTitle("Confirm Delete")
                        .setMessage("Do you really want to delete this session?")
                        .setPositiveButton("Delete") { _, _ ->
                            lifecycleScope.launch {
                                crudSessionService.delete(session.id)
                                loadSessions()
                            }
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            )
        }
    }
}