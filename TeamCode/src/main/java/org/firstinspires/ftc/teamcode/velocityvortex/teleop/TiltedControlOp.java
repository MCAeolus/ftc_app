package org.firstinspires.ftc.teamcode.velocityvortex.teleop;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by Nathan.Smith.19 on 10/3/2016.
 */
@Disabled
@TeleOp(name = TiltedReference.OPMODE_NAME, group = "DEFAULT")
public class  TiltedControlOp extends OpMode{

    private ElapsedTime runtime = new ElapsedTime();

    private DcMotor LM, RM,
                    lift_motor, shoot_motor,
                    tele_motor;
    private Servo shoot_servo;
    private CRServo arm_servo;
    private double L_Y, R_Y,
                   LM_POWER, RM_POWER;
    private boolean ltrig1_pressed = false, rtrig1_pressed = false,
                    RB1_wasPressed = false, RB1_toggle = false, //right bumper toggle to half speed scaling
                    LB1_wasPressed = false, LB1_toggle = false;
    private OpticalDistanceSensor BDS;
    private boolean last_x1 = false;
    private TouchSensor TS;
    private double DISTANCE_THRESHOLD = 0.06;
    @Override
    public void init() {
        /* GET MOTORS */
        LM = hardwareMap.dcMotor.get(TiltedReference.LM_WHEEL);
        RM = hardwareMap.dcMotor.get(TiltedReference.RM_WHEEL);
        RM.setDirection(DcMotorSimple.Direction.REVERSE);
        //G_S = (ModernRoboticsI2cGyro)hardwareMap.gyroSensor.get("GS");

        lift_motor = hardwareMap.dcMotor.get(TiltedReference.LIFT_MOTOR);
        shoot_motor = hardwareMap.dcMotor.get(TiltedReference.SHOOT_MOTOR);
        //tele_motor = hardwareMap.dcMotor.get("tele_motor");

        TS = hardwareMap.touchSensor.get("TS");
        /* GET ARM SERVO */
        shoot_servo = hardwareMap.servo.get(TiltedReference.SHOOT_SERVO);

        arm_servo = hardwareMap.crservo.get(TiltedReference.ARM_SERVO);

        BDS = hardwareMap.opticalDistanceSensor.get("BDS");

        /* INITIALIZE ARM SERVO POSITION */
        //arm_servo.setPosition(TiltedOmniReference.ARM_SERVO_RESTPOS);

        //potentiometer = new Potentiometer(hardwareMap.analogInput.get("pent"));

        arm_servo.setPower(0);
        shoot_servo.setPosition(1);

        //G_S.calibrate();
        //while(G_S.isCalibrating())
        //
        //    telemetry.addData("Gyro", "GYRO SENSOR CALIBRATING");
        //G_S.resetZAxisIntegrator();
    }

    @Override
    public void loop() {
        /* GET USER INPUT AND UPDATE TELEMETRY */
        updatePowers(); //movement power var update
        telemetry();

        /* MOVEMENT (controller 1) */
        updateMotors();

        /* LIFT MOTOR (controller 1) */
        updateBallLiftMotor();

        /* SHOOT MOTOR (controller 2) */
        updateShootMotor();

        /* SHOOT SERVO (controller 2) */
        updateShootServo();

        /* TELESCOPIC MOTOR (controller 2) */
        //updateTelescopicMotor();

        updateArmServo();
    }

    private void updateArmServo(){
        if(gamepad1.b)arm_servo.setPower(-.5);
        else arm_servo.setPower(0);
    }

    private void updatePowers(){ /* CONTROLLER 1 */
        L_Y = gamepad1.left_stick_y;
        R_Y = gamepad1.right_stick_y;

        if(gamepad1.right_bumper && !RB1_wasPressed){
            RB1_toggle = !RB1_toggle;
            RB1_wasPressed = true;
            if(LB1_toggle){
                LB1_toggle = false;
                LB1_wasPressed = false;
            }
        }else if(gamepad1.left_bumper && !LB1_wasPressed){
            LB1_toggle = !LB1_toggle;
            LB1_wasPressed = true;
            if(RB1_toggle){
                RB1_toggle = false;
                RB1_wasPressed = false;
            }
        }
        else if(!gamepad1.right_bumper && RB1_wasPressed) RB1_wasPressed = false;
        else if(!gamepad1.left_bumper && LB1_wasPressed) LB1_wasPressed = false;
    }

    private void updateMotors() {
        LM_POWER = Range.clip(-L_Y, -1, 1);
        RM_POWER = Range.clip(-R_Y, -1, 1);

        double POWERMOD = (RB1_toggle ? 0.33 : LB1_toggle ? 0.66 : 1);
        LM.setPower(LM_POWER * POWERMOD);
        RM.setPower(RM_POWER * POWERMOD);
    }

    private void updateTelescopicMotor() { /* CONTROLLER 2 */
        if(gamepad2.b)tele_motor.setPower(.75);
        else tele_motor.setPower(0);
    }

    private void updateShootMotor() { /* CONTROLLER 2 */
        if(gamepad2.right_bumper) shoot_motor.setPower(1);
        else shoot_motor.setPower(0);
    }

    private void updateShootServo(){
        if(gamepad2.left_bumper) shoot_servo.setPosition(.4);
        else shoot_servo.setPosition(1);
    }

    private void updateBallLiftMotor() { /* CONTROLLER 1 */
        if(gamepad1.left_trigger > 0 && !rtrig1_pressed){
            lift_motor.setPower(gamepad1.left_trigger);
            ltrig1_pressed = true;
        }else if(gamepad1.left_trigger == 0 && ltrig1_pressed){
            lift_motor.setPower(0);
            ltrig1_pressed = false;
        }

        if(gamepad1.right_trigger > 0 && !ltrig1_pressed){
            lift_motor.setPower(-gamepad1.right_trigger);
            rtrig1_pressed = true;
        }else if(gamepad1.right_trigger == 0 && rtrig1_pressed){
            lift_motor.setPower(0);
            rtrig1_pressed = false;
        }
    }

    private void telemetry(){
        telemetry.addData("mot_ENC", LM.getCurrentPosition());
        telemetry.addData("mot_ENC2", RM.getCurrentPosition());
        telemetry.addData("Beacon Distance", BDS.getLightDetected());
        telemetry.addData("TOUCH", TS.isPressed() + " V:" + TS.getValue());
    }
}
