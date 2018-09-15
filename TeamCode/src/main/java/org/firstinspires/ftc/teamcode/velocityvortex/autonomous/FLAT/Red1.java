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
@Autonomous(name="RED1")@Disabled
public class Red1 extends LinearOpMode {

    private ElapsedTime TIME = new ElapsedTime();

    private double TICKS = 60;
    private DcMotor LEFT_MOTOR, RIGHT_MOTOR, SHOOT_MOTOR;
    private Servo SHOOT_SERVO;

    @Override
    public void runOpMode() throws InterruptedException {
        LEFT_MOTOR = hardwareMap.dcMotor.get("LM");
        RIGHT_MOTOR = hardwareMap.dcMotor.get("RM");
        SHOOT_MOTOR = hardwareMap.dcMotor.get("shoot_m");
        SHOOT_SERVO = hardwareMap.servo.get("shoot_s");

        SHOOT_SERVO.setPosition(.5);

        waitForStart();

        moveInches(1,-1,40,40);

        SHOOT_MOTOR.setPower(1);
        TIME.reset();
        while(TIME.seconds() < 1 && opModeIsActive()){idle();}
        SHOOT_MOTOR.setPower(0);
        SHOOT_SERVO.setPosition(0);
        TIME.reset();
        while(TIME.seconds() < 1 && opModeIsActive()){idle();}
        SHOOT_SERVO.setPosition(.5);
        SHOOT_MOTOR.setPower(1);
        TIME.reset();
        while(TIME.seconds() < 1 && opModeIsActive()){idle();}
        SHOOT_MOTOR.setPower(0);
    }

    private void moveInches(double left_power, double right_power, double left_inches, double right_inches){
        updateMotor(left_power, left_inches * TICKS, LEFT_MOTOR);
        updateMotor(right_power, right_inches * TICKS, RIGHT_MOTOR);
    }

    private void updateMotor(double power, double distance, DcMotor motor){
        motor.setPower(power);
        motor.setTargetPosition((int)distance);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
}

