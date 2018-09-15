package org.firstinspires.ftc.teamcode.velocityvortex.autonomous.instructions;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.velocityvortex.smidautils.Instruction;

/**
 * Created by Nathan.Smith.19 on 11/5/2016.
 */
@Deprecated
public class LiftBackInstruction extends Instruction {


    public LiftBackInstruction(DcMotor lift, double speed) {
        super("LIFT", lift, speed);
    }

    @Override
    public void execute(OpMode robot) {
        DcMotor lift = (DcMotor) getData().get(0);
        double speed = (Double) getData().get(1);

        lift.setPower(speed);
    }
}
