package org.firstinspires.ftc.teamcode.common.common_machines

import com.qualcomm.robotcore.hardware.DcMotor
import kotlin.math.absoluteValue

data class ControlledMotor(val motor: DcMotor, val target : Int, val pos : Boolean) {

    fun isBusy() : Boolean {
        return if(pos) target > motor.currentPosition else target < motor.currentPosition
    }
}