package com.nhlstenden.smarthome.activities

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.nhlstenden.smarthome.R
import com.nhlstenden.smarthome.connection.INTERNET
import com.nhlstenden.smarthome.databinding.ActivityMainBinding
import com.nhlstenden.smarthome.databinding.AddArduinoDialogBinding
import com.nhlstenden.smarthome.databinding.InfoDialogBinding

/**
 * Smarthome app main activity
 *
 * @author Kevin
 * @author Robert
 * @since 1.0
 */
class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {
    /** Binding for this activity */
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Request required permissions
        requestPermissions(arrayOf(INTERNET), 0)

        // TODO: Load Arduino's from a database
        for (x in 0..10) {
            // Create buttons for all arduino's
            addArduino()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_arduino) {
            val addDialog = AddArduinoDialogBinding.inflate(layoutInflater)
            val alertDialog = AlertDialog.Builder(this@MainActivity)
            addDialog.submitButton.setOnClickListener {
                // TODO: Add DB save
                println("add arduino")
            }
            alertDialog.setView(addDialog.root)
            alertDialog.create().show()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    /** Adds an Arduino entry to the layout */
    private fun addArduino() {
        // Create buttons for all arduino's
        val button = Button(this).apply {
            setOnClickListener {
                // TODO: Use data of arduino's
                val infoDialog = InfoDialogBinding.inflate(layoutInflater)
                infoDialog.name.text = "Arduino"
                infoDialog.ip.text = "127.0.0.1"
                infoDialog.temp.text = "30 °C"
                infoDialog.humidity.text = "40%"
                infoDialog.alarm.text = "actief"
                infoDialog.toggleAlarm.setOnClickListener {
                    // TODO: Add api call to toggle alarm
                    println("toggle alarm")
                }
                val alertDialog = AlertDialog.Builder(this@MainActivity)
                alertDialog.setView(infoDialog.root)
                alertDialog.create().show()
            }

            height = 160
            text = "test"
        }

        // Add button to layout
        binding.layout.addView(button)
    }
}