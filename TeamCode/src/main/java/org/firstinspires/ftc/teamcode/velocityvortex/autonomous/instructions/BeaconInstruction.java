package org.firstinspires.ftc.teamcode.velocityvortex.autonomous.instructions;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.velocityvortex.autonomous.instructions.interfaces.IBeaconInstruction;
import org.firstinspires.ftc.teamcode.velocityvortex.smidautils.GameSide;
import org.firstinspires.ftc.teamcode.velocityvortex.smidautils.Instruction;
import org.firstinspires.ftc.teamcode.velocityvortex.smidautils.OmniMotor;

/**
 * Created by Nathan.Smith.19 on 9/14/2016.
 */
@Deprecated
public class BeaconInstruction extends Instruction implements IBeaconInstruction {

    private final double WHITE_THRESHOLD = .025, //apparently runs between .1 - .5 (darkest to lightest)
                         ALPHA_THRESHOLD = 3;
    private final double RIGHT_SERVO = .2D, LEFT_SERVO = .8D, STAND_SERVO = .46D;
    private ElapsedTime runtime = new ElapsedTime();

    public BeaconInstruction(GameSide side, ColorSensor c_sensor, OpticalDistanceSensor l_sensor, Servo arm_servo, OmniMotor[]motors, boolean reverse) {

        super("Beacon", side, c_sensor, l_sensor, arm_servo, motors, reverse);
    }

    @Override
    public void execute(OpMode robot) {
        GameSide side = (GameSide) super.getData().get(0);
        ColorSensor c_sensor = (ColorSensor) super.getData().get(1);
        OpticalDistanceSensor l_sensor = (OpticalDistanceSensor) super.getData().get(2);
        Servo arm_servo = (Servo) super.getData().get(3);
        OmniMotor[] motors = (OmniMotor[]) super.getData().get(4);
        boolean reverse = (boolean) super.getData().get(5);

        arm_servo.setPosition(STAND_SERVO);

        //Detect beacon color
        int red = c_sensor.red(), blue = c_sensor.blue(); //RIGHT SIDE

        if (red > blue && side == GameSide.RED || blue > red && side == GameSide.BLUE)
            arm_servo.setPosition(RIGHT_SERVO);
        else arm_servo.setPosition(LEFT_SERVO);

        //runtime.reset();
        //while(runtime.seconds() < 0.5)
        //    try {
        //        ((LinearOpMode)robot).idle();
        //    } catch (InterruptedException e) {}
    }

    private boolean checkAlpha(ColorSensor c){
        return c.alpha() < ALPHA_THRESHOLD;
    }

    @Override
    public void modify(GameSide side) {
        super.modify(side);
    }
}
