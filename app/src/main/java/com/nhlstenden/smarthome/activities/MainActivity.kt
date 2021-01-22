package com.nhlstenden.smarthome.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.nhlstenden.smarthome.R
import com.nhlstenden.smarthome.Smarthome
import com.nhlstenden.smarthome.connection.INTERNET
import com.nhlstenden.smarthome.databinding.ActivityMainBinding
import com.nhlstenden.smarthome.dialog.AddArduinoDialog
import com.nhlstenden.smarthome.dialog.InfoDialog
import com.nhlstenden.smarthome.utils.Arduino
import com.nhlstenden.smarthome.utils.VIBRATE

/**
 * Smarthome app main activity
 *
 * @author Kevin
 * @author Robert
 * @author Rutger
 * @since 1.0
 */
class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback, ServiceConnection {
    /** Binding for this activity */
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    /** [Smarthome] service binder */
    private var service: Smarthome? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Request required permissions
        requestPermissions(arrayOf(INTERNET, VIBRATE), 0)
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

        // Bind to Smarthome service
        Intent(this, Smarthome::class.java).also { intent ->
            bindService(intent, this@MainActivity, Context.BIND_AUTO_CREATE)
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
            val dialog = AddArduinoDialog(this, service!!, onSuccess, onError)
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
                val infoDialog = InfoDialog(this@MainActivity, device) {
                    // Remove button view and delete device
                    binding.layout.removeView(this)
                    service?.remove(device)
                }

                // Show dialog
                infoDialog.show()
            }

            text = device.name
        }

        // Add button to layout
        binding.layout.addView(button)
    }

    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        // Save service instance
        service = (binder as Smarthome.Binder).service
        println("Service connected")

        // Make sure the database is loaded
        service?.init() ?: return println("Failed to load database")

        // Load saved arduinos
        val arduinos = service!!.arduinos
        arduinos.forEach(::addArduino)
        println("Loaded ${arduinos.size} Arduino's")
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        println("Service disconnected")
    }
}