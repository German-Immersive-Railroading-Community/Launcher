package com.troblecodings.launcher.di;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@BindingAnnotation
@Retention(RUNTIME)
@Target({FIELD, PARAMETER, METHOD})
public @interface ContentHost {
}
