package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous.playback_new

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import java.io.File

@Autonomous(name= "Recording Browser")
class BrowserOPMode : LinearOpMode() {

    override fun runOpMode() {
        val fileList = hardwareMap.appContext.fileList()

        var selectorLoc = 0
        val timeoutDelta = 150.0
        var b_press = 0
        var b_press_selector_focus = -1
        var pressTimeout = -1.0

        while(opModeIsActive() && !gamepad1.x) {
            telemetry.captionValueSeparator = ""
            telemetry.addData("Use the up and down arrow on the controller to select the files below.","")
            telemetry.addData("Press 'B' TWICE (currently $b_press/2) to delete the selected file, or 'A' to mark the selected file for usage in Auto Player. Press 'X' to exit the selector.", "")
            telemetry.addData("All existing files shown below(${fileList.size}:", "")

            if(!inTimeout(timeoutDelta, pressTimeout)) {
                pressTimeout = time
                if (gamepad1.dpad_up)
                    selectorLoc = if (selectorLoc > 0) selectorLoc - 1 else 0
                if (gamepad1.dpad_down)
                    selectorLoc = if (selectorLoc < fileList.lastIndex) selectorLoc + 1 else selectorLoc
                if (gamepad1.b) {
                    b_press++
                    if(b_press_selector_focus == -1) b_press_selector_focus = selectorLoc
                    if(b_press > 1 && b_press_selector_focus == selectorLoc) {
                        b_press = 0
                        b_press_selector_focus = -1
                        hardwareMap.appContext.deleteFile(fileList[selectorLoc])
                        selectorLoc = if (selectorLoc > 0) selectorLoc - 1 else 0
                    }else if(b_press_selector_focus != selectorLoc && b_press > 0) {
                        b_press_selector_focus = -1
                        b_press = 0
                    }
                }
                if(gamepad1.a) {
                    RecordingConfig.FILE_NAME = fileList[selectorLoc]
                }
            }

            for (i in 0 until fileList.size)
                telemetry.addData("${if(selectorLoc == i) ">>" else ""}$i:", "${fileList[i]}${if(RecordingConfig.FILE_NAME == fileList[i]) " *" else ""}")


            telemetry.update()
        }




        telemetry.captionValueSeparator = ":"
    }

    private fun inTimeout(delta : Double, timeout : Double) : Boolean = time - timeout < delta || timeout == -1.0

}