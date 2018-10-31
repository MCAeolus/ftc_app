package org.firstinspires.ftc.teamcode.relicrecovery.argus.autonomous.runmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import org.firstinspires.ftc.teamcode.relicrecovery.argus.autonomous.AutonomousBase

/**
 * Created by Nathan.Smith.19 on 2/24/2018.
 */
@Disabled
@Autonomous(name="Backup-BLUE-jewelonly")
class BackupBlue : AutonomousBase() {

    override fun runOpMode() {
        super.runOpMode()

        VU_SECT.deactivate()
        waitForStart()


        JewelSlapper.out()
        hold(1000)
        if(ColorSensor.RGB().B > ColorSensor.RGB().R)
            JewelSlapper.left()
        else JewelSlapper.right()

        hold(200)

        GlyphThroughput.expand_throughtake()

        JewelSlapper.resting()

    }
}