package com.nhlstenden.smarthome.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.nhlstenden.smarthome.R
import com.nhlstenden.smarthome.connection.INTERNET

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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