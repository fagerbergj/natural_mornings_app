package com.example.jasonfagerberg.naturalmornings

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
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
                        jsonObj.getDouble("time"), jsonObj.getBoolean("lightActivated"),
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

        return view
    }

    private fun setupSwitch(view: View) {
        val switch = view.findViewById<Switch>(R.id.switch_enabled)
        switch.isChecked = day.isEnabled
        if (!day.isEnabled) disableDay(view)
        switch.setOnClickListener { _ ->
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
                box.setOnClickListener { _ ->
                    day.lightActivated = box.isChecked
                }
            }
            R.id.checkbox_play_sound ->{
                box.isChecked = day.playSound
                box.setOnClickListener { _ ->
                    day.playSound = box.isChecked
                }
            }
            R.id.checkbox_blinds ->{
                box.isChecked = day.openBlinds
                box.setOnClickListener { _ ->
                    day.openBlinds = box.isChecked
                }
            }
            R.id.checkbox_window ->{
                box.isChecked = day.openWindow
                box.setOnClickListener { _ ->
                    day.openWindow = box.isChecked
                }
            }
        }
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
