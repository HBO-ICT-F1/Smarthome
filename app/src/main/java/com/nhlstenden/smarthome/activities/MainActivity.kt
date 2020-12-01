package com.nhlstenden.smarthome.activities

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.google.gson.GsonBuilder
import com.nhlstenden.smarthome.R
import com.nhlstenden.smarthome.utils.Connection
import com.nhlstenden.smarthome.utils.INTERNET
import java.net.Socket

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {
    val gson = GsonBuilder().setPrettyPrinting().setLenient().create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = Button(this)
        button.setOnClickListener {
            val thread = Thread {
                println("Connecting...")
                val sock = Socket("192.168.0.100", 80)
                val serialized = Connection("192.168.0.100", 80)

                println("Writing")
                sock.getOutputStream().write(gson.toJson(serialized).toByteArray())
                sock.getOutputStream().flush()
                sock.close()
                println("Finished")
            }

            thread.start()
        }

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
}