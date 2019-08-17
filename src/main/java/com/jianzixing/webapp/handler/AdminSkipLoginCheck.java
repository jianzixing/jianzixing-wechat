package com.jianzixing.webapp.handler;

import java.lang.annotation.*;

/**
 * @author yangankang
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdminSkipLoginCheck {
}
