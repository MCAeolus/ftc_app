package org.firstinspires.ftc.teamcode.roverruckus.minibit.machine

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.common.drivetrain.IDriveTrain
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.util.Trackable
import org.firstinspires.ftc.teamcode.roverruckus.minibit.HARDWARENAMES_MINIBOT
import kotlin.reflect.KClass

class MiniTankDrive() : IDriveTrain, Trackable {

    lateinit var fr_motorL : DcMotor
    lateinit var fr_motorR : DcMotor
    lateinit var bk_motorL : DcMotor
    lateinit var bk_motorR : DcMotor

    override fun init(robot: IRobot) {
        fr_motorL = robot.opMode().hardwareMap.get(DcMotor::class.java, HARDWARENAMES_MINIBOT.DRIVE_MOTOR_FRONT_LEFT.v)
        bk_motorL = robot.opMode().hardwareMap.get(DcMotor::class.java, HARDWARENAMES_MINIBOT.DRIVE_MOTOR_BACK_LEFT.v)

        fr_motorR = robot.opMode().hardwareMap.get(DcMotor::class.java, HARDWARENAMES_MINIBOT.DRIVE_MOTOR_FRONT_RIGHT.v)
        bk_motorR = robot.opMode().hardwareMap.get(DcMotor::class.java, HARDWARENAMES_MINIBOT.DRIVE_MOTOR_BACK_RIGHT.v)

        fr_motorL.direction = DcMotorSimple.Direction.REVERSE
        bk_motorL.direction = DcMotorSimple.Direction.REVERSE

        fr_motorL.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        bk_motorL.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        fr_motorR.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        bk_motorR.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        fr_motorL.mode = DcMotor.RunMode.RUN_USING_ENCODER
        bk_motorL.mode = DcMotor.RunMode.RUN_USING_ENCODER

        fr_motorR.mode = DcMotor.RunMode.RUN_USING_ENCODER
        bk_motorR.mode = DcMotor.RunMode.RUN_USING_ENCODER
    }

    /**
     * x = none
     * y = forwards/backwards
     */
    override fun move(x: Double, y: Double, r: Double, p: Double) {
        val left = ((y) * p) - (r * p)
        val right = ((y) * p) + (r * p)
        powerSet(left, right)
    }

    override fun motorList(): List<DcMotor> = listOf(fr_motorL, fr_motorR, bk_motorL, bk_motorR)

    override fun motorMap(): Map<String, DcMotor> =
            mapOf(Pair(HARDWARENAMES_MINIBOT.DRIVE_MOTOR_FRONT_LEFT.v, fr_motorL), Pair(HARDWARENAMES_MINIBOT.DRIVE_MOTOR_FRONT_RIGHT.v, fr_motorR), Pair(HARDWARENAMES_MINIBOT.DRIVE_MOTOR_BACK_LEFT.v, bk_motorL), Pair(HARDWARENAMES_MINIBOT.DRIVE_MOTOR_BACK_RIGHT.v, bk_motorR))

    override fun driveClass(): KClass<out IDriveTrain> = this::class

    override fun stop() = powerSet(0.0, 0.0)

    fun powerSet(left : Double, right : Double) {
        fr_motorL.power = left
        bk_motorL.power = left
        fr_motorR.power = right
        bk_motorR.power = right
    }

    override fun data() : Map<String, Any> {
        return linkedMapOf(
                "Front left motor encoder" to fr_motorL.currentPosition,
                "Front right motor encoder" to fr_motorR.currentPosition,
                "Back left motor encoder" to bk_motorL.currentPosition,
                "Back right motor encoder" to bk_motorR.currentPosition
        )
    }
}