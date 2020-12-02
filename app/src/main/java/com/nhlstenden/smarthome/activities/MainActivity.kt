package com.nhlstenden.smarthome.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.nhlstenden.smarthome.R
import com.nhlstenden.smarthome.connection.Connection
import com.nhlstenden.smarthome.connection.INTERNET
import com.nhlstenden.smarthome.connection.packets.PacketLogin

const val IP = "192.168.0.179"
const val PORT = 80

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = Button(this)
        button.setOnClickListener(this)

        val layout = findViewById<ConstraintLayout>(R.id.constraintlayout)
        layout.addView(button)

        // Request required permissions
        requestPermissions(arrayOf(INTERNET), 0)
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

    override fun onClick(v: View) {
        val thread = Thread {
            println("Connecting...")
            val connection = Connection.connect(IP, PORT) ?: return@Thread println("Not connected")

            println("Writing")
            val login = PacketLogin("Key go brrrrrrr")
            connection.write(login)

            println("Reading")
            connection.run()
            println("Finished")
        }

        thread.start()
    }
}