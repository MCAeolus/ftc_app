package org.firstinspires.ftc.teamcode.velocityvortex.autonomous.blue.shoot;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.velocityvortex.autonomous.BaseAutonomousOpMode;
import org.firstinspires.ftc.teamcode.velocityvortex.autonomous.instructions.ShootInstruction;
import org.firstinspires.ftc.teamcode.velocityvortex.autonomous.instructions.sixwheel_drive.MoveInstruction;

/**
 * Created by Nathan.Smith.19 on 1/20/2017.
 */

@Autonomous(name="Shoot1Blue")@Disabled
public class Blue1Shoot extends BaseAutonomousOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        super.runOpMode();
        waitForStart();

        new MoveInstruction(1,1,40,40,super.motors).execute(this);
        idle();
        new MoveInstruction(0,1,4,4,super.motors).execute(this);
        idle();
        new MoveInstruction(1,1,15,15,super.motors).execute(this);
        idle();
        new MoveInstruction(0,1,4,4,super.motors).execute(this);
        idle();
        new MoveInstruction(1,1,22,22,super.motors).execute(this);
        idle();
        new MoveInstruction(0,1,6,6,super.motors).execute(this);
        idle();
        new ShootInstruction(super.shoot_servo, super.shoot_motor).execute(this);
        idle();
        new MoveInstruction(1,0,3,3,super.motors).execute(this);
        idle();
        new MoveInstruction(1,1,10,10,super.motors).execute(this);




    }
}
