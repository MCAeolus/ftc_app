package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous

import com.qualcomm.ftccommon.SoundPlayer
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.HNAMES_RUCKUS

@Autonomous(name="Music Tester")@Disabled
class MusicTester : LinearOpMode() {

    override fun runOpMode() {
        val jeopID = hardwareMap.appContext.resources.getIdentifier(HNAMES_RUCKUS.JEOPARDY_NAME, "raw", hardwareMap.appContext.packageName)

        telemetry.addData("STATUS", "preloading music...")
        telemetry.update()
        SoundPlayer.getInstance().preload(hardwareMap.appContext, jeopID)
        telemetry.addData("STATUS", "music loaded.")
        telemetry.update()

        waitForStart()

    }

}