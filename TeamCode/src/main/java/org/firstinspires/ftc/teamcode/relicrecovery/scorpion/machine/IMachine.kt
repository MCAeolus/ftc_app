package org.firstinspires.ftc.teamcode.relicrecovery.scorpion.machine

import org.firstinspires.ftc.teamcode.common.robot.IRobot

/**
 * Created by Nathan.Smith.19 on 10/19/2017.
 */
interface IMachine {

    fun init(robot : IRobot)

    fun stop()
}