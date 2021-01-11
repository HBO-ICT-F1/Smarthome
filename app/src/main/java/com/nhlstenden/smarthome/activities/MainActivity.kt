package com.nhlstenden.smarthome.activities

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.nhlstenden.smarthome.R
import com.nhlstenden.smarthome.connection.Connection
import com.nhlstenden.smarthome.connection.INTERNET
import com.nhlstenden.smarthome.databinding.ActivityMainBinding
import com.nhlstenden.smarthome.databinding.InfoDialogBinding
import com.nhlstenden.smarthome.dialog.AddArduinoDialog
import com.nhlstenden.smarthome.utils.Arduino
import com.nhlstenden.smarthome.utils.Database

/** Database file name */
private const val DATABASE_NAME = "Smarthome.db"

/** Query used for creating the devices table */
private const val CREATE_TABLE =
    "CREATE TABLE IF NOT EXISTS `devices` (`name` VARCHAR(255) NOT NULL,  `ip` VARCHAR(15) NOT NULL, `port` INT(5) NOT NULL, `code` INT(4) NOT NULL)"

/**
 * Smarthome app main activity
 *
 * @author Kevin
 * @author Robert
 * @author Rutger
 * @since 1.0
 */
class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {
    /** Binding for this activity */
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    /** SQLite database used for saving arduino's */
    private val database by lazy {
        // Open/Create database
        val database = Database(openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null))

        // Create devices table
        database.exec(CREATE_TABLE)

        // Return database
        database
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Request required permissions
        requestPermissions(arrayOf(INTERNET), 0)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Check if permissions were accepted
        if (grantResults.average() != 0.0) {
            println("Permissions were denied")
            finish()
            return
        }

        // Load Arduino's from a database
        database.exec("SELECT * FROM `devices`") {
            do {
                // Get data from database
                val name = it.getStringOrNull(0) ?: continue
                val ip = it.getStringOrNull(1) ?: continue
                val port = it.getIntOrNull(2) ?: continue
                val code = it.getIntOrNull(3) ?: continue

                // Add arduino to layout
                addArduino(Arduino(name, ip, port, code))
            } while (it.moveToNext())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_arduino) {
            // Create success callback
            val onSuccess: (Arduino) -> Unit = { runOnUiThread { addArduino(it) } }

            // Create error callback
            val onError: (String) -> Unit = {
                runOnUiThread { Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show() }
            }

            // Create dialog and show
            val dialog = AddArduinoDialog(this, database, onSuccess, onError)
            dialog.show()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    /** Adds an Arduino entry to the layout */
    private fun addArduino(device: Arduino) {
        // Create buttons for all arduino's
        val button = Button(this).apply {
            setOnClickListener {
                val infoDialog = InfoDialogBinding.inflate(layoutInflater).apply {
                    name.text = device.name
                    ip.text = device.ip
                }

                val dialog = AlertDialog.Builder(this@MainActivity).create()
                dialog.setView(infoDialog.root)
                dialog.show()

                infoDialog.toggleAlarm.setOnClickListener {
                    Connection.request(device, "buzzer/code=${device.code}") {
                        infoDialog.humidity.text = it.dth.humidity
                        infoDialog.temp.text = it.dth.temperature
                        infoDialog.alarm.text = if (it.alarm) "Aan" else "Uit"
                        dialog.setView(infoDialog.root)
                    }

                }
            }

            text = device.name
            height = 160
        }

        // Add button to layout
        binding.layout.addView(button)
    }
}