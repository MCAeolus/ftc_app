package org.firstinspires.ftc.teamcode.velocityvortex.autonomous.FLAT.inherit;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.velocityvortex.smidautils.GameSide;

/**
 * Created by Nathan.Smith.19 on 2/9/2017.
 */
@Disabled
@Autonomous(name="BLUE SIDE")
public class BLUEGyroAuton extends GyroAutonomousBase {

    @Override
    public void runOpMode() throws InterruptedException {
        super.runOpMode();
        drive(0.5, .9, true, true);
        turn(0.1,0,0.5);
        //sleep(250);
        shoot(1, true);
        shoot(1, false);

        turn(0.1, -65, 1);
        drive(0.45, 1.7, true, true);
        turn(0.1, -177, 2);

        findBeaconLine(-0.25);
        beacon(-0.2, 0.1, -177, GameSide.BLUE);

        turn(0.1, -176, 0.1);
        drive(-0.25,1.4, false, true);
        //turn(0.1, -177, 0.1);
        findBeaconLine(-0.3);
        //turn(0.1, -177, 0.1);
        beacon(-0.2, 0.1, -177, GameSide.BLUE);

    }
}
