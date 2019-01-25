package org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems

import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.util.Trackable
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.HNAMES_RUCKUS

class IntakeMachine : IMachine, Trackable {

    enum class IntakeDirection(val speed : Double) {
        INTAKE(0.5), OUTTAKE(-0.5), OFF(0.0)
    }

    enum class ArmDirection(val speed : Double) {
        UP(0.5), DOWN(-0.5), OFF(0.0)
    }

    lateinit var intakerMotor : CRServo
    lateinit var armMotor : DcMotor

    override fun init(robot: IRobot) {
        intakerMotor = robot.opMode().hardwareMap.get(CRServo::class.java, HNAMES_RUCKUS.VEX_INTAKE)
        armMotor = robot.opMode().hardwareMap.get(DcMotor::class.java, HNAMES_RUCKUS.DC_ARM)
        armMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }

    fun runIntake(dir : IntakeDirection) {
        intakerMotor.power = dir.speed
    }

    fun runArm(dir : ArmDirection) {
        armMotor.power = dir.speed
    }

    override fun stop() {
       runIntake(IntakeDirection.OFF)
    }

    override fun data(): Map<String, Any> {
       return linkedMapOf("Intake Power" to intakerMotor.power, "Arm Power" to armMotor.power)
    }
}