package org.firstinspires.ftc.teamcode.velocityvortex.autonomous.TEST;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.velocityvortex.autonomous.BaseAutonomousOpMode;
import org.firstinspires.ftc.teamcode.velocityvortex.autonomous.instructions.sixwheel_drive.MoveInstruction;

/**
 * Created by Nathan.Smith.19 on 1/20/2017.
 */

@Autonomous(name="ENCODE")@Disabled@Deprecated
public class TestEncoderMode extends BaseAutonomousOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        super.runOpMode();
        waitForStart();

        new MoveInstruction(0.5,0.5,1,1,super.motors).execute(this);
        //new MoveInstruction(-0.2, -0.2, -1, -1, motors).execute(this);

        new TestiNS(motors,1).execute(this);
        while(opModeIsActive()) {
            telemetry.addData("X", super.motors[0].getMotor().getCurrentPosition());
            telemetry.update();
        }
    }
}
