package com.zdroba.multipitchbuddy.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import com.zdroba.multipitchbuddy.MainActivity
import com.zdroba.multipitchbuddy.R
import com.zdroba.multipitchbuddy.entity.ClimbEvent
import com.zdroba.multipitchbuddy.entity.Event
import java.time.Instant
import java.time.ZoneId

class AddEventDialog(
    private val sessionId: Long,
    private val sessionStart: Instant,
    private val onConfirm: (ClimbEvent) -> Unit
) : DialogFragment() {

    private val eventTypes = listOf(
        Event.FALL,
        Event.PITCH_CHANGED,
        Event.REST,
        Event.RETREAT,
        Event.MANUAL_NOTE,
        Event.BAROMETER_READING
    )

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_event, null)

        val timeInput = view.findViewById<EditText>(R.id.input_time)
        val typeSpinner = view.findViewById<Spinner>(R.id.input_event_type)
        val altitudeInput = view.findViewById<EditText>(R.id.input_altitude)
        val notesInput = view.findViewById<EditText>(R.id.input_notes)

        val localTime = sessionStart.atZone(ZoneId.systemDefault()).toLocalTime()
        timeInput.setText("%02d:%02d".format(localTime.hour, localTime.minute))

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner,
            eventTypes.map { it.name }
        )
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        typeSpinner.adapter = adapter
        // default to MANUAL_NOTE
        typeSpinner.setSelection(eventTypes.indexOf(Event.MANUAL_NOTE))
        typeSpinner.setPopupBackgroundDrawable(MainActivity.appBackground?.toDrawable(resources))

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()

        dialog.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_glass_card)
        )
        view.background = MainActivity.appBackground?.toDrawable(resources)

        view.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }

        view.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            val timeParts = timeInput.text.toString().split(":").mapNotNull { it.toIntOrNull() }
            val hour = timeParts.getOrElse(0) { 0 }
            val minute = timeParts.getOrElse(1) { 0 }

            val eventTime = sessionStart.atZone(ZoneId.systemDefault())
                .withHour(hour)
                .withMinute(minute)
                .toInstant()

            val selectedEvent = eventTypes[typeSpinner.selectedItemPosition]
            val altitude = altitudeInput.text.toString().toDoubleOrNull()
            val notes = notesInput.text.toString().takeIf { it.isNotBlank() }

            val newEvent = ClimbEvent(
                time = eventTime,
                event = selectedEvent,
                altitude = altitude,
                notes = notes,
                sessionId = sessionId
            )

            onConfirm(newEvent)
            dialog.dismiss()
        }

        return dialog
    }
}