package com.nhlstenden.smarthome.utils

import com.nhlstenden.smarthome.connection.Response

/**
 * Arduino data class for storing in the database, and for connecting to arduino's
 *
 * @param name The name of the remote device
 * @param ip The ip of the Arduino
 * @param port The port to connect to for connections
 * @param code The arduino's public key
 * @param last The last [Response] received from this device
 * @author Robert
 * @since 1.0
 */
data class Arduino(val name: String, val ip: String, val port: Int, val code: Int, var last: Response?)
