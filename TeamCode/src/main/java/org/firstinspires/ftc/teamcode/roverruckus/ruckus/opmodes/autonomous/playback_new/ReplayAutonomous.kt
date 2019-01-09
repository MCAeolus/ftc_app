package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous.playback_new

import android.icu.text.AlphabeticIndex
import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous.AutonomousBase
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems.MecanumDriveTrain

@Autonomous(name= "Replay Mode")
class ReplayAutonomous : AutonomousBase() {

    override fun runOpMode() {
        super.runOpMode()

        while(!isStarted) {
            telemetry.addData("File", if(RecordingConfig.FILE_NAME=="")"not entered." else RecordingConfig.FILE_NAME)
            telemetry.update()
        }

        if(RecordingConfig.FILE_NAME == "")requestOpModeStop()
        val RECORD = TimeStampedData.DataStream(RecordingConfig.FILE_NAME, hardwareMap)

        if(!hardwareMap.appContext.fileList().contains(RECORD.realFileName)) {
            telemetry.addData("FILE DOES NOT EXIST.", "")
            telemetry.update()
            holdToShutdown()
        }

        RECORD.load()
        if(RecordingConfig.SHOULD_TRIM == "1") RECORD.trim()
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