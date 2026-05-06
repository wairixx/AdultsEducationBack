package com.wairixx.AdultsEducation.aspect;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
    boolean logArgs() default true;
    boolean logResult() default false;
}