package org.firstinspires.ftc.teamcode.relicrecovery.scorpion.autonomous

/**
 * Created by Nathan.Smith.19 on 11/22/2017.
 */
//@Autonomous(name="Repo", group="DEV-2")
class RepositionTest : AutonomousBase() {
    override fun runOpMode() {
        super.runOpMode()

        waitForStart()

        val z_zero = IMU.initialOrientation.thirdAngle

        while(opModeIsActive()){
            reposition(z_zero)
        }
    }
}