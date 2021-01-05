package com.nhlstenden.smarthome.activities

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.nhlstenden.smarthome.R
import com.nhlstenden.smarthome.connection.Connection
import com.nhlstenden.smarthome.connection.INTERNET
import com.nhlstenden.smarthome.connection.PacketType
import com.nhlstenden.smarthome.connection.packets.PacketRoomData

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Request required permissions
        requestPermissions(arrayOf(INTERNET), 0)

        setSupportActionBar(findViewById(R.id.toolbar))

        val linearLayout = findViewById<LinearLayout>(R.id.linear_layout)

        val alertDialog = AlertDialog.Builder(this@MainActivity).create()
        val infoDialog = layoutInflater.inflate(R.layout.info_dialog, null)
        alertDialog.setView(infoDialog)

        for (x in 0..10) {
            val button = Button(this)
            button.apply {
                height = 160
                text = "test"
            }
            button.setOnClickListener {
                alertDialog.show()
            }
            linearLayout.addView(button)
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
        Thread {
            val connection = Connection.connect("185.35.35.20", 6001) ?: return@Thread
            connection.register(PacketType.RoomData) { it: PacketRoomData ->
                println(it.temperature);
            }

            connection.run()
        }.start()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
}