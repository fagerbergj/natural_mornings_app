package com.example.jasonfagerberg.naturalmornings.connect

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import com.example.jasonfagerberg.naturalmornings.BluetoothService
import com.example.jasonfagerberg.naturalmornings.config.ConfigActivity
import com.example.jasonfagerberg.naturalmornings.R


class ConnectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)

        val button = findViewById<AppCompatButton>(R.id.btn_connect)
        button.setOnClickListener {
            val intentBluetooth = Intent()
            intentBluetooth.action = android.provider.Settings.ACTION_BLUETOOTH_SETTINGS
            startActivity(intentBluetooth)
        }
    }

    override fun onResume() {
        super.onResume()
        if (BluetoothService(applicationContext).getAlarmClock() != null) {
            intent = Intent(this, ConfigActivity::class.java)
            startActivity(intent)
        }
    }
}
