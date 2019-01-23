package org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.util.Trackable
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.HNAMES_RUCKUS

class LinearSlideMachine : IMachine, Trackable {

    lateinit var slideMotorL : DcMotor
    lateinit var slideMotorR : DcMotor

    override fun init(robot: IRobot) {
        slideMotorL = robot.opMode().hardwareMap.get(DcMotor::class.java, HNAMES_RUCKUS.LEFT_SLIDE_MOTOR)
        slideMotorR = robot.opMode().hardwareMap.get(DcMotor::class.java, HNAMES_RUCKUS.RIGHT_SLIDE_MOTOR)

        slideMotorL.direction = DcMotorSimple.Direction.REVERSE
        slideMotorR.direction = DcMotorSimple.Direction.REVERSE

        slideMotorL.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        slideMotorR.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }

    fun runSlides(p : Float) {
        slideMotorL.power = p.toDouble()
        slideMotorR.power = p.toDouble()
    }

    override fun stop() {
        runSlides(0.0F)
    }

    fun motorMap() : Map<String, DcMotor> {
        return mapOf() //TODO
    }

    override fun data(): Map<String, Any> {
       return linkedMapOf("Left Slide Motor" to slideMotorL.power, "Right Slide Motor" to slideMotorR.power)
    }
}