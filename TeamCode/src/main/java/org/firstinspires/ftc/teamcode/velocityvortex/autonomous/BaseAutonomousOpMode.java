package org.firstinspires.ftc.teamcode.velocityvortex.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.velocityvortex.smidautils.LinearMotor;
import org.firstinspires.ftc.teamcode.velocityvortex.smidautils.Potentiometer;
import org.firstinspires.ftc.teamcode.velocityvortex.smidautils.SidePosition;
import org.firstinspires.ftc.teamcode.velocityvortex.teleop.TiltedReference;

/**
 * Created by Nathan.Smith.19 on 9/14/2016.
 */
@Autonomous(name = "BaseAuton", group = "default")
@Disabled@Deprecated
public class BaseAutonomousOpMode extends LinearOpMode {

    protected LinearMotor[] motors;
    protected CRServo arm_servo;
    protected Potentiometer potentiometer;
    protected OpticalDistanceSensor opticalDistanceSensor;
    protected ColorSensor colorSensor;
    protected DcMotor shoot_motor;
    protected Servo shoot_servo;
    @Override
    public void runOpMode() throws InterruptedException {

        motors = new LinearMotor[2];

        motors[0] = new LinearMotor("LM", SidePosition.LEFT, this);
        motors[1] = new LinearMotor("RM", SidePosition.RIGHT, this);

        arm_servo = hardwareMap.crservo.get(TiltedReference.ARM_SERVO);
        potentiometer = new Potentiometer(hardwareMap.analogInput.get("pent"), hardwareMap.servo.get("pent_arm"));
        opticalDistanceSensor = hardwareMap.opticalDistanceSensor.get("opt");
        //colorSensor = hardwareMap.colorSensor.get("color");

        shoot_motor = hardwareMap.dcMotor.get("shoot_m");
        shoot_servo = hardwareMap.servo.get("shoot_s");
        //shoot_motor.set
        shoot_servo.setPosition(.5);
        resetEncoders(motors);
    }



    protected void resetEncoders(LinearMotor[] motors) throws InterruptedException {
        for(LinearMotor x : motors) x.getMotor().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        idle();
        for(LinearMotor x : motors) x.getMotor().setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}
