package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous.playback_new

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous.AutonomousBase
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems.MecanumDriveTrain

@Autonomous(name= "Replay Mode")@Disabled
class ReplayAutonomous : AutonomousBase() {

    override fun runOpMode() {
        super.runOpMode()

        while(!isStarted) {
            telemetry.addData("File", if(RecordingConfig.desiredFilePath=="")"not selected." else RecordingConfig.desiredFilePath)
            telemetry.update()
        }

        if(RecordingConfig.desiredFilePath == "")requestOpModeStop()
        val RECORD = TimeStampedData.DataStream(RecordingConfig.desiredFilePath, hardwareMap)

        RECORD.load()
        RECORD.prepare()

        (DRIVETRAIN as MecanumDriveTrain).resetEncoders()

        val STARTTIME = time
        while(opModeIsActive()) {
            val elapsed = time - STARTTIME

            telemetry.addData("elapsed time", elapsed)
            telemetry.update()

            val data = RECORD.pointsUntil(elapsed)
            var targetRotation = 0.0
            data.first.forEach {iv ->
                iv.bytes.forEach {
                    val device = hardwareMap.get(it.name)

                    when(device) {
                        is DcMotor -> {
                            device.mode = DcMotor.RunMode.RUN_USING_ENCODER
                            device.power = it.data[0]
                            device.targetPosition = it.data[1].toInt()
                        }
                        is CRServo -> device.power = it.data[0]

                        is Servo -> device.position = it.data[0]

                        is BNO055IMU -> targetRotation = it.data[0]
                    }
                }
            }




            if(data.second)break
        }
        DRIVETRAIN.stop()

    }

}