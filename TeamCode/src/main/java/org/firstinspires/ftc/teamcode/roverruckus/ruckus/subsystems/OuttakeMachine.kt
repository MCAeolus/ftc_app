package org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.util.Trackable
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.HNAMES_RUCKUS

class OuttakeMachine : IMachine, Trackable {

    lateinit var liftingArm : DcMotor
    lateinit var dumpServo : Servo

    enum class DumpPosition(val pos : Double) {
        DUMP(1.0), RESET(0.0)
    }

    override fun init(robot: IRobot) {
        liftingArm = robot.opMode().hardwareMap.get(DcMotor::class.java, HNAMES_RUCKUS.OUTTAKE_ARM_MOTOR)
        dumpServo = robot.opMode().hardwareMap.get(Servo::class.java, HNAMES_RUCKUS.OUTTAKE_SERVO)

        liftingArm.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }

    fun runSlides(p : Float) {
        liftingArm.power = p.toDouble()
    }

    fun setDumpPosition(d : DumpPosition) {
        dumpServo.position = d.pos
    }

    override fun stop() {
        runSlides(0.0F)
    }

    override fun data(): Map<String, Any> {
       return linkedMapOf("Lifting Motor" to liftingArm.power)
    }
}