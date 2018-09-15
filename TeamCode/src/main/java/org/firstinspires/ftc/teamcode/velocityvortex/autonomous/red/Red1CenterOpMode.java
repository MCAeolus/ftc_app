package org.firstinspires.ftc.teamcode.velocityvortex.autonomous.red;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.velocityvortex.autonomous.BaseAutonomousOpMode;

/**
 * Created by NathanSmith on 9/15/2016.
 */
@Autonomous(name = "Red1", group = "autonomous")@Disabled@Deprecated
public class Red1CenterOpMode extends BaseAutonomousOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        super.runOpMode();

        waitForStart();

        //new MoveInstruction(-0.1, 1, 7, 44, 0, super.motors).execute(this);
        //new MoveInstruction(1, 0, -42, 0, 0, super.motors).execute(this);
        //new MoveInstruction(1, 1, 0, 62, 0, super.motors).execute(this);
        //new MoveInstruction(1, 0, 0, 0, -27, super.motors).execute(this);
        //new ShootInstruction(shoot_servo, shoot_motor).execute(this);
        //new ShootInstruction(shoot_servo, shoot_motor).execute(this);
        //new MoveInstruction(0, 1, 0, 64, 0, super.motors).execute(this);
        //new LiftBackInstruction(super.lift_motor, -1);
        //new MoveInstruction(0, 1, 0, -42, 0, super.motors).execute(this);
        //new LiftBackInstruction(lift_motor, -1).execute(this);
        //new MoveInstruction(0, 1, 0, -20, 0, super.motors).execute(this);
        //new LiftBackInstruction(lift_motor, 0).execute(this);

        //new LiftBackInstruction(super.lift_motor, 0);
    }
}
