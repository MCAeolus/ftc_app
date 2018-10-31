package org.firstinspires.ftc.teamcode.relicrecovery.argus.machine

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine

/**
 * Created by Nathan.Smith.19 on 1/11/2018.
 */

class JewelSlapper : IMachine {

    lateinit var SERVO_VERTICAL : Servo
    lateinit var SERVO_LR : Servo

    val VERTICAL_OUT = .95
    val VERTICAL_REST = .54

    val LR_RESTING = 0.5
    val LR_LEFT = 1.0
    val LR_RIGHT = 0.0

    val timer = ElapsedTime()

    override fun init(robot: IRobot) {
        SERVO_VERTICAL = robot.opMode().hardwareMap.get(Servo::class.java, "JSV")
        SERVO_LR = robot.opMode().hardwareMap.get(Servo::class.java, "JSH")

        if(robot.opMode() is LinearOpMode)
            resting()
        else resting_nonlinear()
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

    fun resting_nonlinear() {
        left()
        SERVO_VERTICAL.position = VERTICAL_REST
    }

    fun resting() {
        out()
        SERVO_VERTICAL.position = VERTICAL_REST
        hold(600)
        left()
    }

    fun out() {
        center()
        hold(300)
        SERVO_VERTICAL.position = VERTICAL_OUT

    }

    override fun stop() {
        resting()
    }

    fun hold(millis : Long){
        timer.reset()
        while(timer.milliseconds() < millis){}
    }

}