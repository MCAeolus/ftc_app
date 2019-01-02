package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous.playback

import android.icu.text.AlphabeticIndex
import com.acmerobotics.dashboard.config.Config
import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous.AutonomousBase


@Autonomous(name="Auto Player")
class AutoPlayer : AutonomousBase() {

    override fun runOpMode() {
        super.runOpMode()

        while(!isStarted) {
            telemetry.addData("Status", "file is ${RecordingFileFromFTCDashboard.FILE_NAME_TO_PLAY}")
            telemetry.update()
        }

        if(RecordingFileFromFTCDashboard.FILE_NAME_TO_PLAY == "")requestOpModeStop()
        else {
            val input = hardwareMap.appContext.openFileInput(RecordingFileFromFTCDashboard.FILE_NAME_TO_PLAY)
            val playback = Recording.Play(input, hardwareMap)

            val timer = ElapsedTime()
            timer.reset()
            while(opModeIsActive()) {
                val ret = playback.playback_data(timer.time())
                var expected_rotation = 0.0

                for(d in ret.first) {
                    val device = hardwareMap.get(d.name)

                    when(device) {
                        is DcMotor -> {
                            device.mode = DcMotor.RunMode.RUN_TO_POSITION
                            device.targetPosition = d.data[1].toInt()
                            device.power = d.data[0]
                        }
                        is Servo -> device.position = d.data[0]
                        is BNO055IMU -> {
                            expected_rotation = d.data[0]
                        }
                    }

                    telemetry.addData("Current rotation error", expected_rotation - IMU.XYZ().thirdAngle)
                    telemetry.update()
                }

                if(ret.second) break
            }

            /**
            while(opModeIsActive()) {
                if(playback.playback(time)) break
            }
             */

            input.close()
        }
    }


    @Config
    object RecordingFileFromFTCDashboard {
        @JvmField var FILE_NAME_TO_PLAY = ""
    }

}
