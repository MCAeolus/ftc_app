package org.firstinspires.ftc.teamcode.relicrecovery.scorpion.autonomous.newresidence

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import org.firstinspires.ftc.teamcode.relicrecovery.scorpion.autonomous.AutonomousBase

/**
 * Created by Nathan.Smith.19 on 1/12/2018.
 */
@Autonomous(name="JEWEL, BLUE")@Disabled
class JEWELBLUE : AutonomousBase() {

    override fun runOpMode() {
        super.runOpMode()

        waitForStart()

        slapper.out()
        hold(500)

        if(C_S.RGB().R < C_S.RGB().B)
            slapper.left()
        else slapper.right()

        hold(1000)
        slapper.resting()
    }
}