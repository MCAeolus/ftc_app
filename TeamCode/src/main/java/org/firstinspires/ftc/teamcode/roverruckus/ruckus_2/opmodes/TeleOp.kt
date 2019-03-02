package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.opmodes

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.common.controller.SmidaGamepad
import org.firstinspires.ftc.teamcode.common.util.math.Pose2d
import org.firstinspires.ftc.teamcode.common.util.math.Vector2d
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.RobotInstance

@TeleOp(name = "New Teleop")
class TeleOp : OpMode() {

    lateinit var robot : RobotInstance
        private set

    lateinit var pad1 : SmidaGamepad
    lateinit var pad2 : SmidaGamepad

    private val button = SmidaGamepad.getReflectButton()

    override fun init() {
        robot = RobotInstance(this)
        robot.start()

        pad1 = SmidaGamepad(gamepad1, this)
        pad2 = SmidaGamepad(gamepad2, this)
    }

    override fun loop() {
        pad1.handleUpdate()
        pad2.handleUpdate()

        val lJoystickVals = button(pad1, SmidaGamepad.GamePadButton.LEFT_STICK).joystickValues
        val rJoystickX = button(pad1, SmidaGamepad.GamePadButton.RIGHT_STICK).joystickValues.first
        val gamepadVector = Pose2d(-lJoystickVals.second, lJoystickVals.first, rJoystickX).multiply(3.0) //ramp up quicker

        robot.mecanumDrive.setVelocity(gamepadVector)

        robot.update()
    }

    override fun stop() {
        robot.stop()
    }
}