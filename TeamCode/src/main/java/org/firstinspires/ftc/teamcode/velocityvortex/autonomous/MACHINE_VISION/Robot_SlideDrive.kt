package org.firstinspires.ftc.teamcode.velocityvortex.autonomous.MACHINE_VISION

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple

/**
 * Created by Nathan.Smith.19 on 9/12/2017.
 */
class Robot_SlideDrive {

    lateinit var centerMotorL : DcMotor
        private set
    lateinit var centerMotorR : DcMotor
    lateinit var motorL : DcMotor
        private set
    lateinit var motorR : DcMotor
        private set

    var centerPower = 0.0
    var sidePower = 0.0
    var rotation = 0.0

    fun grabMotors(mode : OpMode) {
        centerMotorL =  mode.hardwareMap.dcMotor["CML"]
        centerMotorR = mode.hardwareMap.dcMotor["RML"]
        motorL = mode.hardwareMap.dcMotor["LM"]
        motorR = mode.hardwareMap.dcMotor["RM"]

        centerMotorL.direction = DcMotorSimple.Direction.REVERSE
        motorL.direction = DcMotorSimple.Direction.REVERSE
    }
}