package org.firstinspires.ftc.teamcode.velocityvortex.autonomous.instructions.sixwheel_drive;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.velocityvortex.smidautils.Instruction;
import org.firstinspires.ftc.teamcode.velocityvortex.smidautils.LinearMotor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Nathan.Smith.19 on 11/28/2016.
 */
@Deprecated
public class MoveInstruction extends Instruction {

    private double TICKS_PER_INCH = 1120;
    private double FINAL_TICKS = 60;

    //24 - 16 tooth

    public MoveInstruction(double lm_power, double rm_power, double lm_distance, double rm_distance, LinearMotor[] motors) {
        super("MOVE", lm_power, rm_power, lm_distance, rm_distance, motors);
    }

    @Override
    public void execute(OpMode robot) {
        double lm_power = (Double) getData().get(0);
        double rm_power = -((Double) getData().get(1));
        double lm_distance = (Double) getData().get(2);
        double rm_distance = -((Double) getData().get(3));
        LinearMotor[] motors = (LinearMotor[])getData().get(4);

        //try {
        //    resetEncoders((LinearOpMode)robot, motors);
        //} catch (InterruptedException ignored) {}

        for(LinearMotor motor : motors)
                switch(motor.getCrossPosition()){
                    case LEFT:
                        start(lm_power, lm_distance * FINAL_TICKS, motor);
                        break;
                    case RIGHT:
                        start(rm_power, rm_distance * FINAL_TICKS, motor);
                        break;
                }

        ArrayList<LinearMotor> motor_queue = new ArrayList<>();
        motor_queue.addAll(Arrays.asList(motors));

        while (((LinearOpMode) robot).opModeIsActive() && stopMotors(motor_queue))
            ((LinearOpMode) robot).idle(); //idle for hardware cycle so that we aren't running excess CPU cycles.
        stop(motors);
    }

    private boolean stopMotors(List<LinearMotor> motors){
        Iterator<LinearMotor> motor_queue = motors.iterator();
        boolean isInUse = false;

        while(motor_queue.hasNext()){
            LinearMotor motor = motor_queue.next();
            int targ_pos = motor.getMotor().getTargetPosition();
            int current_pos = motor.getMotor().getCurrentPosition();
            if((motor.isLargeDesiredPos() && targ_pos < current_pos) || (!motor.isLargeDesiredPos() && targ_pos > current_pos)){
                motor.getMotor().setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                motor.getMotor().setPower(0);

                motor_queue.remove();
            }else isInUse = true;
        }
        return isInUse;
    }

    private void start(double power, double distance, LinearMotor motor){
        motor.setLargeDesiredPos(distance > motor.getMotor().getCurrentPosition());

        motor.getMotor().setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.getMotor().setTargetPosition((int)distance);
        motor.getMotor().setPower(power);
    }

    public static void run(double l_power, double r_power, LinearMotor[] motors){
        for(LinearMotor x : motors)
            switch(x.getCrossPosition()){
                case LEFT:
                    x.getMotor().setPower(l_power);
                    break;
                case RIGHT:
                    x.getMotor().setPower(r_power);
                    break;
            }
    }

    public static void stop(LinearMotor[] motors) {
        for (LinearMotor x : motors) x.getMotor().setPower(0);
    }


    protected void resetEncoders(LinearOpMode robot, LinearMotor[] motors) throws InterruptedException {
        for(LinearMotor x : motors) x.getMotor().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.idle();
        for(LinearMotor x : motors) x.getMotor().setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
}
