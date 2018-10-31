package org.firstinspires.ftc.teamcode.relicrecovery.argus.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import org.firstinspires.ftc.teamcode.relicrecovery.argus.machine.RelicExtender

/**
 * Created by Nathan.Smith.19 on 3/7/2018.
 */
@Disabled
@Autonomous(name="testttttt")
class Test : AutonomousBase() {

    override fun runOpMode() {
        super.runOpMode()


        val relicextender = COMPONENTS["Relic Extender"] as RelicExtender

        waitForStart()
        relicextender.setGrabberPosition(RelicExtender.GrabberPosition.OPEN)
        hold(1000)
        relicextender.setHandlePosition(RelicExtender.HandlePosition.GRAB)
        hold(1000)
        relicextender.resting()

    }


}