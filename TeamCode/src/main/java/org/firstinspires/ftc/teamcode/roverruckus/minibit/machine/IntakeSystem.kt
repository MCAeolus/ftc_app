package org.firstinspires.ftc.teamcode.roverruckus.minibit.machine

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.util.Trackable
import org.firstinspires.ftc.teamcode.roverruckus.minibit.HARDWARENAMES_MINIBOT

class IntakeSystem : IMachine, Trackable {

    lateinit var intake_motorL : DcMotor
    lateinit var intake_motorR : DcMotor

    override fun init(robot: IRobot) {
        intake_motorL = robot.opMode().hardwareMap.get(DcMotor::class.java, HARDWARENAMES_MINIBOT.INTAKE_MOTOR_LEFT.v)
        intake_motorR = robot.opMode().hardwareMap.get(DcMotor::class.java, HARDWARENAMES_MINIBOT.INTAKE_MOTOR_RIGHT.v)

        intake_motorL.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        intake_motorR.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        intake_motorR.direction = DcMotorSimple.Direction.REVERSE
    }

    fun runMotors(forwards : Boolean = true) {
        val power = if(forwards)0.6 else -0.6

        intake_motorL.power = power
        intake_motorR.power = power
    }

    override fun stop() {
        intake_motorL.power = 0.0
        intake_motorR.power = 0.0
    }

    override fun data(): Map<String, Any> {
        return linkedMapOf()
    }
}