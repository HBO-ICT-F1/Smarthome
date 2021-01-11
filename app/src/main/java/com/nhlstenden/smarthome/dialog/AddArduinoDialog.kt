package com.nhlstenden.smarthome.dialog

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.nhlstenden.smarthome.activities.MainActivity
import com.nhlstenden.smarthome.connection.Connection
import com.nhlstenden.smarthome.databinding.AddArduinoDialogBinding
import com.nhlstenden.smarthome.utils.Arduino
import com.nhlstenden.smarthome.utils.Database
import kotlin.concurrent.thread

/** Regular expression used for matching ip addresses */
private const val IP_REGEX = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\$"

/**
 * Dialog used for adding an arduino to the application
 *
 * @author Kevin
 * @author Robert
 * @author Rutger
 * @since 1.0
 */
class AddArduinoDialog(
    context: MainActivity,
    private val database: Database,
    private val onSuccess: (Arduino) -> Unit,
    private val onError: (String) -> Unit
) :
    AlertDialog(context), View.OnClickListener {
    /** Binding for this dialog */
    private val binding by lazy { AddArduinoDialogBinding.inflate(context.layoutInflater) }

    /** Regex for matching ip addresses */
    private val regex = Regex(IP_REGEX)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        setContentView(binding.root)

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
            val response = Connection.pair(ip, port!!) ?: return@thread onError("Failed to connect to device")
            if (response.code == 0) return@thread onError("Failed to pair with device")

            // Insert data into database
            database.exec("INSERT INTO `devices` (`name`, `ip`, `port`, `code`) VALUES ('$name', '$ip', '$port', '${response.code}')")
            dismiss()

            // Call onSuccess
            onSuccess(Arduino(name, ip, port, 234))
        }
    }

    /** Checks if input is valid */
    private fun validate(name: String, ip: String, port: Int?): Boolean {
        // Check if name is valid
        if (name.isEmpty()) {
            binding.name.error = "Name is required"
            return false
        }

        // Check if ip address is valid
        if (ip.isEmpty() || !regex.matches(ip)) {
            binding.ip.error = "Invalid ip"
            return false
        }

        // Check if input port is valid
        if (port == null || port < 0 || port > 65535) {
            binding.port.error = "Invalid port"
            return false
        }

        // Check if data is already in the database
        return !database.exec("SELECT * FROM `devices` WHERE `name`='$name'") {
            binding.name.error = "Name already in use"
        }
    }
}