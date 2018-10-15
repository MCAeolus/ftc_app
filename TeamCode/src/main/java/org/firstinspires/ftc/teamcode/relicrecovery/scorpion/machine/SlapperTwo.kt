package org.firstinspires.ftc.teamcode.relicrecovery.scorpion.machine

import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.util.LinearOnly

/**
 * Created by Nathan.Smith.19 on 1/11/2018.
 */
@LinearOnly
class SlapperTwo : IMachine {

    lateinit var SERVO_VERTICAL : Servo
    lateinit var SERVO_LR : Servo

    val VERTICAL_OUT = 0.46
    val VERTICAL_REST = -0.6

    val LR_RESTING = 0.5
    val LR_LEFT = 1.0
    val LR_RIGHT = 0.0

    val timer = ElapsedTime()

    override fun init(robot: IRobot) {
        SERVO_VERTICAL = robot.opMode().hardwareMap.get(Servo::class.java, "S2_VERT")
        SERVO_LR = robot.opMode().hardwareMap.get(Servo::class.java, "S2_LR")

    }

    fun right() {
        SERVO_LR.position = LR_RIGHT
    }

    fun left() {
        SERVO_LR.position = LR_LEFT
    }

    fun center() {
        SERVO_LR.position = LR_RESTING
    }

    fun resting() {
        left()
        hold(100)
        SERVO_VERTICAL.position = VERTICAL_REST
    }

    fun out() {
        SERVO_VERTICAL.position = VERTICAL_OUT
        hold(100)
        center()
    }

    override fun stop() {
        resting()
    }

    fun hold(millis : Long){
        timer.reset()
        while(timer.milliseconds() < millis){}
    }
}