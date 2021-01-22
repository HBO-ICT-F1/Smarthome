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
class InfoDialog(context: Context, private val arduino: Arduino, private val onDelete: () -> Unit) :
    AlertDialog(context), View.OnClickListener {
    /** Binding for this activity */
    private val binding by lazy { InfoDialogBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            // Register click listeners
            deleteDevice.setOnClickListener(this@InfoDialog)
            toggleAlarm.setOnClickListener(this@InfoDialog)
            testBuzzer.setOnClickListener(this@InfoDialog)

            // Update device data
            refresh()
        }
    }

    override fun onClick(v: View) {
        // Check which button was pressed
        when (v.id) {
            binding.deleteDevice.id -> {
                // Call onDelete and dismiss dialog
                onDelete()
                dismiss()
            }

            binding.toggleAlarm.id -> {
                // Send request to turn arm alarm and update device data
                Connection.request(arduino, "?code=${arduino.code}&led") {
                    // Update arduino data
                    arduino.last = it

                    // Update device data
                    refresh()
                }
            }

            binding.testBuzzer.id -> {
                // Send request to buzzer toggle endpoint and update device data
                Connection.request(arduino, "?code=${arduino.code}&buzzer") {
                    // Update arduino data
                    arduino.last = it

                    // Update device data
                    refresh()
                }
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

            // DTH instance received from the Arduino
            val dth = arduino.last?.dth

            // Check if DTH sensor is ready to update
            if (dth?.ready == 1) {
                // Set DTH fields
                temperature.text = dth.temperature.toString() + " °C"
                humidity.text = dth.humidity.toString()
            }

            // Set light value
            val unset = context.getString(R.string.not_set)
            light.text = arduino.last?.light?.toString() ?: unset

            // Set button text
            testBuzzer.text = if (arduino.last?.buzzerStatus == 1) "Disable buzzer" else "Enable buzzer"
            toggleAlarm.text = if (arduino.last?.alarmStatus == 1) "Disable alarm" else "Enable alarm"
        }
    }
}