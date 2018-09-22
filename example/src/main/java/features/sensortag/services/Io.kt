package com.traviswyatt.sensortag.features.sensortag.services

import com.traviswyatt.kapable.annotations.ReadCharacteristic
import com.traviswyatt.kapable.annotations.Service
import com.traviswyatt.kapable.annotations.WriteCharacteristic

private const val IO_SERVICE_UUID = "F000AA64-0451-4000-B000-000000000000"
private const val IO_DATA_UUID = "F000AA65-0451-4000-B000-000000000000"
private const val IO_CONFIGURATION_UUID = "F000AA66-0451-4000-B000-000000000000"

@Service(IO_SERVICE_UUID)
interface Io {

    @ReadCharacteristic(IO_DATA_UUID)
    suspend fun readData(): Byte

    @ReadCharacteristic(IO_CONFIGURATION_UUID)
    suspend fun readConfiguration(): Byte

    @WriteCharacteristic(IO_CONFIGURATION_UUID)
    suspend fun writeConfiguration(value: Byte)
}
