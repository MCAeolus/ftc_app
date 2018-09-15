package org.firstinspires.ftc.teamcode.common.drivetrain

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.common.robot.IRobot

/**
 * Created by Nathan.Smith.19 on 10/18/2017.
 */
class MecanumDrive(val motorsNames : Array<String>) : IDriveTrain { //Diamond-Based Mecanum Drive

    lateinit var FL_MOTOR : DcMotor
    lateinit var FR_MOTOR : DcMotor
    lateinit var BL_MOTOR : DcMotor
    lateinit var BR_MOTOR : DcMotor

    lateinit var F_SIDE : Array<DcMotor>
    lateinit var G_SIDE : Array<DcMotor>

    lateinit var ROBOT : IRobot

    override fun init(robot : IRobot) {
        ROBOT = robot
        FL_MOTOR = robot.opMode().hardwareMap.get(DcMotor::class.java, motorsNames[0])
        FR_MOTOR = robot.opMode().hardwareMap.get(DcMotor::class.java, motorsNames[1])
        BL_MOTOR = robot.opMode().hardwareMap.get(DcMotor::class.java, motorsNames[2])
        BR_MOTOR = robot.opMode().hardwareMap.get(DcMotor::class.java, motorsNames[3])

        FL_MOTOR.direction = DcMotorSimple.Direction.REVERSE
        BL_MOTOR.direction = DcMotorSimple.Direction.REVERSE

        FL_MOTOR.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        FR_MOTOR.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        BL_MOTOR.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        BR_MOTOR.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        FL_MOTOR.mode = DcMotor.RunMode.RUN_USING_ENCODER
        FR_MOTOR.mode = DcMotor.RunMode.RUN_USING_ENCODER
        BL_MOTOR.mode = DcMotor.RunMode.RUN_USING_ENCODER
        BR_MOTOR.mode = DcMotor.RunMode.RUN_USING_ENCODER

        F_SIDE = arrayOf(FR_MOTOR, BL_MOTOR)
        G_SIDE = arrayOf(FL_MOTOR, BR_MOTOR)
    }

    override fun stop() {
        powerSet(0.0, 0.0, 0.0, 0.0)
    }

    override fun move(x: Double, y: Double, r: Double, p: Double) {
        val pwr = Math.hypot(x, y)
        val angle = Math.atan2(y, x) - (Math.PI / 4)

        val FL = pwr * Math.cos(angle) + r
        val FR = pwr * Math.sin(angle) - r
        val BL = pwr * Math.sin(angle) + r
        val BR = pwr * Math.cos(angle) - r

        powerSet(FL * p, FR * p, BL * p, BR * p)
    }

    override fun driveClass() = this::class

    override fun motorList() = listOf(FL_MOTOR, FR_MOTOR, BL_MOTOR, BR_MOTOR)

    override fun motorMap() = mapOf(Pair("FL", FL_MOTOR), Pair("FR", FR_MOTOR), Pair("BL", BL_MOTOR), Pair("BR", BR_MOTOR))

    fun powerSet(fl : Double, fr: Double, bl : Double, br : Double) {
        FL_MOTOR.power = fl
        FR_MOTOR.power = fr
        BL_MOTOR.power = bl
        BR_MOTOR.power = br
    }
}