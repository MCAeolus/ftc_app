package org.firstinspires.ftc.teamcode.common.common_machines

import org.firstinspires.ftc.teamcode.common.robot.IRobot

/**
 * Created by Nathan.Smith.19 on 10/19/2017.
 */
interface IMachine {

    fun init(robot : IRobot)

    fun stop()
}