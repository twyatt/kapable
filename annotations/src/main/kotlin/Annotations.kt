package com.traviswyatt.kapable.annotations

@Target(AnnotationTarget.CLASS)
annotation class Service(val uuid: String)

@Target(AnnotationTarget.FUNCTION)
annotation class ReadCharacteristic(val uuid: String)

@Target(AnnotationTarget.FUNCTION)
annotation class WriteCharacteristic(val uuid: String)

@Target(AnnotationTarget.FUNCTION)
annotation class NotificationCharacteristic(val uuid: String)
