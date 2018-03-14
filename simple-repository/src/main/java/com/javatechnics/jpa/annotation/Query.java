package com.javatechnics.jpa.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Query
{
    String value();

    boolean nativeQuery() default false;
}
