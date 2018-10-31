package org.firstinspires.ftc.teamcode.relicrecovery.scorpion.machine

import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.util.LinearOnly

/**
 * Created by Nathan.Smith.19 on 12/6/2017.
 */
@LinearOnly @Deprecated(message = "no longer in use")
class JointedSlapperMachine : IMachine {

    enum class PushDirection {
        RIGHT, LEFT
    }

    val CENTER_PUSH_POSITION = 0.45
    val RIGHT_PUSH_POSITION = 0.8
    val LEFT_PUSH_POSITION = 0.15
    val RESTING_LR_POSITION = 0.4

    val INITIAL_PUSH_BACK_POSITION = 0.55

    val RESTING_VERTICAL_POSITION = 0.95
    val OUT_TEMP_VERTICAL_POSITION = 0.65
    val OUT_VERTICAL_POSITION = 0.0


    lateinit var VERTICAL : Servo
    lateinit var LEFTRIGHTER : Servo
    val timer = ElapsedTime()

    override fun init(robot: IRobot) {
        VERTICAL = robot.opMode().hardwareMap.get(Servo::class.java, "JS_VERT")
        LEFTRIGHTER = robot.opMode().hardwareMap.get(Servo::class.java, "JS_LR")
    }

    fun resting() {
        VERTICAL.position = OUT_TEMP_VERTICAL_POSITION
        LEFTRIGHTER.position = INITIAL_PUSH_BACK_POSITION
        hold(400)
        LEFTRIGHTER.position = RESTING_LR_POSITION
        hold(200)
        VERTICAL.position = RESTING_VERTICAL_POSITION
    }

    fun out() {
        LEFTRIGHTER.position = INITIAL_PUSH_BACK_POSITION
        hold(100)
        VERTICAL.position = 0.4
        hold(500)
        LEFTRIGHTER.position = CENTER_PUSH_POSITION
        hold(100)
        VERTICAL.position = OUT_VERTICAL_POSITION
    }

    fun push(direction : PushDirection) {

        var reset = 0.0

        when(direction){
            PushDirection.LEFT -> {
                LEFTRIGHTER.position = LEFT_PUSH_POSITION
                reset = 0.3
            }

            PushDirection.RIGHT -> {
                LEFTRIGHTER.position = RIGHT_PUSH_POSITION
                reset = 0.55
            }
        }
        hold(1000)
    }

    override fun stop() {}

    fun hold(millis : Long){
        timer.reset()
        while(timer.milliseconds() < millis){}
    }

}