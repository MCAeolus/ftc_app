package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous.playback

import android.icu.text.AlphabeticIndex
import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
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
                playback.playback(timer.time())
            }
            input.close()
        }
    }


    @Config
    object RecordingFileFromFTCDashboard {
        @JvmField var FILE_NAME_TO_PLAY = ""
    }

}
