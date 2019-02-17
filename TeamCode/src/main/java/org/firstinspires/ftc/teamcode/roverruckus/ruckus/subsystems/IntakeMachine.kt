package org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems

import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.util.Trackable
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.HNAMES_RUCKUS

class IntakeMachine : IMachine, Trackable {

    enum class IntakeDirection(val speed : Double) {
        INTAKE(0.8), OUTTAKE(-0.6), OFF(0.0)
    }

    enum class ArmDirection(val speed : Double) {
        OUT(1.0), IN(-1.0), OFF(0.0)
    }

    lateinit var armMotor : DcMotor

    override fun init(robot: IRobot) {
        armMotor = robot.opMode().hardwareMap.get(DcMotor::class.java, HNAMES_RUCKUS.INTAKE_ARM_MOTOR)
        armMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }

    fun runIntake(dir : IntakeDirection) {
        //intakerMotor.power = dir.speed
    }

    fun runArm(dir : ArmDirection) {
        armMotor.power = dir.speed
    }

    override fun stop() {
       runIntake(IntakeDirection.OFF)
       runArm(ArmDirection.OFF)
    }

    override fun data(): Map<String, Any> {
       return linkedMapOf("Arm Power" to armMotor.power)
    }
}