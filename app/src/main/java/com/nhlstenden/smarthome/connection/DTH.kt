package com.nhlstenden.smarthome.connection

/**
 * Arduino data class for storing in the database, and for connecting to arduino's
 *
 * @param temperature Temperature
 * @param humidity Humidity
 * @param ready If device is ready
 * @author Rutger
 * @since 1.0
 */
data class DTH(val temperature: String, val humidity: String, val ready: Boolean)
