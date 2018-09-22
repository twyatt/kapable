package com.traviswyatt.sensortag.features.sensortag.data

data class Vector3(val x: Int, val y: Int, val z: Int)

fun Vector3.asFloat() = Vector3F(x.toFloat(), y.toFloat(), z.toFloat())
fun Vector3.scale(scale: Int) = Vector3(x * scale, y * scale, z * scale)
fun Vector3.scale(scale: Float) = Vector3F(x * scale, y * scale, z * scale)
