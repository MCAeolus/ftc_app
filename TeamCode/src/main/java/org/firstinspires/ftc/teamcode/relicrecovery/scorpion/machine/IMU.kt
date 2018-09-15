package org.firstinspires.ftc.teamcode.relicrecovery.scorpion.machine

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import org.firstinspires.ftc.robotcore.external.navigation.Orientation
import org.firstinspires.ftc.teamcode.common.robot.IRobot

/**
 * Created by Nathan.Smith.19 on 11/20/2017.
 */
class IMU : IMachine {

    lateinit var initialOrientation : Orientation
    lateinit var VS : VoltageSensor
    lateinit var IMU : BNO055IMU


    override fun init(robot: IRobot) {
        val IMU_PAR = BNO055IMU.Parameters()
        IMU_PAR.angleUnit = BNO055IMU.AngleUnit.DEGREES
        IMU_PAR.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC
        IMU_PAR.loggingEnabled = true
        IMU_PAR.loggingTag = "IMU"
        IMU_PAR.accelerationIntegrationAlgorithm = JustLoggingAccelerationIntegrator()

        IMU = robot.opMode().hardwareMap.get(com.qualcomm.hardware.bosch.BNO055IMU::class.java, "imu")
        IMU.initialize(IMU_PAR)

        VS = robot.opMode().hardwareMap.voltageSensor.iterator().next()

        initialOrientation = IMU.angularOrientation.toAxesOrder(AxesOrder.XYZ)


    }

    fun getX360() : Float {
        val x = XYZ().firstAngle
        if(x < 0) return x + 360
        else return x
    }

    fun getY360() : Float {
        val y = XYZ().secondAngle
        if(y < 0) return y + 360
        else return y
    }

    fun getZ360() : Float {
        val z = XYZ().thirdAngle
        if(z < 0) return z + 360
        else return z
    }


    fun XYZ() =
        IMU.angularOrientation.toAxesOrder(AxesOrder.XYZ)


    override fun stop() {}
}