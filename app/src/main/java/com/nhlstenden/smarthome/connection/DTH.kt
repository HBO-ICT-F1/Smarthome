package com.nhlstenden.smarthome.connection

/**
 * Arduino data class for storing in the database, and for connecting to arduino's
 *
 * @param temperature Temperature measured by the Arduino
 * @param humidity Humidity measured by the Arduino
 * @param ready Was the DTH sensor ready for measuring data
 * @author Rutger
 * @since 1.0
 */
data class DTH(val temperature: Float, val humidity: Float, val ready: Int)