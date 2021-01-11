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
class InfoDialog(context: Context, private val device: Arduino) : AlertDialog(context), View.OnClickListener {
    /** Binding for this activity */
    private val binding by lazy { InfoDialogBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            // Register click listener
            toggleAlarm.setOnClickListener(this@InfoDialog)

            // Update device data
            name.text = device.name
            @SuppressLint("SetTextI18n")
            address.text = "${device.ip}:${device.port}"
        }
    }

    override fun onClick(v: View) {
        // Check if the button clicked is actually the toggle alarm button
        if (v.id != binding.toggleAlarm.id) return

        // Send request to buzzer toggle endpoint and update device data
        Connection.request(device, "buzzer/code=${device.code}") {
            binding.apply {
                humidity.text = it.dth.humidity
                temp.text = it.dth.temperature

                val id = if (it.alarm) R.string.alarm_on else R.string.alarm_off
                alarm.text = context.getString(id)
                setView(root)
            }
        }
    }
}