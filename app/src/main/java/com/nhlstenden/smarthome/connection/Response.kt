package com.nhlstenden.smarthome.connection

import com.google.gson.annotations.SerializedName
import com.nhlstenden.smarthome.utils.Arduino

/**
 * Response received from an [Arduino] when sending a normal request
 *
 * @author Robert
 * @since 1.0
 */
data class Response(val dth: DTH, @SerializedName("buzzer") val alarm: Int)