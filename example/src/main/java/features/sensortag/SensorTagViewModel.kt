package com.traviswyatt.sensortag.features.sensortag

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.bluetooth.BluetoothDevice
import com.juul.able.experimental.ConnectGattResult
import com.juul.able.experimental.ConnectGattResult.Canceled
import com.juul.able.experimental.ConnectGattResult.Failure
import com.juul.able.experimental.ConnectGattResult.Success
import com.juul.able.experimental.Gatt
import com.juul.able.experimental.android.connectGatt
import com.traviswyatt.sensortag.features.sensortag.AccelerometerRange.*
import com.traviswyatt.sensortag.features.sensortag.data.Vector3
import com.traviswyatt.sensortag.features.sensortag.data.Vector3F
import com.traviswyatt.sensortag.features.sensortag.data.asFloat
import com.traviswyatt.sensortag.features.sensortag.data.scale
import com.traviswyatt.sensortag.features.sensortag.services.Movement
import com.traviswyatt.sensortag.features.sensortag.services.MovementImpl
import com.traviswyatt.sensortag.lifecycle.ScopedAndroidViewModel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.map
import kotlinx.coroutines.launch
import timber.log.Timber

class SensorTagViewModel(application: Application) : ScopedAndroidViewModel(application) {

    val connectionStatusLiveData = MutableLiveData<ConnectGattResult>()
    val movementLiveData = MutableLiveData<Vector3F>()

    fun connect(bluetoothDevice: BluetoothDevice) = launch {
        val result = bluetoothDevice.connectGatt(getApplication(), autoConnect = false)
        connectionStatusLiveData.postValue(result)

        when (result) {
            is Success -> result.gatt
            is Canceled -> {
                Timber.e(result.cause, "Connection to ${bluetoothDevice.address} canceled.")
                return@launch
            }
            is Failure -> {
                Timber.e(result.cause, "Connection to ${bluetoothDevice.address} failed.")
                return@launch
            }
        }.use { gatt ->
            gatt.discoverServices()
            gatt.movement().consumeEach { movement ->
                movementLiveData.postValue(movement)
            }
        }
    }

    private suspend fun Gatt.movement(): ReceiveChannel<Vector3F> {
//        val movement = Kapable.service<Movement>(this@movement)
        val movement = MovementImpl(this)
        movement.writeConfiguration(Movement.ENABLE)
        return movement.data().map { it.readAccelerometer(RANGE_2G) }
    }
}

private fun ByteArray.readRawVector3(offset: Int): Vector3 {
    val x = (get(offset + 1).toInt() shl 8) + get(offset)
    val y = (get(offset + 3).toInt() shl 8) + get(offset + 2)
    val z = (get(offset + 5).toInt() shl 8) + get(offset + 4)
    return Vector3(x, y, z)
}

enum class AccelerometerRange(val scale: Float) {
    RANGE_2G(2 / 32768f),
    RANGE_4G(4 / 32768f),
    RANGE_8G(8 / 32768f),
    RANGE_16G(16 / 32768f)
}

private const val GYROSCOPE_SCALE = 500 / 65536f

private const val GYROSCOPE_DATA_BYTE_OFFSET = 0
private const val ACCELEROMETER_DATA_BYTE_OFFSET = 6
private const val MAGNETOMETER_DATA_BYTE_OFFSET = 12

private fun ByteArray.readGyroscope(): Vector3F =
    readRawVector3(GYROSCOPE_DATA_BYTE_OFFSET).scale(GYROSCOPE_SCALE)

private fun ByteArray.readAccelerometer(
    range: AccelerometerRange = RANGE_8G
): Vector3F = readRawVector3(ACCELEROMETER_DATA_BYTE_OFFSET).scale(range.scale)

private fun ByteArray.readMagnetometer(): Vector3F =
    readRawVector3(MAGNETOMETER_DATA_BYTE_OFFSET).asFloat()
