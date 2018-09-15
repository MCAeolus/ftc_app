package org.firstinspires.ftc.teamcode.velocityvortex.teleop;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;

/**
 * Created by Kirsten.Williams.19 on 1/16/2017.
 */

@TeleOp(name="TESTING")
@Disabled@Deprecated
public class TestOp extends OpMode {

    private CRServo s;

    double x = 0;

    @Override
    public void init() {
        s=hardwareMap.crservo.get("arm_servo");
    }

    @Override
    public void loop() {
        if(gamepad1.dpad_up) x+= .1;
        else if(gamepad1.dpad_down) x-= .1;
        s.setPower(x);
        telemetry.addData("x",x);
    }
}
