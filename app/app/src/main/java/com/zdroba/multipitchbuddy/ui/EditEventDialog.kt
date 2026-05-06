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
import androidx.fragment.app.DialogFragment
import androidx.core.graphics.drawable.toDrawable
import com.zdroba.multipitchbuddy.MainActivity
import com.zdroba.multipitchbuddy.R
import com.zdroba.multipitchbuddy.entity.ClimbEvent
import com.zdroba.multipitchbuddy.entity.Event

class EditEventDialog(
    private val event: ClimbEvent,
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
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_event, null)

        val typeSpinner = view.findViewById<Spinner>(R.id.input_event_type)
        val altitudeInput = view.findViewById<EditText>(R.id.input_altitude)
        val notesInput = view.findViewById<EditText>(R.id.input_notes)

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner,
            eventTypes.map { it.name }
        )
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        typeSpinner.adapter = adapter
        typeSpinner.setSelection(eventTypes.indexOf(event.event).takeIf { it >= 0 } ?: 0)
        typeSpinner.setPopupBackgroundDrawable(MainActivity.appBackground?.toDrawable(resources))

        altitudeInput.setText(event.altitude?.toString() ?: "")
        notesInput.setText(event.notes ?: "")

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
            val updated = event.copy(
                event = eventTypes[typeSpinner.selectedItemPosition],
                altitude = altitudeInput.text.toString().toDoubleOrNull(),
                notes = notesInput.text.toString().takeIf { it.isNotBlank() }
            )
            onConfirm(updated)
            dialog.dismiss()
        }

        return dialog
    }
}