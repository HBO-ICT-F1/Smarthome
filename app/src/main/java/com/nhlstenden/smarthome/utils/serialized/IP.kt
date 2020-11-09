package com.nhlstenden.smarthome.utils.serialized

import com.google.gson.annotations.SerializedName

/**
 * Used to deserialize the response from <a href="https://www.my-ip.io/ip.json">www.my-ip.io/ip.json</a>
 *
 * @author Robert
 * @since 1.0
 */
data class IP(@SerializedName("ip") val address: String)