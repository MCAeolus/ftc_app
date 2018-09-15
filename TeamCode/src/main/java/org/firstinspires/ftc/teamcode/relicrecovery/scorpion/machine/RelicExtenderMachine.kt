package org.firstinspires.ftc.teamcode.relicrecovery.scorpion.machine

import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.common.robot.IRobot

/**
 * Created by Nathan.Smith.19 on 12/2/2017.
 */
class RelicExtenderMachine : IMachine {

    lateinit var extender : CRServo
    lateinit var lifter : Servo
    lateinit var grabber : Servo

    override fun init(robot: IRobot) {
        extender = robot.opMode().hardwareMap.get(CRServo::class.java, "extendeRE")
        lifter = robot.opMode().hardwareMap.get(Servo::class.java, "lifteRE")
        grabber = robot.opMode().hardwareMap.get(Servo::class.java, "grabbeRE")
    }

    fun extend(dr : Boolean, rn : Boolean) { //true == outwards
        if(rn) if(dr)
                 extender.power =  1.0
        else     extender.power = -1.0
        else     extender.power =  0.0
    }

    fun lift(rawYInput : Double) {
        val correctedInput = (rawYInput/2) + 0.5
        lifter.position = correctedInput*(0.5)
    }

    fun grabber(grab : Boolean) {
        if(grab) grabber.position = 0.0
        else grabber.position = 1.0
    }

    override fun stop() {

    }
}