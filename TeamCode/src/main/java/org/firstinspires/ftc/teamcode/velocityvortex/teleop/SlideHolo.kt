package org.firstinspires.ftc.teamcode.velocityvortex.teleop

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple

/**
 * Created by Nathan.Smith.19 on 9/2/2017.
 */
@Disabled
@TeleOp(name="SlideHolo",group="default")
class SlideHolo : OpMode() {

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

    override fun init() {
        centerMotorL = hardwareMap.dcMotor["CML"]
        centerMotorR = hardwareMap.dcMotor["RML"]
        motorL = hardwareMap.dcMotor["LM"]
        motorR = hardwareMap.dcMotor["RM"]

        centerMotorL.direction = DcMotorSimple.Direction.REVERSE
        motorL.direction = DcMotorSimple.Direction.REVERSE
    }

    override fun loop() {
        grabControllerValues()
        updateMotors()
    }

    fun grabControllerValues() {
        centerPower = gamepad1.left_stick_x.toDouble()
        sidePower = gamepad1.left_stick_y.toDouble()

        rotation = gamepad1.right_stick_x.toDouble()
    }

    fun updateMotors() {
        centerMotorL.power = centerPower + rotation
        centerMotorR.power = centerPower + rotation
        motorR.power = sidePower
        motorL.power = sidePower
    }

}