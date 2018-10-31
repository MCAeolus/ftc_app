package org.firstinspires.ftc.teamcode.relicrecovery.scorpion.robotmodes

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.common.robot.LinearRobot
import org.firstinspires.ftc.teamcode.common.drivetrain.MecanumDrive
import org.firstinspires.ftc.teamcode.relicrecovery.scorpion.machine.DummyMachine

/**
 * Created by Nathan.Smith.19 on 11/15/2017.
 */

@TeleOp(name="GYRO TEST", group="DEV")@Disabled
class GyroTestRobot : LinearRobot(MecanumDrive(arrayOf("FL", "FR", "BL", "BR")), mapOf(Pair("D", DummyMachine()))) {

    lateinit var GYRO : BNO055IMU

    override fun runOpMode() {
        super.runOpMode()

        val IMU_PAR = BNO055IMU.Parameters()
        IMU_PAR.angleUnit = BNO055IMU.AngleUnit.DEGREES
        IMU_PAR.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC
        IMU_PAR.accelerationIntegrationAlgorithm = JustLoggingAccelerationIntegrator();


        GYRO = hardwareMap.get(BNO055IMU::class.java, "imu")
        GYRO.initialize(IMU_PAR)

        waitForStart()

        while(opModeIsActive()){
            telemetry.addData("ANG ORIENT: ${GYRO.angularOrientation.toString()}", "")

            telemetry.update()
        }
    }
}