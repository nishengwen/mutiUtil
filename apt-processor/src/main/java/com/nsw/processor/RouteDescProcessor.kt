package com.nsw.processor

import com.google.auto.service.AutoService
import com.nsw.annotation.RouteDesc
import com.squareup.kotlinpoet.FileSpec
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class RouteDescProcessor : AbstractProcessor() {
    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        val file = FileSpec.builder("", "HelloWorld")




        return true
    }

    override fun getSupportedAnnotationTypes()= mutableSetOf(RouteDesc::class.java.canonicalName)

    override fun getSupportedSourceVersion()= SourceVersion.latestSupported()

}