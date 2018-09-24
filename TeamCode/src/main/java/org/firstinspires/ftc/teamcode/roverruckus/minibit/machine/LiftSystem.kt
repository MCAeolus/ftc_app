package org.firstinspires.ftc.teamcode.roverruckus.minibit.machine

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.relicrecovery.scorpion.machine.IMachine
import org.firstinspires.ftc.teamcode.roverruckus.minibit.HARDWARENAMES_MINIBOT

class LiftSystem() : IMachine {

    lateinit var lift_motorL : DcMotor
    lateinit var lift_motorR: DcMotor

    var position_lifted = 0

    override fun init(robot: IRobot) {
        lift_motorL = robot.opMode().hardwareMap.get(DcMotor::class.java, HARDWARENAMES_MINIBOT.LIFT_MOTOR_LEFT.v)
        lift_motorR = robot.opMode().hardwareMap.get(DcMotor::class.java, HARDWARENAMES_MINIBOT.LIFT_MOTOR_RIGHT.v)

        lift_motorL.direction = DcMotorSimple.Direction.REVERSE

        lift_motorL.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        lift_motorR.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        lift_motorL.mode = DcMotor.RunMode.RUN_USING_ENCODER
        lift_motorR.mode = DcMotor.RunMode.RUN_USING_ENCODER
    }

    override fun stop() {
        powerSet(0.0, 0.0)
    }

    fun powerSet(left : Double, right : Double){
        lift_motorL.power = left
        lift_motorR.power = right
    }
}