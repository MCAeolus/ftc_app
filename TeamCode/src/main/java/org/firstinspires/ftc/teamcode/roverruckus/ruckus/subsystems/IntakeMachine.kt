package org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems

import com.qualcomm.robotcore.hardware.CRServo
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.util.Trackable
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.HNAMES_RUCKUS

class IntakeMachine : IMachine, Trackable {

    enum class Direction(val speed : Double) {
        INTAKE(0.5), OUTTAKE(-0.5), OFF(0.0)
    }

    lateinit var intakerMotor : CRServo

    override fun init(robot: IRobot) {
        intakerMotor = robot.opMode().hardwareMap.get(CRServo::class.java, HNAMES_RUCKUS.VEX_INTAKE)
    }

    fun runIntake(dir : Direction) {
        intakerMotor.power = dir.speed
    }

    override fun stop() {
       runIntake(Direction.OFF)
    }

    override fun data(): Map<String, Any> {
       return linkedMapOf("Intaker Power" to intakerMotor.power)
    }
}