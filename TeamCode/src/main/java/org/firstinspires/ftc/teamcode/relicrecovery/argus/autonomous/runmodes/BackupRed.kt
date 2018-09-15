package org.firstinspires.ftc.teamcode.relicrecovery.argus.autonomous.runmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.relicrecovery.argus.autonomous.AutonomousBase

/**
 * Created by Nathan.Smith.19 on 2/24/2018.
 */

@Autonomous(name="Backup-RED-jewelonly")
class BackupRed : AutonomousBase() {

    override fun runOpMode() {
        super.runOpMode()

        VU_SECT.deactivate()
        waitForStart()


        JewelSlapper.out()
        hold(1000)
        if(ColorSensor.RGB().R > ColorSensor.RGB().B)
            JewelSlapper.left()
        else JewelSlapper.right()

        hold(200)

        GlyphThroughput.expand_throughtake()

        JewelSlapper.resting()

    }
}