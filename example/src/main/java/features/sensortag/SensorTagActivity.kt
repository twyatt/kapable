package com.traviswyatt.sensortag.features.sensortag

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.traviswyatt.sensortag.R
import kotlinx.android.synthetic.main.device.x_axis
import kotlinx.android.synthetic.main.device.y_axis
import kotlinx.android.synthetic.main.device.z_axis
import timber.log.Timber

class SensorTagActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.device)

        val bluetoothDevice = intent.getParcelableExtra<BluetoothDevice>(EXTRA_BLUETOOTH_DEVICE)

        val viewModel = ViewModelProviders.of(this)[SensorTagViewModel::class.java]

        viewModel.connectionStatusLiveData.observe(this, Observer {
            Timber.i("Connection state: $it")
        })

        viewModel.movementLiveData.observe(this, Observer {
            x_axis.text = it?.x?.toString()
            y_axis.text = it?.y?.toString()
            z_axis.text = it?.z?.toString()
        })
        viewModel.connect(bluetoothDevice)
    }

    companion object {
        private const val EXTRA_BLUETOOTH_DEVICE = "com.traviswyatt.sensortag.DEVICE"

        fun createIntent(context: Context, bluetoothDevice: BluetoothDevice) =
            Intent(context, SensorTagActivity::class.java).apply {
                putExtra(EXTRA_BLUETOOTH_DEVICE, bluetoothDevice)
            }
    }
}
