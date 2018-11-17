package org.firstinspires.ftc.teamcode.roverruckus.minibit.machine

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.util.Trackable
import org.firstinspires.ftc.teamcode.roverruckus.minibit.HARDWARENAMES_MINIBOT

class IntakeSystem : IMachine, Trackable {

    lateinit var intake_motor : DcMotor

    override fun init(robot: IRobot) {
        intake_motor = robot.opMode().hardwareMap.get(DcMotor::class.java, HARDWARENAMES_MINIBOT.INTAKE_MOTOR.v)

        intake_motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        intake_motor.direction = DcMotorSimple.Direction.REVERSE
    }

    fun runMotors(forwards : Boolean = true) {
        val power = if(forwards)0.6 else -0.6

        intake_motor.power = power
    }

    override fun stop() {
        intake_motor.power = 0.0
    }

    override fun data(): Map<String, Any> {
        return linkedMapOf()
    }
}