package com.troblecodings.launcher.di

import com.google.inject.BindingAnnotation
import kotlin.annotation.Retention
import kotlin.annotation.Target

@BindingAnnotation
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
annotation class ContentPane
