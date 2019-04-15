package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.opmodes

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo
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
    lateinit var outtakeMotor : DcMotor
    lateinit var outtakeServo : Servo


    private val servoDelta = 0.05
    private var servoPos = OuttakeSystem.DumpPosition.UP.pos

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

        outtakeMotor = hardwareMap.get(DcMotor::class.java, HNAMES_RUCKUS.OUTTAKE_DELIVERY_SLIDE)
        outtakeMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        outtakeServo = hardwareMap.get(Servo::class.java, HNAMES_RUCKUS.OUTTAKE_SERVO)
        //outtakeServo.position = OuttakeSystem.DumpPosition.UP.pos

        pad1 = SmidaGamepad(gamepad1, this)
        pad2 = SmidaGamepad(gamepad2, this)
    }

    override fun loop() {

        /**
         * PERFORM GAMEPAD UPDATE
         */

        pad1.handleUpdate()
        pad2.handleUpdate()


        when {
            button(pad1, SmidaGamepad.GamePadButton.LEFT_TRIGGER).isPressed -> wormMotor.power = pad1.lastCheckedButton.buttonValue
            button(pad1, SmidaGamepad.GamePadButton.RIGHT_TRIGGER).isPressed -> wormMotor.power = -pad1.lastCheckedButton.buttonValue
            else -> wormMotor.power = 0.0
        }

        when {
            button(pad1, SmidaGamepad.GamePadButton.LEFT_BUMPER).isPressed -> linearSlides.power = 1.0
            button(pad1, SmidaGamepad.GamePadButton.RIGHT_BUMPER).isPressed -> linearSlides.power = -1.0
            else -> linearSlides.power = 0.0
        }

        when {
            button(pad1, SmidaGamepad.GamePadButton.X).isPressed -> outtakeMotor.power = OuttakeSystem.DeliveryDirection.UP.speed
            button(pad1, SmidaGamepad.GamePadButton.B).isPressed -> outtakeMotor.power = OuttakeSystem.DeliveryDirection.DOWN.speed
            else -> outtakeMotor.power = 0.0
        }

        when {
            button(pad1, SmidaGamepad.GamePadButton.PAD_UP).isIndividualActionButtonPress() -> {
                servoPos = if(servoPos <= (1 - servoDelta)) servoPos + servoDelta else 1.0
            }
            button(pad1, SmidaGamepad.GamePadButton.PAD_DOWN).isIndividualActionButtonPress() -> {
                servoPos = if(servoPos >= servoDelta) servoPos - servoDelta else 0.0
            }
        }

        outtakeServo.position = servoPos

        telemetry.addData("servo posiion", outtakeServo.position)
        telemetry.addData("worm position", wormMotor.currentPosition)
        telemetry.addData("slides position", linearSlides.currentPosition)

        /**
         * POST-CONTROLLER CHECK
         */

        //robot.update()
    }

    //override fun stop() = robot.stop()

}