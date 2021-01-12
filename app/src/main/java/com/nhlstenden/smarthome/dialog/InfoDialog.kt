package com.nhlstenden.smarthome.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.nhlstenden.smarthome.R
import com.nhlstenden.smarthome.connection.Connection
import com.nhlstenden.smarthome.databinding.InfoDialogBinding
import com.nhlstenden.smarthome.utils.Arduino

/**
 * Dialog used for showing device information
 *
 * @author Kevin
 * @author Robert
 * @since 1.0
 */
class InfoDialog(context: Context, private val arduino: Arduino) : AlertDialog(context), View.OnClickListener {
    /** Binding for this activity */
    private val binding by lazy { InfoDialogBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            // Register click listener
            toggleAlarm.setOnClickListener(this@InfoDialog)

            // Update device data
            refresh()
        }
    }

    override fun onClick(v: View) {
        // Check if the button clicked is actually the toggle alarm button
        if (v.id != binding.toggleAlarm.id) return

        // Send request to buzzer toggle endpoint and update device data
        Connection.request(arduino, "?code=${arduino.code}&buzzer") {
            // Update arduino data
            arduino.dth = it.dth
            arduino.alarm = it.alarm == 1

            binding.apply {
                // Update device data
                refresh()
                setView(root)
            }
        }
    }

    /** Refresh [Arduino] data */
    private fun refresh() {
        binding.apply {
            // Update device data
            name.text = arduino.name
            @SuppressLint("SetTextI18n")
            address.text = "${arduino.ip}:${arduino.port}"

            // Check if DTH sensor is ready to update
            if (arduino.dth?.ready == 1) {
                humidity.text = arduino.dth?.humidity.toString()
                temp.text = arduino.dth?.temperature.toString()
            }

            // Set alarm status
            val id = if (arduino.alarm == true) R.string.alarm_on else R.string.alarm_off
            alarm.text = context.getString(id)
        }
    }
}