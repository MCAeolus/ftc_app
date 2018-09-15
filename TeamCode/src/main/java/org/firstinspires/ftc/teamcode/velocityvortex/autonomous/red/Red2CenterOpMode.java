package org.firstinspires.ftc.teamcode.velocityvortex.autonomous.red;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

/**
 * Created by Nathan.Smith.19 on 11/4/2016.
 */
@Autonomous(name = "Red2", group = "autonomous")@Disabled@Deprecated
public class Red2CenterOpMode extends Red1CenterOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        super.runOpMode();

        waitForStart();

        //new MoveInstruction(0, 1, 0, 44, 0, super.motors).execute(this);
        //new MoveInstruction(1, 0, -42, 0, 0, super.motors).execute(this);
        //new MoveInstruction(0, 1, 0, 68, 0, super.motors).execute(this);
        //new MoveInstruction(1, 0, -6, 0, 0, super.motors).execute(this);
        //new MoveInstruction(1, 0, 0, 0, -27, super.motors).execute(this);
        //new ShootInstruction(shoot_servo, shoot_motor).execute(this);
        //new ShootInstruction(shoot_servo, shoot_motor).execute(this);
        //new MoveInstruction(0, 1, 0, 64, 0, super.motors).execute(this);
    }
}
