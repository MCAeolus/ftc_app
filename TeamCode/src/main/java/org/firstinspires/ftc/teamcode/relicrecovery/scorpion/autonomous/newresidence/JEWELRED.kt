package org.firstinspires.ftc.teamcode.relicrecovery.scorpion.autonomous.newresidence

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import org.firstinspires.ftc.teamcode.relicrecovery.scorpion.autonomous.AutonomousBase

/**
 * Created by Nathan.Smith.19 on 1/12/2018.
 */
@Autonomous(name="JEWEL, RED")@Disabled
class JEWELRED : AutonomousBase() {

    override fun runOpMode() {
        super.runOpMode()

        waitForStart()

        slapper.out()
        hold(500)


        if(C_S.RGB().B < C_S.RGB().R)
            slapper.left()
        else slapper.right()

        hold(1000)
        slapper.resting()
    }
}