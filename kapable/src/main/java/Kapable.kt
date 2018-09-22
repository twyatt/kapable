package com.traviswyatt.kapable

import com.juul.able.experimental.Gatt

object Kapable {

    inline fun <reified T> service(gatt: Gatt): T = service(T::class.java, gatt)

    fun <T> service(clazz: Class<T>, gatt: Gatt): T {
        TODO()
    }
}
