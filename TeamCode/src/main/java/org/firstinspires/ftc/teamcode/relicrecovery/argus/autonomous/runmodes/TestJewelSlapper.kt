package org.firstinspires.ftc.teamcode.relicrecovery.argus.autonomous.runmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark
import org.firstinspires.ftc.teamcode.relicrecovery.argus.autonomous.AutonomousBase
import org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.UltrasonicSensorSet

/**
 * Created by Nathan.Smith.19 on 2/17/2018.
 */

@Autonomous(name="Sensor Test")
class TestJewelSlapper : AutonomousBase() {

    override fun runOpMode() {
        super.runOpMode()
        VU_SECT.deactivate()


        waitForStart()

        while(opModeIsActive()) {
            telemetry.addData("Rotation", IMU.getZ360())
            telemetry.addData("US Right", UltrasonicSensors.sensor_r.cmUltrasonic())
            telemetry.addData("OPT Right", UltrasonicSensors.sensor_r.cmOptical())
            telemetry.addData("US Left", UltrasonicSensors.sensor_l.cmUltrasonic())
            telemetry.addData("OPT Left", UltrasonicSensors.sensor_l.cmOptical())
            telemetry.addData("US Back", UltrasonicSensors.sensor_b.cmUltrasonic())
            telemetry.addData("OPT Back", UltrasonicSensors.sensor_b.cmOptical())

            telemetry.addData("SENSOR STATUS US L: ", UltrasonicSensors.sensor_l.deviceClient.healthStatus)
            telemetry.addData("SENSOR STATUS US R: ", UltrasonicSensors.sensor_r.deviceClient.healthStatus)
            telemetry.addData("SENSORS STATUS US B: ", UltrasonicSensors.sensor_b.deviceClient.healthStatus)
            telemetry.update()
        }
    }

}