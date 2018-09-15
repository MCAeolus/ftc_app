package org.firstinspires.ftc.teamcode.velocityvortex.autonomous.TEST;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.velocityvortex.autonomous.instructions.sixwheel_drive.MoveInstruction;
import org.firstinspires.ftc.teamcode.velocityvortex.smidautils.Instruction;
import org.firstinspires.ftc.teamcode.velocityvortex.smidautils.LinearMotor;

/**
 * Created by Nathan.Smith.19 on 1/20/2017.
 */
@Deprecated
public class TestiNS extends Instruction {

    public TestiNS(LinearMotor[] motors, double x) {
        super("x", motors, x);
    }

    @Override
    public void execute(OpMode robot) {
        LinearMotor[] motors = (LinearMotor[]) getData().get(0);
        robot.telemetry.addData("Y","1");
        robot.telemetry.update();
        new MoveInstruction(-0.2, -0.2, -1, -1, motors).execute(robot);
    }
}
