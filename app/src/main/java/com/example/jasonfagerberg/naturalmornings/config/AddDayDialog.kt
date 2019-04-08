package com.example.jasonfagerberg.naturalmornings.config

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import com.example.jasonfagerberg.naturalmornings.R

class AddDayDialog(context: Context, layoutInflater: LayoutInflater, availableDays: List<DayEnum>) {
    private val dialog: AlertDialog
    private val dropdown: Spinner
    private val posButton: Button
    private val negButton: Button

    init {
        val builder = AlertDialog.Builder(context)
        val parent: ViewGroup? = null
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_day, parent, false)

        builder.setView(dialogView)
        dialog = builder.create()
        dialog.show()

        dropdown = dialog.findViewById(R.id.dropdown_select_day)
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, availableDays)
        dropdown.adapter = adapter
        dropdown.setSelection(0)

        posButton = dialog.findViewById(R.id.btn_add_day_positive)
        negButton = dialog.findViewById(R.id.btn_add_day_negative)
    }

    fun setFunctions(posFunction: (View) -> Unit, negFunction: (View) -> Unit = {dismiss()}) {
        posButton.setOnClickListener(posFunction)
        negButton.setOnClickListener(negFunction)
    }

    fun dismiss() {
        dialog.dismiss()
    }

    fun getSelectedDay() : DayEnum {
        return DayEnum.valueOf(dropdown.selectedItem.toString())
    }

    fun getSelectedFeatures() : List<Boolean> {
        return List(4) {
            when(it) {
                0 -> dialog.findViewById<CheckBox>(R.id.checkbox_light_activated).isChecked
                1 -> dialog.findViewById<CheckBox>(R.id.checkbox_play_sound).isChecked
                2 -> dialog.findViewById<CheckBox>(R.id.checkbox_blinds).isChecked
                3 -> dialog.findViewById<CheckBox>(R.id.checkbox_window).isChecked
                else -> false
            }
        }
    }
}