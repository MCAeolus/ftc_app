package org.firstinspires.ftc.teamcode.velocityvortex.autonomous.instructions.beacon;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.velocityvortex.smidautils.Instruction;

/**
 * Created by Nathan.Smith.19 on 11/2/2016.
 */
@Deprecated
public class RunServoInstruction extends Instruction {


    private ElapsedTime runtime = new ElapsedTime();

    public RunServoInstruction(CRServo servo, double force, double time) {
        super("PRESS SERVO", servo, force, time);
    }

    @Override
    public void execute(OpMode robot) {
        CRServo servo = (CRServo) getData().get(0);
        double force = (double) getData().get(1), time = (double) getData().get(2);
        servo.setPower(force);
        runtime.reset();
        while(runtime.seconds() < time){((LinearOpMode)robot).idle();}
    }
}

