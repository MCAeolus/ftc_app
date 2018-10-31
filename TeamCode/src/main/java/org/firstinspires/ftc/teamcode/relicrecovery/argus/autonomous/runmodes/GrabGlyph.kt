package org.firstinspires.ftc.teamcode.relicrecovery.argus.autonomous.runmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.relicrecovery.argus.autonomous.AutonomousBase

/**
 * Created by Nathan.Smith.19 on 3/6/2018.
 */
@Disabled
@Autonomous(name="glyph")
class GrabGlyph : AutonomousBase() {

    override fun runOpMode() {
        super.runOpMode()
        VU_SECT.deactivate()
        waitForStart()

        run_until_glyph(2000, ElapsedTime())

        hold(5000)
    }
}