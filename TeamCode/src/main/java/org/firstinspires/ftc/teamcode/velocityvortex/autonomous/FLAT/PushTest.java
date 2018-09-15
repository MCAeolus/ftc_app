package org.firstinspires.ftc.teamcode.velocityvortex.autonomous.FLAT;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by Nathan.Smith.19 on 1/20/2017.
 */
@Autonomous(name="PUSH_TEST")@Disabled
public class PushTest extends LinearOpMode {

    private ElapsedTime TIME = new ElapsedTime();

    private CRServo servo;
    private OpticalDistanceSensor BDS;
    private double distance_threshold = 0.085;

    @Override
    public void runOpMode() throws InterruptedException {
        servo = hardwareMap.crservo.get("BCR");
        BDS = hardwareMap.opticalDistanceSensor.get("BDS");

        waitForStart();

        servo.setPower(-1);
        TIME.reset();
        while(BDS.getLightDetected() < distance_threshold && opModeIsActive() && TIME.milliseconds() < 4500){idle();}
        servo.setPower(0);
    }
}
