package com.traviswyatt.sensortag.features.sensortag.services

import android.bluetooth.BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
import com.juul.able.experimental.Gatt
import com.juul.able.experimental.toUuid
import com.juul.able.experimental.writeCharacteristic
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.filter
import kotlinx.coroutines.channels.produce

class MovementImpl(private val gatt: Gatt) : Movement {

    private val serviceUuid = "F000AA80-0451-4000-B000-000000000000".toUuid()

    override suspend fun writeConfiguration(value: ByteArray) {
        val characteristicUuid = "F000AA82-0451-4000-B000-000000000000".toUuid() // todo: from annotation
        val characteristic = gatt.getService(serviceUuid)!!.getCharacteristic(characteristicUuid)
        gatt.writeCharacteristic(characteristic, value)
    }

    override fun data(): ReceiveChannel<ByteArray> = gatt.produce {
        val characteristicUuid = "F000AA81-0451-4000-B000-000000000000".toUuid() // todo: from annotation
        val characteristic = gatt.getService(serviceUuid)!!.getCharacteristic(characteristicUuid)

        invokeOnClose {
            // todo: write descriptor
//            gatt.writeDescriptor(descriptor, DISABLE_NOTIFICATION_VALUE)
            gatt.setCharacteristicNotification(characteristic, false)
        }

        gatt.setCharacteristicNotification(characteristic, true)

        val descriptorUuid = "00002902-0000-1000-8000-00805F9B34FB".toUuid() // todo: from annotation
        val descriptor = characteristic.getDescriptor(descriptorUuid)
        gatt.writeDescriptor(descriptor, ENABLE_NOTIFICATION_VALUE)

        gatt.onCharacteristicChanged
            .openSubscription()
            .filter {
                // todo: Can we perform reference equality check on the characteristic? it.characteristic === characteristic
                it.characteristic.instanceId == characteristic.instanceId &&
                    it.characteristic.uuid == characteristicUuid
            }
            .consumeEach {
                send(it.value)
            }
    }
}
