package edu.uw.ischool.nonzinv.awty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.PhoneNumberUtils
import android.telephony.SmsManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText

const val ALARM_ACTION = "edu.uw.ischool.newart.ALARM"

class MainActivity : AppCompatActivity() {

    lateinit var message: EditText
    lateinit var number: EditText
    lateinit var minutes: EditText
    lateinit var beginBtn: Button
    private var receiver: BroadcastReceiver? = null
    private var isRunning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        message = findViewById(R.id.textInput)
        number = findViewById(R.id.numberInput)
        minutes = findViewById(R.id.incrementMin)
        beginBtn = findViewById(R.id.buttonBegin)

        beginBtn.isEnabled = false

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) { checkFields() }
        }

        message.addTextChangedListener(textWatcher)
        number.addTextChangedListener(textWatcher)
        minutes.addTextChangedListener(textWatcher)

        beginBtn.setOnClickListener(View.OnClickListener {
            if(!isRunning) {
                startApp()
            } else {
                stopApp()
            }
        })
    }

    private fun checkFields() {
        val filled = !message.text.isNullOrBlank() &&
                !number.text.isNullOrBlank() &&
                !minutes.text.isNullOrBlank() &&
                isValid(minutes.text.toString()) &&
                validNumber(number.text.toString())

        beginBtn.isEnabled = filled
    }

    private fun startApp() {
        Log.d("START", "startApp() called.")
        val msg = message.text.toString()
        val phone = number.text.toString()
        val minuteCount = minutes.text.toString().toLong() * 1000 * 60
        Log.d("SMS", "SENDING TO $phone")
        sendSMS(phone, msg)
        if(receiver == null) {
            receiver = object: BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val numberFormat = "(${phone.substring(0, 3)}) ${phone.substring(3, 6)}-${phone.substring(6, 10)}"
                    val smsMsg = "$numberFormat: $msg"
                    Log.d("SMS", "Sending to $numberFormat")
                    sendSMS(phone, smsMsg)
                }
            }
            val filter = IntentFilter(ALARM_ACTION)
            registerReceiver(receiver, filter)
        }
        val intent = Intent(ALARM_ACTION)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarm: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            minuteCount,
            pendingIntent
        )
        beginBtn.text = "STOP"
        isRunning = true
    }

    private fun stopApp() {
        Log.d("STOP", "stopApp() called")
        val intent = Intent(ALARM_ACTION)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val alarm: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.cancel(pendingIntent)
        isRunning = false
        if (receiver != null) {
            unregisterReceiver(receiver)
            receiver = null
        }
    }

    private fun isValid(input: String): Boolean {
        return try {
            val intValue = input.toInt()
            intValue > 0
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun validNumber(input: String): Boolean {
        return PhoneNumberUtils.isGlobalPhoneNumber(input)
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Log.d("SMS", "SMS sent: $message")
        } catch (e: Exception) {
            Log.e("SMS", "Failed to send SMS: ${e.message}")
        }
    }
}