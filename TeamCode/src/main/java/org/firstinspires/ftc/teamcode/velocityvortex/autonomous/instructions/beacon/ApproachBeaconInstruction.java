package org.firstinspires.ftc.teamcode.velocityvortex.autonomous.instructions.beacon;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.velocityvortex.autonomous.AutonomousReference;
import org.firstinspires.ftc.teamcode.velocityvortex.smidautils.Instruction;
import org.firstinspires.ftc.teamcode.velocityvortex.smidautils.OmniMotor;

/**
 * Created by Nathan.Smith.19 on 11/2/2016.
 */

@Deprecated
public class ApproachBeaconInstruction extends Instruction {

    private ElapsedTime runtime = new ElapsedTime();

    public ApproachBeaconInstruction(double tilt, OpticalDistanceSensor l_sensor, OmniMotor[] motors) {
        super("APPROACH BEACON", tilt, l_sensor, motors);
    }

    @Override
    public void execute(OpMode robot) {
        double tilt = (Double) getData().get(0);
        OpticalDistanceSensor l_sensor = (OpticalDistanceSensor) getData().get(1);
        OmniMotor[] motors = (OmniMotor[]) getData().get(2);

        double timeout = 2;
        boolean rot = false;
        runtime.reset();
        //MoveInstruction.run(-0.5, 0, 0, motors);
        while (((LinearOpMode) robot).opModeIsActive() && runtime.seconds() < timeout) {

            if(l_sensor.getLightDetected() < AutonomousReference.LIGHT_THRESHOLD && !rot) {
                //MoveInstruction.run(-0.2, 0, tilt, motors);
                timeout += 0.005;
                rot = true;
            }
            else if(rot){
                //MoveInstruction.run(-0.5, 0, 0, motors);
                rot = false;
            }

            ((LinearOpMode) robot).idle();
        }
        //MoveInstruction.stop(motors);
    }
}
