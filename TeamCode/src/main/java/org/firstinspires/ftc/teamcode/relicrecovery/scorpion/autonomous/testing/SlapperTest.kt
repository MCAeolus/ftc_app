package org.firstinspires.ftc.teamcode.relicrecovery.scorpion.autonomous.testing

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import org.firstinspires.ftc.teamcode.relicrecovery.scorpion.autonomous.AutonomousBase

/**
 * Created by Nathan.Smith.19 on 1/12/2018.
 */

@Autonomous(name="slapper")@Disabled
class SlapperTest : AutonomousBase() {

    override fun runOpMode() {
        super.runOpMode()
        waitForStart()

        slapper.out()

        hold(600)

        if(C_S.RGB().B < C_S.RGB().R){
            slapper.left()
        }else slapper.right()

        hold(2000)

        hold(2000)
        slapper.resting()
    }
}