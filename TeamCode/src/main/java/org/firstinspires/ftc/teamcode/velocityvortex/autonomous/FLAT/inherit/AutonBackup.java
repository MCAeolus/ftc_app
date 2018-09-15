package org.firstinspires.ftc.teamcode.velocityvortex.autonomous.FLAT.inherit;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

/**
 * Created by Nathan.Smith.19 on 2/15/2017.
 */

@Autonomous(name = "Plan 'B' Autonomous")@Disabled
public class AutonBackup extends GyroAutonomousBase {
    @Override
    public void runOpMode() throws InterruptedException {
        super.runOpMode();

        drive(0.5, .05, false, true);
        shoot(1, false);
        drive(0.5, .7, true, true);
        while(opModeIsActive())idle();
    }
}
