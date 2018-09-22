package com.traviswyatt.sensortag.features.scan

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.traviswyatt.sensortag.R
import com.traviswyatt.sensortag.features.sensortag.SensorTagActivity
import kotlinx.android.synthetic.main.scan.scan_list
import timber.log.Timber

class ScanActivity : AppCompatActivity() {

    private lateinit var adapter: ScanAdapter
    private lateinit var viewModel: ScanViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scan)

        scan_list.layoutManager = LinearLayoutManager(this)
        val listener = { sensorTag: SensorTag ->
            startActivity(SensorTagActivity.createIntent(this, sensorTag.device))
        }
        adapter = ScanAdapter(listener).also {
            scan_list.adapter = it
        }

        viewModel = ViewModelProviders.of(this)[ScanViewModel::class.java]
        viewModel.sensorTagsLiveData.observe(this, Observer { sensorTags ->
            adapter.update(sensorTags)
        })

        viewModel.scanStatusLiveData.observe(this, Observer {
            Timber.d("Scan status: $it")
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.startScan()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopScan()
    }
}
