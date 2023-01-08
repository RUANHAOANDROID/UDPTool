package com.hao.gatetool

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.coroutineScope
import com.hao.gatetool.net.Constants
import com.hao.gatetool.net.UDPReceiver
import com.hao.gatetool.net.UDPSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private val key_number = "number"
    private val key_address = "address"
    private val key_ticket = "ticket"
    private val key_auto = "auto"

    lateinit var edSerUDPAddress: EditText
    lateinit var tvLocalAddress: TextView
    lateinit var swAutoPassGate: Switch
    lateinit var editTextTicket: EditText
    lateinit var btnSendTicket: TextView
    lateinit var btnSendGateOpen: TextView
    lateinit var edOpenGate: EditText
    lateinit var tvReceiverCon: TextView
    lateinit var tvResult: TextView
    lateinit var tvError: TextView
    lateinit var tvNumber: TextView
    lateinit var sharedPref: SharedPreferences

    var auto: Boolean = false
    lateinit var udpReceiver: UDPReceiver
    lateinit var udpSender: UDPSender
    override fun onStart() {
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = getSharedPreferences("setting", Context.MODE_PRIVATE)
        TTSUtils.init(this) {
            Log.d("Main", "onCreate: TTS init")
        }
        setContentView(R.layout.activity_main)
        initView()
        try {
            udpReceiver = UDPReceiver()
            tvLocalAddress.text = "本地端口号：${Constants.NETWORK_UDP_PORT}"
            swAutoPassGate.setOnCheckedChangeListener { _, isChecked ->
                auto = isChecked
            }
            udpSender = UDPSender()
//            Thread {
            udpReceiver.startReceiver()
            udpSender.startSender()
//            }.start()


            btnSendTicket.setOnClickListener {
                val ticket = editTextTicket.text.toString()
                if (ticket.isNullOrBlank()) {
                    Toast.makeText(this, "未填内容", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                sendUdpMsg(Pack.qrMsg(ticket, getNumber()))
                saveSetting()
            }
            btnSendGateOpen.setOnClickListener {
                val gateOpenMsg = edOpenGate.text.toString()
                if (gateOpenMsg.isNullOrBlank()) {
                    Toast.makeText(this, "未填内容", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                sendUdpMsg(Pack.passedMsg(getNumber()))
            }
            udpReceiver.registerReceiverListener { message, ipAddress, port ->
                runOnUiThread {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    tvReceiverCon.text = "来信地址:${ipAddress}:${port}"
                    tvResult.text = "接受内容：${message}"
                    if (auto) {
                        if (message.contains("A", true)) {
                            TTSUtils.getInstance().speak("请通行")
                            sendUdpMsg(Pack.passedMsg(sn = getNumber()))
                        } else {
                            TTSUtils.getInstance().speak("禁止通行")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            Log.d("Main", "onCreate: " + e.message)
            e.printStackTrace()
        }

    }

    private fun saveSetting() {
        with(sharedPref.edit()) {
            putString(key_number, tvNumber.text.toString())
            putString(key_address, edSerUDPAddress.text.toString())
            putString(key_ticket, editTextTicket.text.toString())
            putBoolean(key_auto, auto)
            apply()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        udpStop()
    }


    private fun udpStop() {
        udpSender.stopSender()
        udpReceiver.stopReceiver()
    }

    //   192.168.7.208
    private fun sendUdpMsg(
        msg: String,
        ip: String = getIpPort()[0],
        port: Int = getIpPort()[1].toInt()
    ) {
        lifecycle.coroutineScope.launch(Dispatchers.IO) {
            udpSender.send(msg, ip, port)
        }
    }

    private fun initView() {
        edSerUDPAddress = findViewById(R.id.editUDPServerAddr)
        tvLocalAddress = findViewById(R.id.tvUDPAddress)
        swAutoPassGate = findViewById(R.id.switchAuto)
        editTextTicket = findViewById(R.id.editTextTicket)
        btnSendTicket = findViewById(R.id.btnSendTicket)
        btnSendGateOpen = findViewById(R.id.btnSendOpenGate)
        edOpenGate = findViewById(R.id.edOpenGate)
        tvReceiverCon = findViewById(R.id.tvRequestContent)
        tvResult = findViewById(R.id.tvCheckRelust)
        tvError = findViewById(R.id.tvError)
        tvNumber = findViewById(R.id.tvNumber)
        tvNumber.text = "${tvNumber.text}${Random.nextInt(9999)}"
        with(sharedPref) {
            var number = getString(key_number, "null")
            var address = getString(key_address, "null")
            var ticket = getString(key_ticket, "null")
            var _auto = getBoolean(key_auto, true)
            if (number != "null")
                tvNumber.text = number
            if (address != "null")
                edSerUDPAddress.setText(address)
            if (ticket != "null")
                editTextTicket.setText(ticket)
            swAutoPassGate.isChecked = _auto
            auto = _auto
        }

    }

    private fun getNumber(): String {
        val text = tvNumber.text
        val number = text.substring(text.length - 4, text.length)
        return "00000000000000000000${number}"
    }

    private fun getIpPort(): List<String> {
        return edSerUDPAddress.text.toString().split(":")
    }
}