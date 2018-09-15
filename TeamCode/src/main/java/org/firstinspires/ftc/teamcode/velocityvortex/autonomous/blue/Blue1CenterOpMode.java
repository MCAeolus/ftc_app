package org.firstinspires.ftc.teamcode.velocityvortex.autonomous.blue;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.velocityvortex.autonomous.BaseAutonomousOpMode;
import org.firstinspires.ftc.teamcode.velocityvortex.autonomous.instructions.beacon.FindLineInstruction;
import org.firstinspires.ftc.teamcode.velocityvortex.autonomous.instructions.sixwheel_drive.MoveInstruction;

/**
 * Created by Nathan.Smith.19 on 11/4/2016.
 */
@Autonomous(name = "Blue1", group = "autonomous")
@Disabled
public class Blue1CenterOpMode extends BaseAutonomousOpMode {

    @Override
    public void runOpMode() throws InterruptedException { //FAR
        super.runOpMode();

        waitForStart();

        new MoveInstruction(1, 1, 45, 45, super.motors).execute(this);
        new MoveInstruction(1, -1, 4, -4, super.motors).execute(this);
        new MoveInstruction(1, 1, 43, 43, super.motors).execute(this);
        new MoveInstruction(-1, 1, -4, 4, super.motors).execute(this);

        new FindLineInstruction(0.7, super.opticalDistanceSensor, super.potentiometer, super.motors).execute(this);
        //new PressServoInstruction(GameSide.BLUE, super.arm_servo, super.colorSensor, super.motors).execute(this);
        new MoveInstruction(1, 1, 4, 4, super.motors).execute(this);

        new FindLineInstruction(0.7, super.opticalDistanceSensor, super.potentiometer, super.motors).execute(this);
        //new PressServoInstruction(GameSide.BLUE, super.arm_servo, super.colorSensor, super.motors).execute(this);
    }
}
