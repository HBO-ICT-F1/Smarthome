package com.nhlstenden.smarthome.connection

import com.nhlstenden.smarthome.utils.Arduino

/**
 * Response received from an [Arduino] when sending a normal request
 *
 * @author Robert
 * @since 1.0
 */
data class Response(val temperature: Float, val humidity: Float, val alarm: Boolean)