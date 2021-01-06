package com.nhlstenden.smarthome.connection

import com.nhlstenden.smarthome.utils.Arduino

/**
 * Response received from an [Arduino] when trying to pair
 *
 * @author Robert
 * @since 1.0
 */
data class PairResponse(val key: String)