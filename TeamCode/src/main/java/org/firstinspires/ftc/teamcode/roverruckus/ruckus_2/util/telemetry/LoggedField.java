package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.util.telemetry;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import kotlin.annotation.AnnotationTarget;
import kotlin.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(allowedTargets = AnnotationTarget.PROPERTY)
public @interface LoggedField {
    String description() default "";
}
