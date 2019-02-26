package org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.util.Trackable
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.HNAMES_RUCKUS

class LiftMachine : IMachine, Trackable {

    enum class LiftDirection(val speed : Double) {
        UP(-1.0), DOWN(1.0), OFF(0.0)
    }

    enum class LiftPosition(val enc : Int) {
        DOWN(100), UP(0)
    }

    lateinit var liftMotor : DcMotor

    override fun init(robot: IRobot) {
        liftMotor = robot.opMode().hardwareMap.get(DcMotor::class.java, HNAMES_RUCKUS.LIFT_MOTOR)
        liftMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        liftMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
    }

    fun runLift(dir : LiftDirection) {
        liftMotor.power = dir.speed
    }

    fun liftPosition(di : LiftPosition) {
        liftMotor.targetPosition = di.enc
        liftMotor.power = 1.0
        liftMotor.mode = DcMotor.RunMode.RUN_TO_POSITION
    }

    override fun stop() {
        runLift(LiftDirection.OFF)
    }

    override fun data(): Map<String, Any> {
        return linkedMapOf("Lift motor power" to liftMotor.power)
    }
}