package com.zdroba.multipitchbuddy.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zdroba.multipitchbuddy.R
import com.zdroba.multipitchbuddy.entity.Session
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class SessionAdapter(
    private val sessions: List<Session>,
    private val onClick: (Session) -> Unit,
    private val onLongClick: (Session) -> Unit
) : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

    private val formatter = DateTimeFormatter.ofPattern("EEE, MMMM d")
        .withZone(ZoneId.systemDefault())

    inner class SessionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.session_title)
        val location: TextView = view.findViewById(R.id.session_location)
        val duration: TextView = view.findViewById(R.id.session_duration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_session, parent, false)
        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessions[position]

        val dateStr = formatter.format(session.start)
        holder.title.text = if (session.name != null) "${session.name} on $dateStr"
        else "Climb on $dateStr"

        if (session.latitude != null && session.longitude != null) {
            holder.location.visibility = View.VISIBLE
            holder.location.text = "Lat: ${"%.4f".format(session.latitude)} Long: ${"%.4f".format(session.longitude)}"
        } else {
            holder.location.visibility = View.GONE
        }

        holder.duration.text = "Duration: ${getDuration(session.start, session.end)}"

        holder.itemView.setOnClickListener { onClick(session) }
        holder.itemView.setOnLongClickListener { onLongClick(session); true }
    }

    override fun getItemCount() = sessions.size

    private fun getDuration(start: Instant, end: Instant?): String {
        if (end == null) return "Ongoing"
        val mins = Duration.between(start, end).toMinutes()
        return if (mins < 60) "$mins min${if (mins != 1L) "s" else ""}"
        else {
            val hrs = mins / 60
            val rem = mins % 60
            "${hrs} hr${if (hrs != 1L) "s" else ""}${if (rem > 0) " $rem min" else ""}"
        }
    }
}