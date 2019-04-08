package com.example.jasonfagerberg.naturalmornings.config

import android.bluetooth.*
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.jasonfagerberg.naturalmornings.BluetoothService
import com.example.jasonfagerberg.naturalmornings.R
import com.example.jasonfagerberg.naturalmornings.connect.ConnectActivity
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.Writer
import java.io.BufferedReader
import java.io.FileReader

enum class DayEnum {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

class ConfigActivity : AppCompatActivity() {
    private lateinit var dayRecyclerView: RecyclerView
    private lateinit var dayRecyclerViewAdapter: ConfigDaysAdapter
    val activeDays = ArrayList<DayEnum>()

    private lateinit var configFilePath: String
    private lateinit var bluetoothService: BluetoothService
    private var pi : BluetoothDevice? = null
    lateinit var config : Map<DayEnum, Day>

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_config)

        // see if connected to pi via BT
        bluetoothService = BluetoothService(applicationContext)
        pi = bluetoothService.getAlarmClock()

        // if not go to connect activity
        if (pi == null) {
            intent = Intent(this, ConnectActivity::class.java)
            startActivity(intent)
        }

        // get config file if it exists
        configFilePath = filesDir.absolutePath +  "/config/config.json"
        config = getConfigFromFile()

        setupFloatingActionButton()
        setupRecycler()

        findViewById<Button>(R.id.btn_save_config).setOnClickListener {
            //Write JSON file
            try {
                val f = File(filesDir, "config")
                f.mkdir()

                val output: Writer?
                val file = File(configFilePath)
                output = BufferedWriter(FileWriter(file))
                output.write(createJSONString())
                output.close()
                bluetoothService.sendToClock(createJSONString().toByteArray())
                Toast.makeText(applicationContext, "Config saved", Toast.LENGTH_LONG).show()

            } catch (e: Exception) {
                Log.e("ConfigActivity", e.message)
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
        }
        super.onCreate(savedInstanceState)
    }

    private fun getConfigFromFile() : Map<DayEnum, Day> {
        val json = getJSONobject()
        return if (json != null) {
            val map = HashMap<DayEnum, Day>()
            for (dayEnum in DayEnum.values()) {
                map[dayEnum] = convertJsonToDay(json.getJSONObject("config").getJSONObject(dayEnum.toString()))
                if (map.getValue(dayEnum).isEnabled) activeDays.add(dayEnum)
            }
            return map
        } else {
            emptyConfig()
        }
    }

    private fun convertJsonToDay(jsonObject: JSONObject) : Day {
        return Day(jsonObject.getBoolean("isEnabled"), jsonObject.getInt("time"),
                jsonObject.getBoolean("lightActivated"), jsonObject.getBoolean("playSound"),
                jsonObject.getBoolean("openBlinds"), jsonObject.getBoolean("openWindow"))
    }

    private fun emptyConfig() : Map<DayEnum, Day> {
        val map = HashMap<DayEnum, Day>()
        for (dayEnum in DayEnum.values()) {
            map[dayEnum] = Day()
        }
        return map
    }

    override fun onDestroy() {
        // Close proxy connection after use.
        bluetoothService.close()
        super.onDestroy()
    }

    private fun setupRecycler(){
        dayRecyclerView = findViewById(R.id.recycler_days)
        val linearLayoutManagerFavorites = LinearLayoutManager(this)
        linearLayoutManagerFavorites.orientation = LinearLayoutManager.VERTICAL
        dayRecyclerView.layoutManager = linearLayoutManagerFavorites

        dayRecyclerViewAdapter = ConfigDaysAdapter(this)
        dayRecyclerView.adapter = dayRecyclerViewAdapter
    }

    private fun setupFloatingActionButton() {
        val button = findViewById<FloatingActionButton>(R.id.btn_add_day)

        button.setOnClickListener {
            val dialog = AddDayDialog(this, layoutInflater, getInactiveDays().sorted())
            val posFunction = { _ : View ->
                val selectedDay = dialog.getSelectedDay()
                val features = dialog.getSelectedFeatures()
                val configDay = config.getValue(selectedDay)
                activeDays.add(selectedDay)
                configDay.isEnabled = true
                configDay.lightActivated = features[0]
                configDay.playSound = features[1]
                configDay.openBlinds = features[2]
                configDay.openWindow = features[3]
                dayRecyclerViewAdapter.notifyItemInserted(activeDays.size - 1)
                toggleAddButtonEnabled()
                dialog.dismiss()
            }
            dialog.setFunctions(posFunction)
        }
    }

    private fun getInactiveDays() : List<DayEnum> {
        val availableDays = ArrayList<DayEnum>()
        for (key in config.keys) {
            if (!config.getValue(key).isEnabled) availableDays.add(key)
        }
        return availableDays
    }

    fun toggleAddButtonEnabled() {
        val button = findViewById<FloatingActionButton>(R.id.btn_add_day)
        if (getInactiveDays().isEmpty()){
            button.isClickable = false
            button.isFocusable = false
            button.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorLightGray))
        } else {
            button.isClickable = true
            button.isFocusable = true
            button.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorBlue))
        }
    }

    private fun getJSONobject(): JSONObject? {
        return try {
            val text = StringBuilder()
            val br = BufferedReader(FileReader(configFilePath))

            var line = br.readLine()
            while (line != null) {
                text.append(line)
                text.append('\n')
                line = br.readLine()
            }
            br.close()
            JSONObject(text.toString())
        } catch (e: java.lang.Exception) {
            null
        }
    }

    private fun createJSONString() : String {
        var result = " \n{\n\t\"config\":{"
        result += "\n\t\t\"" + DayEnum.MONDAY + "\": " + config[DayEnum.MONDAY].toString() + ","
        result += "\n\t\t\"" + DayEnum.TUESDAY + "\": " + config[DayEnum.TUESDAY].toString() + ","
        result += "\n\t\t\"" + DayEnum.WEDNESDAY + "\": " + config[DayEnum.WEDNESDAY].toString() + ","
        result += "\n\t\t\"" + DayEnum.THURSDAY + "\": " + config[DayEnum.THURSDAY].toString() + ","
        result += "\n\t\t\"" + DayEnum.FRIDAY + "\": " + config[DayEnum.FRIDAY].toString() + ","
        result += "\n\t\t\"" + DayEnum.SATURDAY + "\": " + config[DayEnum.SATURDAY].toString() + ","
        result += "\n\t\t\"" + DayEnum.SUNDAY + "\": " + config[DayEnum.SUNDAY].toString() + "\n\t}\n}"
        Log.v("JSON", result)
        return result
    }
}