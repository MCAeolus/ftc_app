package org.firstinspires.ftc.teamcode.common.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;
import java.util.function.Supplier;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TelemetryField {
    String data() default "";
}
