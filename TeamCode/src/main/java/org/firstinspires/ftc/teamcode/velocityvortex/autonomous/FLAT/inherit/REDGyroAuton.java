package org.firstinspires.ftc.teamcode.velocityvortex.autonomous.FLAT.inherit;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.velocityvortex.smidautils.GameSide;

/**
 * Created by Nathan.Smith.19 on 2/9/2017.
 */
@Disabled
@Autonomous(name="RED SIDE")
public class REDGyroAuton extends GyroAutonomousBase {

    @Override
    public void runOpMode() throws InterruptedException {
        super.runOpMode();
        drive(0.5, .87, true, true);
        turn(0.1,0,0.5);
        //sleep(250);
        shoot(1, true);
        shoot(1, false);

        turn(0.1, 70, 1);
        drive(0.45, 1.75, true, true);
        turn(0.1, 3, 2);

        findBeaconLine(0.25);
        beacon(0.2, 0.1, 3, GameSide.RED);

        turn(0.1, 3, 0.1);
        drive(0.25,1.4, false, true);
        //turn(0.1, -177, 0.1);
        findBeaconLine(0.3);
        //turn(0.1, -177, 0.1);
        beacon(0.2, 0.1, 3, GameSide.RED);


    }
}
