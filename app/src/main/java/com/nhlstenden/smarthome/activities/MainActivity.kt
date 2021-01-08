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
import com.nhlstenden.smarthome.R
import com.nhlstenden.smarthome.connection.Connection
import com.nhlstenden.smarthome.connection.INTERNET
import com.nhlstenden.smarthome.databinding.ActivityMainBinding
import com.nhlstenden.smarthome.databinding.AddArduinoDialogBinding
import com.nhlstenden.smarthome.databinding.InfoDialogBinding
import com.nhlstenden.smarthome.utils.Arduino
import com.nhlstenden.smarthome.utils.Database
import kotlin.concurrent.thread

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
    private val database by lazy {
        val database = Database(
            openOrCreateDatabase(
                "SmartHome.db",
                Context.MODE_PRIVATE,
                null
            )
        )
        database.exec(CREATE_TABLE)
        database
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Request required permissions
        requestPermissions(arrayOf(INTERNET), 0)

        // TODO: Load Arduino's from a database
        database.exec("SELECT * FROM `devices`") {
            do {
                for (i in 0 until it.columnCount) {
                    addArduino(
                        Arduino(
                            it.getString(0),
                            it.getString(1),
                            it.getInt(2),
                            it.getInt(3)
                        )
                    )
                }
            } while (it.moveToNext())
        }
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

        println("Permissions accepted")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_arduino) {
            val addDialog = AddArduinoDialogBinding.inflate(layoutInflater)
            val alertDialog = AlertDialog.Builder(this@MainActivity)
            addDialog.submitButton.setOnClickListener {
                val name = addDialog.name.text.toString()
                val ip = addDialog.ip.text.toString()
                val port = addDialog.port.text.toString()
                if (name.isEmpty() || ip.isEmpty() || port.isEmpty()) {
                    Toast.makeText(this@MainActivity, "Alles moet ingevuld zijn", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                thread(true) {
                    val response = Connection.pair(
                        ip, port.toInt()
                    )
                    if (response?.code == 0) {
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                "Device geeft geen response",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        return@thread
                    }
                    var available = true
                    database.exec("SELECT * FROM `devices` WHERE `name` = '$name'") {
                        if (it.count != 0) {
                            runOnUiThread {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Device naam is al gebruikt",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            return@exec
                        }
                        available = false
                    }
                    if (available) {
                        database.exec("INSERT INTO `devices` (`name`, `ip`, `port`, `code`) VALUES ('$name', '$ip', '${port.toInt()}', '${response!!.code}')")
                    }
                    return@thread
                }
            }
            alertDialog.setView(addDialog.root)
            alertDialog.create().show()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    /** Adds an Arduino entry to the layout */
    private fun addArduino(device: Arduino) {
        // Create buttons for all arduino's
        val button = Button(this).apply {
            setOnClickListener {
                // TODO: Use data of arduino's
                val infoDialog = InfoDialogBinding.inflate(layoutInflater)
                infoDialog.name.text = device.name
                infoDialog.ip.text = device.ip
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

            height = 160
            text = device.name
        }

        // Add button to layout
        binding.layout.addView(button)
    }
}