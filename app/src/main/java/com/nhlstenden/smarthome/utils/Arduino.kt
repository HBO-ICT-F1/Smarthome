package com.nhlstenden.smarthome.utils

import com.nhlstenden.smarthome.connection.DTH

/**
 * Arduino data class for storing in the database, and for connecting to arduino's
 *
 * @param name The name of the remote device
 * @param ip The ip of the Arduino
 * @param port The port to connect to for connections
 * @param code The arduino's public key
 * @param dth The data currently received from the [DTH] sensor
 * @param alarm The current state of the alarm
 * @author Robert
 * @since 1.0
 */
data class Arduino(val name: String, val ip: String, val port: Int, val code: Int, var dth: DTH?, var alarm: Boolean?)
