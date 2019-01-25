package org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.util.Trackable
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.HNAMES_RUCKUS

class LiftMachine : IMachine, Trackable {

    enum class LiftDirection(val speed : Double) {
        UP(0.5), DOWN(-0.5), OFF(0.0)
    }

    lateinit var liftMotor : DcMotor

    override fun init(robot: IRobot) {
        liftMotor = robot.opMode().hardwareMap.get(DcMotor::class.java, HNAMES_RUCKUS.LIFT_MOTOR)
    }

    fun runLift(dir : LiftDirection) {
        liftMotor.power = dir.speed
    }

    override fun stop() {
        runLift(LiftDirection.OFF)
    }

    override fun data(): Map<String, Any> {
        return linkedMapOf("Lift motor power" to liftMotor.power)
    }
}