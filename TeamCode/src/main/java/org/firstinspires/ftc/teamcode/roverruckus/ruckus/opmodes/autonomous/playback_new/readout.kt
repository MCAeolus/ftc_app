package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous.playback_new

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode

@Autonomous(name="read out file")
class readout : LinearOpMode() {

    override fun runOpMode() {

        if(RecordingConfig.FILE_NAME == "")requestOpModeStop()

        val RECORD = TimeStampedData.DataStream(RecordingConfig.FILE_NAME, hardwareMap)

        RECORD.load()

        telemetry.addData("Start readout", "File: ${RecordingConfig.FILE_NAME}")
        var currentDataPoint = RECORD.nextPoint()

        while(currentDataPoint != null) {
            telemetry.addData(currentDataPoint.time.toString(), currentDataPoint.bytes.size)
            currentDataPoint = RECORD.nextPoint()
        }

        telemetry.addData("End readout", "")

        telemetry.update()
        while(!isStopRequested) idle()
    }

}