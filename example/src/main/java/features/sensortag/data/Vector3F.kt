package com.traviswyatt.sensortag.features.sensortag.data

data class Vector3F(val x: Float, val y: Float, val z: Float)

fun Vector3F.scale(scale: Float) = Vector3F(x * scale, y * scale, z * scale)
