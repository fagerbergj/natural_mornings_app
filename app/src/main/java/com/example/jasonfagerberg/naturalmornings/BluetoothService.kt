package com.example.jasonfagerberg.naturalmornings

import android.bluetooth.*
import android.content.Context
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

private const val TAG = "MY_APP_DEBUG_TAG"
class BluetoothService (mContext: Context) {
    var bluetoothHeadset: BluetoothHeadset? = null
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var pi : BluetoothDevice? = null

    private val profileListener = object : BluetoothProfile.ServiceListener {

        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            if (profile == BluetoothProfile.HEADSET) {
                bluetoothHeadset = proxy as BluetoothHeadset
            }
        }

        override fun onServiceDisconnected(profile: Int) {
            if (profile == BluetoothProfile.HEADSET) {
                bluetoothHeadset = null
            }
        }
    }

    init {
        // Establish connection to the proxy.
        bluetoothAdapter?.getProfileProxy(mContext, profileListener, BluetoothProfile.HEADSET)

        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
        }

        if (bluetoothAdapter?.isEnabled == false) {
            // val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            // startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    fun getAlarmClock() : BluetoothDevice? {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address
            if (deviceHardwareAddress == "B8:27:EB:0C:74:60") {
                pi = device
                return device
            }
        }
        return null
    }

    fun sendToClock(config: ByteArray) {
        val thread = ConnectedThread(pi!!.createRfcommSocketToServiceRecord(UUID.randomUUID()))
        thread.write(config)
    }

    fun close() {
        bluetoothAdapter?.closeProfileProxy(BluetoothProfile.HEADSET, bluetoothHeadset)
    }

    private class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {

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

//            val readMsg = handler.obtainMessage(MESSAGE_READ, numBytes, -1, mmBuffer)
//            readMsg.sendToTarget()
            }
        }

        fun write(bytes: ByteArray){
            try{
                mmSocket.connect()
                if (mmSocket.isConnected) mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when sending data", e)
//                val writeErrorMsg = handler.obtainMessage(MESSAGE_TOAST)
//            val bundle = Bundle().apply {
//                putString("toast", "Couldn't send data to the other device")
//            }
//                writeErrorMsg.data = bundle
//                handler.sendMessage(writeErrorMsg)
                return
            }

//        val writtenMsg = handler.obtainMessage(MESSAGE_WRITE, -1, -1, mmBuffer)
//        writtenMsg.sendToTarget()
        }

        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }}