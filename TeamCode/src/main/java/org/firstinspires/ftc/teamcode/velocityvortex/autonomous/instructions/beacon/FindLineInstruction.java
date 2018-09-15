package org.firstinspires.ftc.teamcode.velocityvortex.autonomous.instructions.beacon;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

import org.firstinspires.ftc.teamcode.velocityvortex.autonomous.AutonomousReference;
import org.firstinspires.ftc.teamcode.velocityvortex.autonomous.instructions.sixwheel_drive.MoveInstruction;
import org.firstinspires.ftc.teamcode.velocityvortex.smidautils.Instruction;
import org.firstinspires.ftc.teamcode.velocityvortex.smidautils.LinearMotor;
import org.firstinspires.ftc.teamcode.velocityvortex.smidautils.Potentiometer;

/**
 * Created by Nathan.Smith.19 on 11/1/2016.
 */
@Deprecated
public class FindLineInstruction extends Instruction {

    public FindLineInstruction(double y_power, OpticalDistanceSensor l_sensor, Potentiometer pot, LinearMotor[] motors) {
        super("FIND LINE", y_power, l_sensor, pot, motors);
    }

    @Override
    public void execute(OpMode robot) {
        double y = (Double) getData().get(0);
        OpticalDistanceSensor l_sensor = (OpticalDistanceSensor) getData().get(1);
        Potentiometer pot = (Potentiometer) getData().get(2);
        LinearMotor[] motors = (LinearMotor[]) getData().get(3);

        resetPos(pot, 4.5, 3,(LinearOpMode)robot, motors);
        pot.getArmServo().getController().pwmDisable();

        double rot = pot.getRotation();

        MoveInstruction.run(y, -y, motors);
        while (((LinearOpMode) robot).opModeIsActive()
                && l_sensor.getLightDetected() <= AutonomousReference.LIGHT_THRESHOLD) {
            if (pot.getRotation() < rot) {
                resetPos(pot, 4.5, 3, (LinearOpMode) robot, motors);
                MoveInstruction.run(y, -y, motors);
            }
            ((LinearOpMode) robot).idle();
        }

        MoveInstruction.stop(motors);
        pot.getArmServo().setPosition(0);
    }

    private void resetPos(Potentiometer pot, double ideal_inch, double min_inch, LinearOpMode robot, LinearMotor[] motors){
        pot.getArmServo().setPosition(0);
        double inch = pot.getWalLDistance();
        while(pot.getWalLDistance() < ideal_inch){
            if(pot.getRotation() > inch){
                pot.getArmServo().setPosition(pot.getArmServo().getPosition() + .1);
                inch = pot.getWalLDistance();
                robot.idle();
            }else {
                if(inch < min_inch){
                    new MoveInstruction(1,0,4,0,motors).execute(robot);
                    new MoveInstruction(0,1,0,4,motors).execute(robot);
                }
                break;
            }
        }
    }
}
