package com.nhlstenden.smarthome.dialog

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.nhlstenden.smarthome.R
import com.nhlstenden.smarthome.Smarthome
import com.nhlstenden.smarthome.connection.Connection
import com.nhlstenden.smarthome.databinding.AddArduinoDialogBinding
import com.nhlstenden.smarthome.utils.Arduino
import kotlin.concurrent.thread

/** Regular expression used for matching ip addresses */
private const val IP_REGEX = "^([0-9]{1,3}\\.){3}[0-9]{1,3}\$"

/**
 * Dialog used for adding an [Arduino] to the application
 *
 * @author Kevin
 * @author Robert
 * @author Rutger
 * @since 1.0
 */
class AddArduinoDialog(
    context: Context,
    private val smarthome: Smarthome,
    private val onSuccess: (Arduino) -> Unit,
    private val onError: (String) -> Unit
) :
    AlertDialog(context), View.OnClickListener {
    /** Binding for this dialog */
    private val binding by lazy { AddArduinoDialogBinding.inflate(layoutInflater) }

    /** Regex for matching ip addresses */
    private val regex = Regex(IP_REGEX)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Clear window flags to enable focussing
        val flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        window?.clearFlags(flags)

        // Register click listener
        binding.confirmButton.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        // Check if the button clicked is actually the confirm button
        if (v.id != binding.confirmButton.id) return

        // Get data from input fields
        val name = binding.name.text.toString()
        val ip = binding.ip.text.toString()
        val port = binding.port.text.toString().toIntOrNull()

        // Validate data and return if invalid
        if (!validate(name, ip, port)) return

        thread(true) {
            // Send pair request
            val response = Connection.pair(ip, port!!)
                ?: return@thread onError(context.getString(R.string.connect_failed))
            if (response.code == 0) return@thread onError(context.getString(R.string.pair_failed))

            // Create arduino instance
            val arduino = Arduino(name, ip, port, response.code, null)

            // Insert data into database, call onSuccess and dismiss
            smarthome.add(arduino)
            onSuccess(arduino)
            dismiss()
        }
    }

    /** Checks if input is valid */
    private fun validate(name: String, ip: String, port: Int?): Boolean {
        // Check if name is valid
        if (name.isEmpty()) {
            binding.name.error = context.getString(R.string.name_required)
            return false
        }

        // Check if ip address is valid
        if (ip.isEmpty() || !regex.matches(ip)) {
            binding.ip.error = context.getString(R.string.invalid_ip)
            return false
        }

        // Check if input port is valid
        if (port == null || port < 0 || port > 65535) {
            binding.port.error = context.getString(R.string.invalid_port)
            return false
        }

        // Check if a duplicate exists
        val duplicate = smarthome.arduinos.find { it.name == name || it.ip == ip && it.port == port } ?: return true

        // Set error message if duplicate was found
        if (duplicate.name == name) {
            // Set error message for name field
            binding.name.error = context.getString(R.string.name_taken)
        } else {
            // Set error message for ip and port fields
            binding.ip.error = context.getString(R.string.address_taken)
            binding.port.error = context.getString(R.string.address_taken)
        }

        return false
    }
}