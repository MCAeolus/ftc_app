package org.firstinspires.ftc.teamcode.relicrecovery.argus.machine

import com.qualcomm.hardware.lynx.LynxI2cColorRangeSensor
import com.qualcomm.robotcore.hardware.DistanceSensor
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.relicrecovery.scorpion.machine.IMachine

/**
 * Created by Nathan.Smith.19 on 2/18/2018.
 */
class DistanceSensor : IMachine {

    lateinit var sensor : LynxI2cColorRangeSensor

    override fun init(robot: IRobot) {
        sensor = robot.opMode().hardwareMap.get(LynxI2cColorRangeSensor::class.java, "ADS")
    }

    fun getDistance() : Double {
        return sensor.getDistance(DistanceUnit.CM)
    }

    override fun stop() {
    }
}