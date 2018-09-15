package org.firstinspires.ftc.teamcode.velocityvortex.autonomous.instructions;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.velocityvortex.smidautils.Instruction;

/**
 * Created by Nathan.Smith.19 on 11/4/2016.
 */
@Deprecated
public class ShootInstruction extends Instruction {

    private ElapsedTime runtime = new ElapsedTime();

    public ShootInstruction(Servo servo, DcMotor motor) {
        super("SHOOT", servo, motor);
    }

    @Override
    public void execute(OpMode robot) {
        Servo servo = (Servo) getData().get(0);
        DcMotor motor = (DcMotor) getData().get(1);

        motor.setPower(1);
        runtime.reset();
        while(runtime.seconds() < 2.5)
                ((LinearOpMode)robot).idle();

        motor.setPower(0);
        servo.setPosition(0);
        runtime.reset();
        while(runtime.seconds() < 0.75)
                ((LinearOpMode)robot).idle();

        motor.setPower(1);
        runtime.reset();
        while(runtime.seconds() < 2.5)
            ((LinearOpMode)robot).idle();

        motor.setPower(0);
    }
}
