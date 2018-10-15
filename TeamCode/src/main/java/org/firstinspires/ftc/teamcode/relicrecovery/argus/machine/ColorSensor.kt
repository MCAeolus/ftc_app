package org.firstinspires.ftc.teamcode.relicrecovery.argus.machine

import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.util.RGB
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine

/**
 * Created by Nathan.Smith.19 on 11/2/2017.
 */
class ColorSensor : IMachine {

    lateinit var color_sensor : com.qualcomm.robotcore.hardware.ColorSensor

    override fun init(robot: IRobot) {
        color_sensor = robot.opMode().hardwareMap.get(com.qualcomm.robotcore.hardware.ColorSensor::class.java, "JSC")
    }

    override fun stop() {}

    fun RGB() = RGB(color_sensor.red(), color_sensor.green(), color_sensor.blue())
}