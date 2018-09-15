package org.firstinspires.ftc.teamcode.velocityvortex.autonomous.blue;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.velocityvortex.autonomous.BaseAutonomousOpMode;

/**
 * Created by Nathan.Smith.19 on 11/4/2016.
 */
@Autonomous(name = "Blue2", group = "autonomous")

@Disabled
public class Blue2CenterOpMode extends BaseAutonomousOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        super.runOpMode();

        waitForStart();

        //new MoveInstruction(-0.1, 1, 18, 44, 0, super.motors).execute(this);
        //new MoveInstruction(1, 0, 42, 0, 0, super.motors).execute(this);
        //new MoveInstruction(0, 1, 0, 68, 0, super.motors).execute(this);
        //new MoveInstruction(1, 0, 0, 0, 27, super.motors).execute(this);
        //new ShootInstruction(shoot_servo, shoot_motor).execute(this);
        //new ShootInstruction(shoot_servo, shoot_motor).execute(this);
        //new MoveInstruction(0, 1, 0, 64, 0, super.motors).execute(this);
    }
}
