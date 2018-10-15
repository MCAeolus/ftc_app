package org.firstinspires.ftc.teamcode.relicrecovery.argus.machine

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine

/**
 * Created by Nathan.Smith.19 on 2/12/2018.
 */
class UltrasonicSensorSet : IMachine {

    enum class UltrasonicSide {
        LEFT, RIGHT, BACK
    }

    lateinit var sensor_l : ModernRoboticsI2cRangeSensor
    lateinit var sensor_r : ModernRoboticsI2cRangeSensor
    lateinit var sensor_b : ModernRoboticsI2cRangeSensor

    override fun init(robot: IRobot) {
        sensor_l = robot.opMode().hardwareMap.get(ModernRoboticsI2cRangeSensor::class.java, "US_L")

        sensor_r = robot.opMode().hardwareMap.get(ModernRoboticsI2cRangeSensor::class.java, "US_R")

        sensor_b = robot.opMode().hardwareMap.get(ModernRoboticsI2cRangeSensor::class.java, "US_B")

        sensor_l.initialize()
        sensor_r.initialize()
        sensor_b.initialize()
    }

    fun getSensor(side : UltrasonicSide) : ModernRoboticsI2cRangeSensor {
        when(side) {
            UltrasonicSide.LEFT -> return sensor_l
            UltrasonicSide.RIGHT -> return sensor_r
            UltrasonicSide.BACK -> return sensor_b
        }
    }

    override fun stop() {}
}