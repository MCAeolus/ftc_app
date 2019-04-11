package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode

@Autonomous(name="Music Tester")@Disabled
class MusicTester : LinearOpMode() {

    override fun runOpMode() {

        telemetry.addData("STATUS", "preloading music...")
        telemetry.update()
        telemetry.addData("STATUS", "music loaded.")
        telemetry.update()

        waitForStart()

    }

}