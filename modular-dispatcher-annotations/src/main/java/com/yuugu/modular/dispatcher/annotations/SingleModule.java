package com.yuugu.modular.dispatcher.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE})
public @interface SingleModule {

    /**
     * name of current module,
     * must not be null or empty
     */
    String value();
}
