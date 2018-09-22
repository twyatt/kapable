package com.traviswyatt.sensortag.features.sensortag.services

import com.traviswyatt.kapable.annotations.NotificationCharacteristic
import com.traviswyatt.kapable.annotations.Service
import com.traviswyatt.kapable.annotations.WriteCharacteristic
import kotlinx.coroutines.channels.ReceiveChannel

private const val MOVEMENT_SERVICE_UUID = "F000AA80-0451-4000-B000-000000000000"
private const val MOVEMENT_DATA_UUID = "F000AA81-0451-4000-B000-000000000000"
private const val MOVEMENT_CONFIGURATION_UUID = "F000AA82-0451-4000-B000-000000000000"

@Service(MOVEMENT_SERVICE_UUID)
interface Movement {

    @WriteCharacteristic(MOVEMENT_CONFIGURATION_UUID)
    suspend fun writeConfiguration(value: ByteArray)

    @NotificationCharacteristic(MOVEMENT_DATA_UUID)
    fun data(): ReceiveChannel<ByteArray>

    companion object {
        val ENABLE = byteArrayOf(0x7F, 0x00)
        val DISABLE = byteArrayOf(0x7F, 0x00) // FIXME Is 0x00 correct?
    }
}
