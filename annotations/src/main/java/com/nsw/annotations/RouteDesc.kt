package com.nsw.annotations

@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class RouteDesc(val content: String)