package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.common.controller.Button
import org.firstinspires.ftc.teamcode.common.controller.SmidaGamepad
import org.firstinspires.ftc.teamcode.common.robot.LinearRobot

@TeleOp(name="test that motor")
class ExampleMotorTest : LinearOpMode() {

    override fun runOpMode() {
        val pad1 = SmidaGamepad(gamepad1, this)
        val button : (SmidaGamepad.GamePadButton) -> Button = pad1::getButton

        val motor = hardwareMap.get(DcMotor::class.java, "m")
        val ser = hardwareMap.get(CRServo::class.java, "cr")

        while(!isStopRequested) {
            pad1.handleUpdate()

            if(button(SmidaGamepad.GamePadButton.LEFT_BUMPER).isPressed)
                motor.power = 1.0
            else if(button(SmidaGamepad.GamePadButton.RIGHT_BUMPER).isPressed)
                motor.power = -1.0
            else motor.power = 0.0

            if(button(SmidaGamepad.GamePadButton.LEFT_TRIGGER).isPressed)
                ser.power = 1.0
            else if(button(SmidaGamepad.GamePadButton.RIGHT_TRIGGER).isPressed)
                ser.power = -1.0
            else ser.power = 1.0

        }
    }

}