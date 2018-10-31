package org.firstinspires.ftc.teamcode.common.drivetrain.util

import com.qualcomm.robotcore.util.ElapsedTime

class PID(var P : Float, var I : Float, var D : Float) {

    val TIME : ElapsedTime = ElapsedTime()
    var TIME_POST = 0.0
    var ERROR_POST = 0F
    var INTEGRAL = 0F

    var RUN = false

    fun calculate(desired : Float, sensor_value : Float) : Double {
        if(!RUN)start()
        val error = desired - sensor_value
        val time_current = TIME.time()
        INTEGRAL += error
        val result = P * error + I*INTEGRAL + D*((error - ERROR_POST)/(time_current - TIME_POST))

        ERROR_POST = error
        TIME_POST = time_current

        return result
    }

    fun stop() {
        RUN = false
        TIME_POST = 0.0
        ERROR_POST = 0F
        INTEGRAL = 0F
    }

    fun start() {
        TIME.reset()
        RUN = true
    }
}