package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.opmodes

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.common.controller.SmidaGamepad
import org.firstinspires.ftc.teamcode.common.util.math.Pose2d
import org.firstinspires.ftc.teamcode.common.util.math.Vector2d
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.RobotInstance
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems.IntakeSystem
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems.OuttakeSystem

@TeleOp(name = "New Teleop")
open class TeleOp : OpMode() {

    lateinit var robot : RobotInstance
        private set


    lateinit var pad1 : SmidaGamepad
    lateinit var pad2 : SmidaGamepad

    private val button = SmidaGamepad.getReflectButton()

    private var isIntakeControlled = false

    override fun init() {
        robot = RobotInstance(this)
        robot.start()

        pad1 = SmidaGamepad(gamepad1, this)
        pad2 = SmidaGamepad(gamepad2, this)
    }

    override fun loop() {

        /**
         * PERFORM GAMEPAD UPDATE
         */

        pad1.handleUpdate()
        pad2.handleUpdate()

        /**
         * CONTROLLER CHECK
         */

        /**
         * DRIVETRAIN
         */

        val lJoystickVals = button(pad1, SmidaGamepad.GamePadButton.LEFT_STICK).joystickValues
        val rJoystickX = button(pad1, SmidaGamepad.GamePadButton.RIGHT_STICK).joystickValues.first
        val gamepadVector = Pose2d(-lJoystickVals.second, lJoystickVals.first, rJoystickX).multiply(3.0) //ramp up quicker

        robot.mecanumDrive.setVelocity(gamepadVector) //drivetrain

        if(button(pad1, SmidaGamepad.GamePadButton.LEFT_STICK).isIndividualActionButtonPress())
            robot.mecanumDrive.inSlowMode = !robot.mecanumDrive.inSlowMode

        /**
         * ROBOT LIFT
         */

        if(button(pad2, SmidaGamepad.GamePadButton.LEFT_BUMPER).isPressed) //the robot lift
            robot.liftSystem.manualLiftPower = 1.0
        else if(button(pad2, SmidaGamepad.GamePadButton.RIGHT_BUMPER).isPressed)
            robot.liftSystem.manualLiftPower = -1.0
        else
            robot.liftSystem.manualLiftPower = 0.0

        /**
         * OUTTAKE SYSTEM
         */

        if(button(pad1, SmidaGamepad.GamePadButton.LEFT_BUMPER).isPressed) //delivery lift
            robot.outtakeSystem.deliveryDirection = OuttakeSystem.DeliveryDirection.UP
        else if(button(pad1, SmidaGamepad.GamePadButton.RIGHT_BUMPER).isPressed)
            robot.outtakeSystem.deliveryDirection = OuttakeSystem.DeliveryDirection.DOWN
        else robot.outtakeSystem.deliveryDirection = OuttakeSystem.DeliveryDirection.STOPPED


        /**
         * INTAKE SYSTEM
         */

        if(button(pad1, SmidaGamepad.GamePadButton.LEFT_TRIGGER).isPressed) //the linear slide
            robot.intakeSystem.linearSlidesPower = pad1.lastCheckedButton.buttonValue
        else if(button(pad1, SmidaGamepad.GamePadButton.RIGHT_TRIGGER).isPressed) {
            robot.intakeSystem.linearSlidesPower = -pad1.lastCheckedButton.buttonValue
            robot.intakeSystem.intakePosition = IntakeSystem.IntakePosition.DOWN
        } else robot.intakeSystem.linearSlidesPower = 0.0

        if(button(pad1, SmidaGamepad.GamePadButton.X).isIndividualActionButtonPress()) {
            robot.intakeSystem.intakePosition = robot.intakeSystem.intakePosition.flip()
            isIntakeControlled = false
        }

        if(robot.outtakeSystem.deliveryDirection != OuttakeSystem.DeliveryDirection.STOPPED && robot.intakeSystem.intakePosition != IntakeSystem.IntakePosition.DOWN) {
            robot.intakeSystem.intakePosition = IntakeSystem.IntakePosition.DOWN
            isIntakeControlled = true
        } else if(robot.outtakeSystem.deliveryDirection == OuttakeSystem.DeliveryDirection.STOPPED && isIntakeControlled) {
            robot.intakeSystem.intakePosition = IntakeSystem.IntakePosition.UP
            isIntakeControlled = false
        }

        if(button(pad2, SmidaGamepad.GamePadButton.LEFT_TRIGGER).isPressed)
            robot.intakeSystem.intakeDirection = IntakeSystem.IntakeDirection.INTAKE
        else if(button(pad2, SmidaGamepad.GamePadButton.RIGHT_TRIGGER).isPressed)
            robot.intakeSystem.intakeDirection = IntakeSystem.IntakeDirection.OUTTAKE
        else robot.intakeSystem.intakeDirection = IntakeSystem.IntakeDirection.STOPPED


        /**
         * POST-CONTROLLER CHECK
         */

        robot.update()
    }

    override fun stop() = robot.stop()

}