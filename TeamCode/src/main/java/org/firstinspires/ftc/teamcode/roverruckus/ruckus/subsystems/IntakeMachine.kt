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
        INTAKE(0.8), OUTTAKE(-0.8), OFF(0.0)
    }

    enum class ArmDirection(val speed : Double) {
        OUT(1.0), IN(-1.0), OFF(0.0)
    }

    enum class ArmPosition(val enc : Int) {
        UP(0), DOWN(-220)
    }

    lateinit var armMotor : DcMotor
    lateinit var linearSlide : DcMotor
    lateinit var intakerMotor : CRServo

    var armPosition = IntakeMachine.ArmPosition.UP

    override fun init(robot: IRobot) {
        armMotor = robot.opMode().hardwareMap.get(DcMotor::class.java, HNAMES_RUCKUS.INTAKE_ARM_MOTOR)
        intakerMotor = robot.opMode().hardwareMap.get(CRServo::class.java, HNAMES_RUCKUS.INTAKER_MOTOR)
        linearSlide = robot.opMode().hardwareMap.get(DcMotor::class.java, HNAMES_RUCKUS.LINEAR_SLIDES)
        armMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        armPosition(armPosition)
    }

    fun runIntake(dir : IntakeDirection) {
        intakerMotor.power = dir.speed
    }

    fun runArm(dir : ArmDirection) {
        armMotor.power = dir.speed
    }

    fun armPosition(pos : ArmPosition) {
        armMotor.targetPosition = pos.enc
        armMotor.power = 1.0
        armMotor.mode = DcMotor.RunMode.RUN_TO_POSITION
        armPosition = pos
    }

    fun toggleArmPosition() {
        armPosition = if(armPosition == ArmPosition.UP) ArmPosition.DOWN else ArmPosition.UP
        armPosition(armPosition)
    }

    fun doArmPositionToggleNew() {
        armPosition = if(armPosition == ArmPosition.UP) ArmPosition.DOWN else ArmPosition.UP
        //TODO
    }

    fun runSlides(dir : ArmDirection) {
        linearSlide.power = dir.speed
    }

    override fun stop() {
       runIntake(IntakeDirection.OFF)
       runArm(ArmDirection.OFF)
    }

    override fun data(): Map<String, Any> {
       return linkedMapOf("Arm Power" to armMotor.power, "Intake Position" to armMotor.currentPosition)
    }
}