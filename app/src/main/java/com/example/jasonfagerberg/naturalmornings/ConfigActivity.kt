package com.example.jasonfagerberg.naturalmornings

import android.bluetooth.BluetoothSocket
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.Toast
import org.json.JSONObject
import java.io.*

class ConfigActivity : AppCompatActivity() {
    lateinit var configFilePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configFilePath = filesDir.absolutePath +  "/config/config.json"
        setContentView(R.layout.activity_config)
        createDay(R.id.monday, "Monday", 0)
        createDay(R.id.tuesday, "Tuesday", 1)
        createDay(R.id.wednesday, "Wednesday", 2)
        createDay(R.id.thursday, "Thursday", 3)
        createDay(R.id.friday, "Friday", 4)
        createDay(R.id.saturday, "Saturday", 5)
        createDay(R.id.sunday, "Sunday", 6)

        findViewById<Button>(R.id.btn_save_config).setOnClickListener { _ ->
            //Write JSON file
            try {
                val f = File(filesDir, "config")
                f.mkdir()

                val output: Writer?
                val file = File(configFilePath)
                output = BufferedWriter(FileWriter(file))
                output.write(createJSONString())
                output.close()
                // todo jefferey, send file created here to raspberry pi
                val bluetoothService = BluetoothService(Handler())
                bluetoothService.write(createJSONString().toByteArray())
                Toast.makeText(applicationContext, "Config saved", Toast.LENGTH_LONG).show()

            } catch (e: Exception) {
                Log.e("ConfigActivity", e.message)
            }
        }
    }

    private fun createDay(frameId: Int, dayName: String, dayInt: Int) {
        val ft = supportFragmentManager.beginTransaction()

        val frg = DayFragment()
        ft.replace(frameId, frg)

        val bdl = Bundle()
        val json = getJSONobject()
        if (json != null) {
            val dayConfig = json.getJSONArray("config").getJSONObject(dayInt)
            bdl.putString("dayJSON",dayConfig.toString(0))
        } else {
            bdl.putString("dayName", dayName)
        }
        frg.arguments = bdl

        ft.commit()
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
        var result = " \n{\n\t\"config\":["
        result += "\n" + (supportFragmentManager.findFragmentById(R.id.monday) as DayFragment).day.toString() + ","
        result += "\n" + (supportFragmentManager.findFragmentById(R.id.tuesday) as DayFragment).day.toString() + ","
        result += "\n" + (supportFragmentManager.findFragmentById(R.id.wednesday) as DayFragment).day.toString() + ","
        result += "\n" + (supportFragmentManager.findFragmentById(R.id.thursday) as DayFragment).day.toString() + ","
        result += "\n" + (supportFragmentManager.findFragmentById(R.id.friday) as DayFragment).day.toString() + ","
        result += "\n" + (supportFragmentManager.findFragmentById(R.id.saturday) as DayFragment).day.toString() + ","
        result += "\n" + (supportFragmentManager.findFragmentById(R.id.sunday) as DayFragment).day.toString() + "\n\t]\n}"
        Log.v("JSON", result)
        return result
    }
}

private const val TAG = "MY_APP_DEBUG_TAG"

const val MESSAGE_READ: Int = 0
const val MESSAGE_WRITE: Int = 1
const val MESSAGE_TOAST: Int = 2

class BluetoothService(private val handler: Handler) {

    private inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {

        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream
        private val mmBuffer: ByteArray = ByteArray(1024)

        override fun run() {
            var numBytes: Int

            while(true) {
                numBytes = try{
                    mmInStream.read(mmBuffer)
                } catch (e: IOException){
                    Log.d(TAG, "Input stream was disconnected", e)
                    break
                }

                val readMsg = handler.obtainMessage(MESSAGE_READ, numBytes, -1, mmBuffer)
                readMsg.sendToTarget()
            }
        }

        fun write(bytes: ByteArray){
            try{
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when sending data", e)

                val writeErrorMsg = handler.obtainMessage(MESSAGE_TOAST)
                val bundle = Bundle().apply {
                    putString("toast", "Couldn't send data to the other device")
                }
                writeErrorMsg.data = bundle
                handler.sendMessage(writeErrorMsg)
                return
            }

            val writtenMsg = handler.obtainMessage(MESSAGE_WRITE, -1, -1, mmBuffer)
            writtenMsg.sendToTarget()
        }

        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }
}