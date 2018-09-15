package com.traviswyatt.kapable.processor

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

private const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"

class Processor : AbstractProcessor() {

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        return true
    }
}
