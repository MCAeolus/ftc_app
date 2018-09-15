package org.firstinspires.ftc.teamcode.velocityvortex.autonomous.FLAT;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by Nathan.Smith.19 on 1/20/2017.
 */
@Autonomous(name="BACKUP")@Disabled
public class Blue1TIME extends LinearOpMode {

    private ElapsedTime TIME = new ElapsedTime();

    private double TICKS = 60;
    private DcMotor LEFT_MOTOR, RIGHT_MOTOR, SHOOT_MOTOR, LIFT_MOTOR;
    private Servo SHOOT_SERVO;

    @Override
    public void runOpMode() throws InterruptedException {
        LEFT_MOTOR = hardwareMap.dcMotor.get("LM");
        RIGHT_MOTOR = hardwareMap.dcMotor.get("RM");
        SHOOT_MOTOR = hardwareMap.dcMotor.get("SM");
        SHOOT_SERVO = hardwareMap.servo.get("BS");
        LIFT_MOTOR = hardwareMap.dcMotor.get("BLM");

        SHOOT_SERVO.setPosition(1);
        //resetEncoders();
        LEFT_MOTOR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RIGHT_MOTOR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        waitForStart();

        moveTime(0.5,-0.5,0.8 * 1000);

        TIME.reset();
        while(TIME.milliseconds() < 500 && opModeIsActive()){idle();}

        SHOOT_MOTOR.setPower(1);
        TIME.reset();
        while(TIME.seconds() < 1.5 && opModeIsActive()){idle();}
        SHOOT_MOTOR.setPower(0);
        SHOOT_SERVO.setPosition(.4);
        TIME.reset();
        while(TIME.seconds() < 1 && opModeIsActive()){idle();}
        SHOOT_SERVO.setPosition(1);
        SHOOT_MOTOR.setPower(1);
        TIME.reset();
        while(TIME.seconds() < 1.5 && opModeIsActive()){idle();}
        SHOOT_MOTOR.setPower(0);

        moveTime(0.5,0.5,920);
        LIFT_MOTOR.setPower(1);
        moveTime(-0.3,0.3,1.4*1000);
        LIFT_MOTOR.setPower(0);
        moveTime(-0.2,0.2,0.5 * 1000);
    }

    private void moveTime(double left_power, double right_power, double time){
        LEFT_MOTOR.setPower(left_power);
        RIGHT_MOTOR.setPower(right_power);

        TIME.reset();
        while(TIME.milliseconds() < time && opModeIsActive()){idle();}
        stopMotors();
    }

    private void resetEncoders(){
        LEFT_MOTOR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RIGHT_MOTOR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        idle();
        LEFT_MOTOR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        RIGHT_MOTOR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    private void moveInches(double left_power, double right_power, double left_inches, double right_inches){
        updateMotor(left_power, left_inches * TICKS, LEFT_MOTOR);
        updateMotor(right_power, right_inches * TICKS, RIGHT_MOTOR);
        while((LEFT_MOTOR.isBusy())
                        &&
                opModeIsActive()) {
            telemetry.addData("MOTOR", LEFT_MOTOR.getCurrentPosition());
            telemetry.update();
            idle();
        }
        stopMotors();
    }

    private void updateMotor(double power, double distance, DcMotor motor){
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setTargetPosition((int)distance);
        motor.setPower(power);
    }

    private void stopMotors(){
        LEFT_MOTOR.setPower(0);
        RIGHT_MOTOR.setPower(0);
    }
}
