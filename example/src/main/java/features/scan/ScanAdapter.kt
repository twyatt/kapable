package com.traviswyatt.sensortag.features.scan

import android.support.annotation.LayoutRes
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.traviswyatt.sensortag.R
import kotlinx.android.synthetic.main.scan_item.view.device_name
import kotlinx.android.synthetic.main.scan_item.view.mac_address
import kotlinx.android.synthetic.main.scan_item.view.rssi

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(sensorTag: SensorTag, listener: (SensorTag) -> Unit) = with (itemView) {
        device_name.text = sensorTag.device.name ?: "<unknown>"
        mac_address.text = sensorTag.device.address
        rssi.text = "${sensorTag.rssi} dBm"

        setOnClickListener { listener(sensorTag) }
    }
}

class ScanAdapter(
    private val listener: (SensorTag) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private val sensorTags = mutableListOf<SensorTag>()

    fun update(newList: List<SensorTag>?) {
        if (newList == null) {
            sensorTags.clear()
            notifyDataSetChanged()
        } else {
            val result = DiffUtil.calculateDiff(DiffCallback(sensorTags, newList), false)
            sensorTags.clear()
            sensorTags.addAll(newList)
            result.dispatchUpdatesTo(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent.inflate(R.layout.scan_item))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(sensorTags[position], listener)

    override fun getItemCount(): Int = sensorTags.size

    override fun getItemId(position: Int): Long = sensorTags[position].id
}

private fun ViewGroup.inflate(@LayoutRes layout: Int, attachToRoot: Boolean = false): View =
    LayoutInflater.from(context).inflate(layout, this, attachToRoot)
