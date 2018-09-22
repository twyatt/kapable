package com.traviswyatt.sensortag.features.scan

import android.support.v7.util.DiffUtil

class DiffCallback(
    private val oldSensorTags: List<SensorTag>,
    private val newSensorTags: List<SensorTag>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldSensorTags.size

    override fun getNewListSize(): Int = newSensorTags.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldSensorTags[oldItemPosition].id == newSensorTags[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldSensorTags[oldItemPosition].rssi == newSensorTags[newItemPosition].rssi

}
