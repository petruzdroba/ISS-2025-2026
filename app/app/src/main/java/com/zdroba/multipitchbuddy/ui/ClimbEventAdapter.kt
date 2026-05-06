package com.zdroba.multipitchbuddy.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.zdroba.multipitchbuddy.R
import com.zdroba.multipitchbuddy.entity.ClimbEvent
import com.zdroba.multipitchbuddy.entity.Event
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ClimbEventAdapter(
    val events: MutableList<ClimbEvent>,
    private val onEdit: ((ClimbEvent) -> Unit)? = null,
    private val onDelete: ((ClimbEvent) -> Unit)? = null
) : RecyclerView.Adapter<ClimbEventAdapter.EventViewHolder>() {

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

    fun attachSwipeHelper(recyclerView: RecyclerView) {
        val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false

            override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val event = events[viewHolder.adapterPosition]
                // don't allow swipe on session started/ended
                if (event.event == Event.SESSION_STARTED || event.event == Event.SESSION_ENDED) return 0
                return super.getSwipeDirs(recyclerView, viewHolder)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val event = events[position]
                if (direction == ItemTouchHelper.RIGHT) {
                    // edit
                    notifyItemChanged(position) // snap back
                    onEdit?.invoke(event)
                } else {
                    // delete
                    notifyItemChanged(position) // snap back
                    onDelete?.invoke(event)
                }
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val paint = Paint()
                val context = recyclerView.context

                if (dX > 0) {
                    val gradient = LinearGradient(
                        itemView.left.toFloat(), 0f,
                        itemView.left + dX, 0f,
                        Color.argb(0, 255, 255, 255),
                        Color.argb(60, 255, 255, 255),
                        Shader.TileMode.CLAMP
                    )
                    paint.shader = gradient
                    c.drawRoundRect(
                        itemView.left.toFloat(), itemView.top.toFloat() + 8,
                        itemView.left + dX, itemView.bottom.toFloat() - 8,
                        32f, 32f, paint
                    )
                    paint.shader = null

                    val icon = ContextCompat.getDrawable(recyclerView.context, android.R.drawable.ic_menu_edit)
                    icon?.setTint(Color.WHITE)
                    val iconSize = 48
                    val iconLeft = (itemView.left + minOf(dX.toInt(), 120) / 2) - iconSize / 2
                    val iconTop = (itemView.top + itemView.bottom) / 2 - iconSize / 2
                    icon?.setBounds(iconLeft, iconTop, iconLeft + iconSize, iconTop + iconSize)
                    if (dX > iconSize) icon?.draw(c)

                } else if (dX < 0) {
                    val gradient = LinearGradient(
                        itemView.right + dX, 0f,
                        itemView.right.toFloat(), 0f,
                        Color.argb(60, 255, 50, 50),
                        Color.argb(0, 255, 50, 50),
                        Shader.TileMode.CLAMP
                    )
                    paint.shader = gradient
                    c.drawRoundRect(
                        itemView.right + dX, itemView.top.toFloat() + 8,
                        itemView.right.toFloat(), itemView.bottom.toFloat() - 8,
                        32f, 32f, paint
                    )
                    paint.shader = null

                    val icon = ContextCompat.getDrawable(recyclerView.context, android.R.drawable.ic_menu_delete)
                    icon?.setTint(Color.WHITE)
                    val iconSize = 48
                    val iconLeft = itemView.right - minOf((-dX).toInt(), 120) / 2 - iconSize / 2
                    val iconTop = (itemView.top + itemView.bottom) / 2 - iconSize / 2
                    icon?.setBounds(iconLeft, iconTop, iconLeft + iconSize, iconTop + iconSize)
                    if (-dX > iconSize) icon?.draw(c)
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })
        helper.attachToRecyclerView(recyclerView)
    }
}