package com.nhlstenden.smarthome

import android.app.Service
import android.content.Context
import android.content.Intent
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.nhlstenden.smarthome.activities.MainActivity
import com.nhlstenden.smarthome.connection.Connection
import com.nhlstenden.smarthome.utils.Arduino
import com.nhlstenden.smarthome.utils.Database
import com.nhlstenden.smarthome.utils.Notification
import kotlin.concurrent.thread

/** Database file name */
private const val DATABASE_NAME = "Smarthome.db"

/** Query used for creating the devices table */
private const val CREATE_TABLE =
    "CREATE TABLE IF NOT EXISTS `devices`(`name` VARCHAR(255) NOT NULL, `ip` VARCHAR(15) NOT NULL, `port` INT(5) NOT NULL, `code` INT(4) NOT NULL)"

/** Query used for getting all [Arduino]'s from the devices table */
private const val SELECT_DEVICES = "SELECT * FROM `devices`"

/**
 * Smarthome background service class
 *
 * @author Robert
 * @since 1.0
 */
class Smarthome : Service(), Runnable {
    /** Binder used for connecting to [MainActivity] */
    private val binder = Binder()

    /** Is the database loaded */
    private var loaded = false

    /** SQLite database used for storing [Arduino]'s */
    private val database by lazy {
        val database = Database(openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null))
        init()
        database
    }

    /** [Notification] instance for buzzer alerts */
    private val notification by lazy {
        Notification(
            applicationContext,
            "Smarthome",
            "Notification channel for Smarthome alerts"
        )
    }

    /** List containing all loaded [Arduino]'s */
    var arduinos = mutableListOf<Arduino>()
        private set

    /** Set [Service] binder to [Binder] */
    override fun onBind(intent: Intent) = binder

    override fun run() {
        println("Started update thread")

        while (true) {
            // Wait 30 seconds between refreshing all Arduino's
            Thread.sleep(30000)
            println("Updating Arduino's...")

            for (arduino in arduinos) {
                // Send request for updating Arduino data
                Connection.request(arduino, "?code=${arduino.code}") {
                    // Update Arduino data
                    arduino.dth = it.dth
                    arduino.alarm = it.alarm == 1

                    // Show notification
                    if (arduino.alarm == true) {
                        notification.notify(
                            "Alarm set off!",
                            "The alarm on '${arduino.name}' was set off",
                            android.app.Notification.CATEGORY_ALARM
                        )
                    }
                }
            }

            println("Finished updating Arduino's")
        }
    }

    /** Used to save an [Arduino] to the database */
    fun add(arduino: Arduino) {
        // Save Arduino to list
        arduinos.plusAssign(arduino)

        // Save Arduino to database
        database.exec("INSERT INTO `devices` (`name`, `ip`, `port`, `code`) VALUES ('${arduino.name}', '${arduino.ip}', '${arduino.port}', '${arduino.code}')")
    }

    /** Creates tables and loads data in the specified [Database] */
    fun init() {
        // Make sure the application doesnt load multiple times
        if (loaded) return
        loaded = true

        // Start update thread
        thread(true, block = ::run)

        // Create table if it doesnt exist
        database.exec(CREATE_TABLE)

        // Load devices
        database.exec(SELECT_DEVICES) {
            do {
                // Get device data from database
                val name = it.getStringOrNull(0) ?: continue
                val ip = it.getStringOrNull(1) ?: continue
                val port = it.getIntOrNull(2) ?: continue
                val code = it.getIntOrNull(3) ?: continue

                // Create Arduino instance and save to list
                arduinos.plusAssign(Arduino(name, ip, port, code, null, null))
            } while (it.moveToNext())
        }
    }

    /** Binder used for binding [Smarthome] to [MainActivity] */
    inner class Binder : android.os.Binder() {

        /** The [Smarthome] service instance */
        val service get() = this@Smarthome
    }
}
