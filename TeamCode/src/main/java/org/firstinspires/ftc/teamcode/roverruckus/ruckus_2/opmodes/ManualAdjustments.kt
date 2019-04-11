package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.opmodes

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.common.controller.SmidaGamepad
import org.firstinspires.ftc.teamcode.common.util.math.Pose2d
import org.firstinspires.ftc.teamcode.roverruckus.HNAMES_RUCKUS
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.RobotInstance
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems.IntakeSystem
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems.OuttakeSystem

@TeleOp(name = "Manual Adjustments")
open class ManualAdjustments : OpMode() {

    //lateinit var robot : RobotInstance
    //    private set

    lateinit var pad1 : SmidaGamepad
    lateinit var pad2 : SmidaGamepad

    private val button = SmidaGamepad.getReflectButton()

    lateinit var wormMotor : DcMotor
    lateinit var linearSlides : DcMotor

    override fun init() {
        //robot = RobotInstance(this)
        ///robot.start()

        wormMotor = hardwareMap.get(DcMotor::class.java, HNAMES_RUCKUS.INTAKE_ARM_MOTOR)
        wormMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        wormMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        wormMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER

        linearSlides = hardwareMap.get(DcMotor::class.java, HNAMES_RUCKUS.LINEAR_SLIDES)
        linearSlides.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        linearSlides.mode = DcMotor.RunMode.RUN_USING_ENCODER
        linearSlides.direction = DcMotorSimple.Direction.REVERSE

        pad1 = SmidaGamepad(gamepad1, this)
        pad2 = SmidaGamepad(gamepad2, this)
    }

    override fun loop() {

        /**
         * PERFORM GAMEPAD UPDATE
         */

        pad1.handleUpdate()
        pad2.handleUpdate()


        val lJoystickVals = button(pad1, SmidaGamepad.GamePadButton.LEFT_STICK).joystickValues
        val rJoystickX = button(pad1, SmidaGamepad.GamePadButton.RIGHT_STICK).joystickValues.first
        val gamepadVector = Pose2d(-lJoystickVals.second, lJoystickVals.first, rJoystickX).multiply(3.0) //ramp up quicker

        /**
        robot.mecanumDrive.setVelocity(gamepadVector) //drivetrain


        if(button(pad2, SmidaGamepad.GamePadButton.LEFT_BUMPER).isPressed) //the robot lift
            robot.liftSystem.manualLiftPower = 1.0
        else if(button(pad2, SmidaGamepad.GamePadButton.RIGHT_BUMPER).isPressed)
            robot.liftSystem.manualLiftPower = -1.0
        else
            robot.liftSystem.manualLiftPower = 0.0
        **/

        /**
         * INTAKE SYSTEM
         */

        if(button(pad1, SmidaGamepad.GamePadButton.LEFT_TRIGGER).isPressed)
            wormMotor.power = pad1.lastCheckedButton.buttonValue
        else if(button(pad1, SmidaGamepad.GamePadButton.RIGHT_TRIGGER).isPressed)
            wormMotor.power = -pad1.lastCheckedButton.buttonValue
        else wormMotor.power = 0.0

        if(button(pad1, SmidaGamepad.GamePadButton.LEFT_BUMPER).isPressed)
            linearSlides.power = 1.0
        else if(button(pad1, SmidaGamepad.GamePadButton.RIGHT_BUMPER).isPressed)
            linearSlides.power = -1.0
        else linearSlides.power = 0.0


        telemetry.addData("worm position", wormMotor.currentPosition)
        telemetry.addData("slides position", linearSlides.currentPosition)

        /**
         * POST-CONTROLLER CHECK
         */

        //robot.update()
    }

    //override fun stop() = robot.stop()

}