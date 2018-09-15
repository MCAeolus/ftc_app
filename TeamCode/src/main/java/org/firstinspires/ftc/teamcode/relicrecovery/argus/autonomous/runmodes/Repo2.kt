package org.firstinspires.ftc.teamcode.relicrecovery.argus.autonomous.runmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.teamcode.relicrecovery.argus.autonomous.AutonomousBase

/**
 * Created by Nathan.Smith.19 on 2/18/2018.
 */
@Autonomous(name="repo test")
class Repo2 : AutonomousBase() {

    override fun runOpMode() {
        super.runOpMode()
        VU_SECT.deactivate()

        JewelSlapper.out()
        while(opModeIsActive()) {

            //reposition(IMU.initialOrientation.thirdAngle, Rotation.ONE_EIGHTY)
            //telemetry.update()
        }
    }
}