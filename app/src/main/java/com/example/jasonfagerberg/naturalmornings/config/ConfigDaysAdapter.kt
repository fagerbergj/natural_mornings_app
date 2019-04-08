package com.example.jasonfagerberg.naturalmornings.config

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.util.TypedValue
import android.widget.CheckBox
import android.widget.TextView
import com.example.jasonfagerberg.naturalmornings.R


class ConfigDaysAdapter(private val mActivity: ConfigActivity):
        RecyclerView.Adapter<ConfigDaysAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mActivity.layoutInflater.inflate(com.example.jasonfagerberg.naturalmornings.R.layout.item_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // set day name
        val dayEnum = mActivity.activeDays[position]
        val day = mActivity.config.getValue(dayEnum)
        holder.dayTitle.text = getStringResource(dayEnum)
        // set each feature
        setupFeatureCheckboxes(holder, day)
        setupWakeupText(holder, day)
        val posAction = {
            day.isEnabled = false
            mActivity.activeDays.remove(dayEnum)
            notifyItemRemoved(position)
            mActivity.toggleAddButtonEnabled()
        }
        holder.deleteDayButton.setOnClickListener {
            val dialogClickListener = DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> posAction.invoke()
               }
            }
            val builder = AlertDialog.Builder(mActivity)
            builder.setMessage("Are you sure you want to delete your schedule for ${getStringResource(dayEnum)}?")
            builder.setPositiveButton("Yes", dialogClickListener)
            builder.setNegativeButton("No", dialogClickListener)
            builder.show()
        }
    }

        override fun getItemCount(): Int {
        var count = 0
        for (day in mActivity.config.values) {
            if (day.isEnabled) count++
        }
        return count
    }

    private fun setupWakeupText(holder: ViewHolder, day: Day) {
        val text = holder.wakeupText
        if (day.lightActivated) {
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            text.setPadding(8, 64, 8, 64)
            text.text = mActivity.resources.getString(R.string.wake_up_naturally)
            text.setOnClickListener {  }
        } else {
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
            text.setPadding(8, 40, 8, 40)
            text.text = timeToString(day.time / 60, day.time % 60, false)
            text.setOnClickListener {
                val mTimePicker: TimePickerDialog
                mTimePicker = TimePickerDialog(mActivity,
                        TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                            text.text = timeToString(selectedHour, selectedMinute, false)
                            day.time = selectedHour * 60 + selectedMinute
                        }, day.time / 60, day.time / 60, false)

                mTimePicker.setTitle(mActivity.resources.getString(R.string.select_wakeup_time))
                mTimePicker.show()
            }
        }
    }

    private fun setupFeatureCheckboxes(holder: ViewHolder, day: Day) {
        holder.lightActivateBox.isChecked = day.lightActivated
        holder.lightActivateBox.setOnClickListener {
            day.lightActivated = holder.lightActivateBox.isChecked
            setupWakeupText(holder, day)
        }

        holder.playSoundBox.isChecked = day.playSound
        holder.playSoundBox.setOnClickListener {
            day.playSound = holder.playSoundBox.isChecked
        }

        holder.openBlindsBox.isChecked = day.openBlinds
        holder.openBlindsBox.setOnClickListener {
            day.openBlinds = holder.openBlindsBox.isChecked
        }

        holder.openWindowBox.isChecked = day.openWindow
        holder.openWindowBox.setOnClickListener {
            day.openWindow = holder.openWindowBox.isChecked
        }
    }

    private fun timeToString(selectedHour: Int, selectedMinute: Int, use24HourTime: Boolean): String {
        val timePeriod: String
        var displayHour = selectedHour
        var displayMinuet = selectedMinute.toString()
        if (selectedHour >= 12 && !use24HourTime) {
            displayHour -= 12
            timePeriod = "PM"
        } else if (!use24HourTime) {
            timePeriod = "AM"
        } else {
            timePeriod = ""
        }
        if (displayHour == 0 && !use24HourTime) displayHour = 12
        if (displayMinuet.length == 1) displayMinuet = "0$displayMinuet"
        return "$displayHour:$displayMinuet $timePeriod"
    }

    private fun getStringResource(dayEnum: DayEnum) : String{
        return when(dayEnum) {
            DayEnum.MONDAY -> mActivity.resources.getString(R.string.monday)
            DayEnum.TUESDAY -> mActivity.resources.getString(R.string.tuesday)
            DayEnum.WEDNESDAY -> mActivity.resources.getString(R.string.wednesday)
            DayEnum.THURSDAY -> mActivity.resources.getString(R.string.thursday)
            DayEnum.FRIDAY -> mActivity.resources.getString(R.string.friday)
            DayEnum.SATURDAY -> mActivity.resources.getString(R.string.saturday)
            DayEnum.SUNDAY -> mActivity.resources.getString(R.string.sunday)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var dayTitle = itemView.findViewById<TextView>(R.id.text_day)
        internal var wakeupText = itemView.findViewById<TextView>(R.id.text_wakeup)
        internal var lightActivateBox = itemView.findViewById<CheckBox>(R.id.checkbox_light_activated)
        internal var playSoundBox = itemView.findViewById<CheckBox>(R.id.checkbox_play_sound)
        internal var openBlindsBox = itemView.findViewById<CheckBox>(R.id.checkbox_blinds)
        internal var openWindowBox = itemView.findViewById<CheckBox>(R.id.checkbox_window)
        internal var deleteDayButton = itemView.findViewById<AppCompatImageButton>(R.id.btn_delete_day)
    }
}