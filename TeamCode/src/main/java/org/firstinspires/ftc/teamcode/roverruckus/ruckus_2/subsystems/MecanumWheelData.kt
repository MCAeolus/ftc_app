package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.common.util.DummyDcMotor
import org.firstinspires.ftc.teamcode.common.util.math.Vector2d

data class MecanumWheelData(val rollerDirection : Vector2d, val wheelPosition : Vector2d, val motorName : String, private var motor : DcMotor = DummyDcMotor()) {

    fun motor() = motor

    fun setMotor(motor : DcMotor) {
        this.motor = motor
    }
}