package com.traviswyatt.sensortag.features.scan

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.bluetooth.BluetoothDevice
import com.traviswyatt.sensortag.features.scan.ScanStatus.*
import com.traviswyatt.sensortag.lifecycle.ScopedAndroidViewModel
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanResult
import no.nordicsemi.android.support.v18.scanner.ScanSettings
import timber.log.Timber
import java.lang.Long.parseLong

private const val SCAN_REPORT_DELAY = 1_000L

sealed class ScanStatus {
    object Started : ScanStatus()
    object Stopped : ScanStatus()
    data class Failed(val errorCode: Int) : ScanStatus()
}

data class SensorTag(
    val device: BluetoothDevice,
    var rssi: Int
) {
    val id: Long by lazy(LazyThreadSafetyMode.NONE) {
        parseLong(device.address.replace(":", ""), 16)
    }
}

class ScanViewModel(application: Application) : ScopedAndroidViewModel(application) {

    private val sensorTags = linkedMapOf<BluetoothDevice, SensorTag>()

    private val settings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
        .setReportDelay(SCAN_REPORT_DELAY)
        .setUseHardwareBatchingIfSupported(false)
        .build()

    private val callback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (result.isSensorTag) {
                sensorTags[result.device] = result.asSensorTag()
                sensorTagsLiveData.postValue(sensorTags.values.toList())
            } else {
                Timber.v("onScanResult NOT: ${result.device.name} ${result.device}")
            }
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            var sensorTagFound = false

            for (result in results) {
                if (result.isSensorTag) {
                    sensorTags[result.device] = result.asSensorTag()
                    sensorTagFound = true
                } else {
                    Timber.v("onBatchScanResults NOT: ${result.device.name} ${result.device}")
                }
            }

            if (sensorTagFound) {
                sensorTagsLiveData.postValue(sensorTags.values.toList())
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Timber.e("onScanFailed: errorCode=$errorCode")
            scanStatusLiveData.postValue(Failed(errorCode))
        }
    }

    private val scanner = BluetoothLeScannerCompat.getScanner()

    val scanStatusLiveData = MutableLiveData<ScanStatus>()
    val sensorTagsLiveData = MutableLiveData<List<SensorTag>>()

    fun startScan() {
        scanner.startScan(null, settings, callback)
        scanStatusLiveData.postValue(Started)
    }

    fun stopScan() {
        scanner.stopScan(callback)
        scanStatusLiveData.postValue(Stopped)
    }
}

private val ScanResult.isSensorTag
    get() =
        device.name?.startsWith("SensorTag") == true ||
        device.name?.startsWith("CC2650 SensorTag") == true

private fun ScanResult.asSensorTag() = SensorTag(device, rssi)
