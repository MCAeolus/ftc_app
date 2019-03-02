package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.replay

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode

@Autonomous(name="Trim File")@Disabled
class TrimFile : LinearOpMode() {

    override fun runOpMode() {
        /**doTelemetry("Press the start button to begin.")
        waitForStart()
        if(RecordingConfig.FILE_NAME == "") {
            doTelemetry("STATUS", "No file has been selected.")
            holdToEnd()
        } else {
            val RECORD = TimeStampedData.DataStream(RecordingConfig.FILE_NAME, hardwareMap)
            if(!hardwareMap.appContext.fileList().contains(RECORD.realFileName)) {
                doTelemetry("STATUS", "Selected file doesn't exist '${RECORD.realFileName}'")
                holdToEnd()
            } else {
                doTelemetry("STATUS", "Loading file.")
                try {
                    RECORD.load()
                } catch(e : Exception) {
                    doTelemetry("STATUS", "Failed to load file.")
                    holdToEnd()
                }
                doTelemetry("STATUS", "Trimming file.")
                try {
                    RECORD.trim()
                }catch(e : Exception) {
                    doTelemetry("STATUS", "Failed to trim file.")
                    holdToEnd()
                }
                doTelemetry("STATUS", "Writing file back to storage.")
                try {
                    RECORD.write()
                }catch(e : Exception) {
                    doTelemetry("STATUS", "Failed to write to storage.")
                    holdToEnd()
                }
                doTelemetry("STATUS", "Finished trimming file.")
                holdToEnd()

            }
        }
        **/
    }

    private fun holdToEnd() {
        while(opModeIsActive())idle()
    }

    private fun doTelemetry(info : String, info2 : String = "") {
        telemetry.addData(info, info2)
        telemetry.update()
    }
}