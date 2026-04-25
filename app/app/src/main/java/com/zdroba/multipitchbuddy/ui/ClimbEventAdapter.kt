package com.zdroba.multipitchbuddy.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zdroba.multipitchbuddy.entity.ClimbEvent
import com.zdroba.multipitchbuddy.R
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ClimbEventAdapter(private val events: List<ClimbEvent>) :
    RecyclerView.Adapter<ClimbEventAdapter.EventViewHolder>() {

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm")
        .withZone(ZoneId.systemDefault())

    inner class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val type: TextView = view.findViewById(R.id.event_type)
        val time: TextView = view.findViewById(R.id.event_time)
        val altitude: TextView = view.findViewById(R.id.event_altitude)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.type.text = event.event.name
        holder.time.text = formatter.format(event.time)
        if (event.altitude != null) {
            holder.altitude.visibility = View.VISIBLE
            holder.altitude.text = "Altitude: ${"%.2f".format(event.altitude)} m"
        } else {
            holder.altitude.visibility = View.GONE
        }
    }

    override fun getItemCount() = events.size
}