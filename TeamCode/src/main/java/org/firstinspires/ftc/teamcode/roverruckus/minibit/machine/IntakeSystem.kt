package org.firstinspires.ftc.teamcode.roverruckus.minibit.machine

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.util.Trackable
import org.firstinspires.ftc.teamcode.roverruckus.minibit.HARDWARENAMES_MINIBOT

class IntakeSystem : IMachine, Trackable {

    lateinit var intake_motor : DcMotor

    override fun init(robot: IRobot) {
        intake_motor = robot.opMode().hardwareMap.get(DcMotor::class.java, HARDWARENAMES_MINIBOT.INTAKE_MOTOR.v)
    }

    override fun stop() {

    }

    override fun data(): Map<String, Any> {
        return linkedMapOf()
    }
}