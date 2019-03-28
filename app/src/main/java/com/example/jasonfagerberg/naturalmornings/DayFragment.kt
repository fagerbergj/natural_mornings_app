package com.example.jasonfagerberg.naturalmornings

import android.app.TimePickerDialog
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import org.json.JSONObject

private const val ARG_DAY_NAME = "dayName"
private const val ARG_DAY_JSON = "dayJSON"

class DayFragment : Fragment() {
    lateinit var day: Day


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments!![ARG_DAY_JSON] != null) {
            arguments.let {
                val jsonObj = JSONObject(it!!.getString(ARG_DAY_JSON))
                Log.v("DayFragment", it.getString(ARG_DAY_JSON))
                day = Day(jsonObj.getString("name"), jsonObj.getBoolean("isEnabled"),
                        jsonObj.getInt("time"), jsonObj.getBoolean("lightActivated"),
                        jsonObj.getBoolean("playSound"), jsonObj.getBoolean("openBlinds"),
                        jsonObj.getBoolean("openWindow"))
            }
        } else {
            arguments!!.let {
                day = Day(it.getString(ARG_DAY_NAME)!!)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_day, container, false)
        // set day name
        view.findViewById<TextView>(R.id.text_day).text = day.name
        setupSwitch(view)
        // set each feature
        setupFeatureCheckbox(view, R.id.checkbox_light_activated)
        setupFeatureCheckbox(view, R.id.checkbox_play_sound)
        setupFeatureCheckbox(view, R.id.checkbox_blinds)
        setupFeatureCheckbox(view, R.id.checkbox_window)

        setupWakeupText(view)
        return view
    }

    private fun setupWakeupText(view: View) {
        val text = view.findViewById<TextView>(R.id.text_wakeup)
        if (day.lightActivated) {
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            text.text = "You will be woken up naturally"
//            text.typeface = Typeface.DEFAULT
            text.setOnClickListener {  }
            } else {
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
            text.text = timeToString(day.time / 60, day.time % 60, false)
//            text.typeface = Typeface.DEFAULT_BOLD
            text.setOnClickListener {
                val mTimePicker: TimePickerDialog
                mTimePicker = TimePickerDialog(context!!,
                        TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                            text.text = timeToString(selectedHour, selectedMinute, false)
                            day.time = selectedHour * 60 + selectedMinute
                        }, day.time / 60, day.time / 60, false)

                mTimePicker.setTitle("Select Wakeup Time")
                mTimePicker.show()
            }
        }
    }

    private fun setupSwitch(view: View) {
        val switch = view.findViewById<Switch>(R.id.switch_enabled)
        switch.isChecked = day.isEnabled
        if (!day.isEnabled) disableDay(view)
        switch.setOnClickListener {
            day.isEnabled = switch.isChecked
            if (switch.isChecked) {
                enableDay(view)
            } else {
                disableDay(view)
            }
        }
        switch.isChecked = day.isEnabled
    }

    private fun setupFeatureCheckbox(view: View, boxId: Int) {
        val box = view.findViewById<CheckBox>(boxId)
        when (boxId) {
            R.id.checkbox_light_activated -> {
                box.isChecked = day.lightActivated
                box.setOnClickListener {
                    day.lightActivated = box.isChecked
                    setupWakeupText(view)
                }
            }
            R.id.checkbox_play_sound ->{
                box.isChecked = day.playSound
                box.setOnClickListener {
                    day.playSound = box.isChecked
                }
            }
            R.id.checkbox_blinds ->{
                box.isChecked = day.openBlinds
                box.setOnClickListener {
                    day.openBlinds = box.isChecked
                }
            }
            R.id.checkbox_window ->{
                box.isChecked = day.openWindow
                box.setOnClickListener {
                    day.openWindow = box.isChecked
                }
            }
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

    private fun enableDay(view: View){
        view.findViewById<TextView>(R.id.text_wakeup).visibility = View.VISIBLE
        view.findViewById<LinearLayout>(R.id.layout_features).visibility = View.VISIBLE
    }

    private fun disableDay(view: View){
        view.findViewById<TextView>(R.id.text_wakeup).visibility = View.GONE
        view.findViewById<LinearLayout>(R.id.layout_features).visibility = View.GONE
    }
}
