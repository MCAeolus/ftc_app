package org.firstinspires.ftc.teamcode.roverruckus.minibit.machine

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.common.drivetrain.IDriveTrain
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.roverruckus.minibit.HARDWARENAMES_MINIBOT
import kotlin.reflect.KClass

class MiniTankDrive() : IDriveTrain {

    lateinit var motorL : DcMotor
    lateinit var motorR : DcMotor

    override fun init(robot: IRobot) {
        motorL = robot.opMode().hardwareMap.get(DcMotor::class.java, HARDWARENAMES_MINIBOT.DRIVE_MOTOR_LEFT.v)
        motorR = robot.opMode().hardwareMap.get(DcMotor::class.java, HARDWARENAMES_MINIBOT.DRIVE_MOTOR_RIGHT.v)

        motorR.direction = DcMotorSimple.Direction.REVERSE

        motorL.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        motorR.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        motorL.mode = DcMotor.RunMode.RUN_USING_ENCODER
        motorR.mode = DcMotor.RunMode.RUN_USING_ENCODER
    }

    /**
     * x = none
     * y = forwards/backwards
     */
    override fun move(x: Double, y: Double, r: Double, p: Double) {
        val left = ((y) * p) + r
        val right = ((y) * p) - r
        powerSet(left, right)
    }

    override fun motorList(): List<DcMotor> = listOf(motorL, motorR)

    override fun motorMap(): Map<String, DcMotor> =
            mapOf(Pair(HARDWARENAMES_MINIBOT.DRIVE_MOTOR_LEFT.v, motorL), Pair(HARDWARENAMES_MINIBOT.DRIVE_MOTOR_RIGHT.v, motorR))

    override fun driveClass(): KClass<out IDriveTrain> = this::class

    override fun stop() = powerSet(0.0, 0.0)

    fun powerSet(left : Double, right : Double) {
        motorL.power = left
        motorR.power = right
    }
}