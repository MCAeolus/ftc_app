package org.firstinspires.ftc.teamcode.velocityvortex.autonomous.FLAT;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.velocityvortex.smidautils.GameSide;

/**
 * Created by Nathan.Smith.19 on 1/25/2017.
 */
@Autonomous(name="Gyro Autonomous[BLUE]")@Disabled
public class GyroAutonBlue extends LinearOpMode {

    ElapsedTime MAIN_RUNTIME =   new ElapsedTime(); //Main runtime, for drive and initialization time
    ElapsedTime SECOND_RUNTIME = new ElapsedTime(); //Secondary runtime, for holding (probably unnecessary)

    DcMotor    LEFT_DRIVE_MOTOR, RIGHT_DRIVE_MOTOR, //Drive train motors
                                       SHOOT_MOTOR, //Shoot mechanism motor
                                   BALL_LIFT_MOTOR; //Ball collector motor
    Servo                                BALL_HOLD; //Ball gate servo
    CRServo                           BEACON_PRESS; //Beacon presser servo
    ColorSensor                       COLOR_SENSOR; //Color sensor
    OpticalDistanceSensor               OPD_SENSOR,
                                        BPD_SENSOR; //Optical Distance Sensors
    ModernRoboticsI2cGyro              GYRO_SENSOR; //Gyro sensor
    DeviceInterfaceModule         INTERFACE_MODULE; //Device interface module
    private int                 CS_LED_CHANNEL = 5; //Port of color sensor LED in digital devices port

    private double           LIGHT_THRESHOLD = 0.2,
                         DISTANCE_THRESHOLD = 0.06;

    /**
     * Main method, which is executed by the robot controller for autonomous.
     * Terminates once all the instructions (labeled below) have finished,
     *                                                  or 30 seconds is up.
     *
     * @throws InterruptedException if 30 seconds occurs while the code is running
     *          and it gets cancelled, this is thrown for the robot controller to catch.
     */
    @Override
    public void runOpMode() throws InterruptedException {
        //Main timer, which determines time since a move(ex. drive() or hold()) has been run. Resetting to calculate time to initialize all the motors and the gyro.
        MAIN_RUNTIME.reset();

        //Drive train motors, which control robot movement on the field.
        LEFT_DRIVE_MOTOR = hardwareMap.dcMotor.get("LM");
        RIGHT_DRIVE_MOTOR = hardwareMap.dcMotor.get("RM");

        //Other motors, which control the shooter mechanism and the ball collector (or ball lift) of the robot.
        //SHOOT_MOTOR = hardwareMap.dcMotor.get("SM");
        BALL_LIFT_MOTOR = hardwareMap.dcMotor.get("BLM");

        //The ball 'gate' that stops balls from being released into the ball shooter.
        BALL_HOLD = hardwareMap.servo.get("BS");

        //The side plexi glass panel that is used to push the beacon buttons.
        BEACON_PRESS = hardwareMap.crservo.get("BCR");

        //Color sensor that is mounted on the beacon presser. Used to determine beacon color.
        COLOR_SENSOR = hardwareMap.colorSensor.get("CS");
        //Optical distance sensor is mounted bottom center of robot. Returns light reflected values of ground beneath.
        OPD_SENSOR = hardwareMap.opticalDistanceSensor.get("ODS");
        BPD_SENSOR = hardwareMap.opticalDistanceSensor.get("BDS");
        //Gyro sensor that is mounted in the center of the robot (probably hidden). Used to get the turn direction of the robot for accurate turning in autonomous.
        GYRO_SENSOR = (ModernRoboticsI2cGyro)hardwareMap.gyroSensor.get("GS");

        INTERFACE_MODULE = hardwareMap.deviceInterfaceModule.get("dim");
        INTERFACE_MODULE.setDigitalChannelMode(CS_LED_CHANNEL, DigitalChannelController.Mode.OUTPUT);

        INTERFACE_MODULE.setDigitalChannelState(CS_LED_CHANNEL, false); //disable color sensor LED

        //Can't rely on ambient light.
        OPD_SENSOR.enableLed(true);
        BPD_SENSOR.enableLed(false);

        //Setting some properties of the left and right motor
        LEFT_DRIVE_MOTOR.setMode(DcMotor.RunMode.RUN_USING_ENCODER); //Forcing motors to run with encoder, this helps manage how fast the motors run.
        RIGHT_DRIVE_MOTOR.setMode(DcMotor.RunMode.RUN_USING_ENCODER); //Ditto

        RIGHT_DRIVE_MOTOR.setDirection(DcMotorSimple.Direction.REVERSE); //Right motor is in opposite direction of left motor, our main axis.

        //Resetting the gyro sensor so that the starting robot position is '0'. This takes a while.
        GYRO_SENSOR.calibrate();

        //We must make sure that the gyro sensor has calibrated before starting the program due to it's necessity.
        //This ignores any calls from the drivers unless a stop is requested until it has finished.
        while(GYRO_SENSOR.isCalibrating() && !isStopRequested()){
            inputData("STATUS", "CALIBRATING GYRO SENSOR.", true); //send calibration message to remote phone drivers use.
            sleep(50); //wait 50 milliseconds before trying again.
        }

        BALL_HOLD.setPosition(1);

        inputData("STATUS","STARTUP COMPLETE IN " + MAIN_RUNTIME.milliseconds() + "ms.", true); //Informing drivers how long robot took to initialize.

        //waiting for drivers to press the run button. Gives information of robot rotation to inform drivers the angle the robot thinks it's facing.
        while(!isStarted() && !isStopRequested()){
            inputData("ROTATION", "Robot Heading: " + GYRO_SENSOR.getIntegratedZValue(), false);
            inputData("COLOR", "R:" + COLOR_SENSOR.red() + ", G:" + COLOR_SENSOR.green() + ", B:" + COLOR_SENSOR.blue(), false);
            inputData("LIGHT", OPD_SENSOR.getLightDetected() + "", false);
            inputData("BEACON DISTANCE", BPD_SENSOR.getLightDetected() + "", false);
            commitData();
        }

        //Gyro sensors use calculus to determine the theta (angular distance) of the robot.
        //Basically, this is important and should be reset for calculation accuracy.
        GYRO_SENSOR.resetZAxisIntegrator();

        /*------------------
          ROBOT INSTRUCTIONS
          ------------------*/

        drive(0.4, 1.55);
        turn(0.1, -80, 2);

        drive(0.4, 1.65);

        sleep(100);
        turn(0.1, -177, 2);


        findBeaconLine(-0.2);
        drive(-0.1,0.1);
        sleep(500);
        if(COLOR_SENSOR.blue() > COLOR_SENSOR.red())press();
        else{
            drive(0.15,0.75);
            press();
        }
        drive(-0.5,0.6);

        findBeaconLine(-0.3);
        drive(-0.1,0.2);

        sleep(500);
        if(COLOR_SENSOR.blue() > COLOR_SENSOR.red())press();
        else{
            drive(0.15,0.6);
            press();
        }
    }

    /**
     * The drive instruction. Robot will move for given time at given speed,
     * and will hold the given angle to make sure it does not get off track.
     *
     * UNCERTAINTY: +/- 0.5 degrees
     *
     * @param speed that the robot moves forwards at.
     * @param time that the robot moves for.
     * @param angle that the robot keeps.
     */
    @Deprecated
    private void drive(double speed, double time, int angle){
        double rightS, leftS; //right and left speed variable initialization.
        double steerForce, errorAngle; //steer force and error angle initialization.
        double max; //whether or not the maximum motor speed has been passed (-1 <= motor speed <= 1)
        time = time * 1000; //the given time converted to milliseconds.

        rightS = leftS = speed; //compact setter for the left and right motor speed.

        LEFT_DRIVE_MOTOR.setPower(leftS); //set initial power of the left and right motor
        RIGHT_DRIVE_MOTOR.setPower(rightS); //ditto

        MAIN_RUNTIME.reset(); //reset main timer.
        while(MAIN_RUNTIME.milliseconds() < time && opModeIsActive()){ //run while given time hasn't elapsed and the op mode is still running.
            errorAngle = getHeadError(angle);
            steerForce = getSteerForce(errorAngle, .05);

            if(speed < 0)steerForce=-steerForce;

            leftS = speed - steerForce;
            rightS = speed + steerForce;

            max = Math.max(Math.abs(leftS), Math.abs(rightS));
            if(max > 1.0){
                leftS  /= max;
                rightS /= max;
            }

            LEFT_DRIVE_MOTOR.setPower(leftS);
            RIGHT_DRIVE_MOTOR.setPower(rightS);
        }
        stopDriveMotors();
    }

    private void drive(double power, double time){
        LEFT_DRIVE_MOTOR.setPower(power);
        RIGHT_DRIVE_MOTOR.setPower(power);
        time = time * 1000;
        MAIN_RUNTIME.reset();
        while(MAIN_RUNTIME.milliseconds() < time && opModeIsActive()) idle();
        stopDriveMotors();
    }

    /**
     * The turn instruction. Robot will turn at given speed until it is at the given angle.
     * After this, holds the given angle for specified amount of time.
     *
     * UNCERTAINTY: +/- 0.5 degrees
     *
     * @param speed
     * @param angle
     * @param holdTime
     */
    private void turn(double speed, double angle, double holdTime){
        while(opModeIsActive() && !heading(speed, angle, 0.1)){idle();}
        if(holdTime > 0)hold(speed, angle, holdTime * 1000);
        else stopDriveMotors();
    }

    /**
     * The hold instruction. Robot will attempt to keep given angle for given time.
     *
     * UNCERTAINTY: +/- 0.5 degrees
     *
     * @param speed
     * @param angle
     * @param time
     */
    private void hold(double speed, double angle, double time){

        SECOND_RUNTIME.reset();
        while(opModeIsActive() && SECOND_RUNTIME.milliseconds() < time)
            heading(speed, angle, 0.1);
        stopDriveMotors();
    }

    /**
     * The heading instruction. Checks whether the robot is facing the given angle,
     *                              and if not turns at given speed to given angle.
     *
     * @param speed at which the motors turn as a primary base.
     * @param angle is the goal angle that the robot is to be facing.
     * @param turnCoeff is the 'force' or secondary speed base, which determines intensity of steering power.
     *
     * @return whether or not the robot is facing the correct direction.
     */
    private boolean heading(double speed, double angle, double turnCoeff){
        double error = getHeadError(angle);

        double steer,
               leftSpeed, rightSpeed;
        boolean onTarget = false;

        if(Math.abs(error) < 2){
            leftSpeed = 0;
            rightSpeed = 0;
            onTarget = true;
        } else {
            steer = getSteerForce(error, turnCoeff);
            rightSpeed = speed * steer;
            leftSpeed = -rightSpeed;
        }

        LEFT_DRIVE_MOTOR.setPower(leftSpeed);
        RIGHT_DRIVE_MOTOR.setPower(rightSpeed);

        inputData("HEADING","TARGET: " + angle + ", ERROR:" + error, true);

        return onTarget;
    }

    private void findBeaconLine(double speed){
        LEFT_DRIVE_MOTOR.setPower(speed);
        RIGHT_DRIVE_MOTOR.setPower(speed);

        while((OPD_SENSOR.getLightDetected() < LIGHT_THRESHOLD) && opModeIsActive()){idle();}
        stopDriveMotors();
    }

    private void shoot(double time, boolean reload){
        time *= 1000;
        SHOOT_MOTOR.setPower(1);
        SECOND_RUNTIME.reset();
        while((SECOND_RUNTIME.milliseconds() < time) && opModeIsActive()){idle();}
        SHOOT_MOTOR.setPower(0);
        if(reload)reload();
    }

    private void reload(){
        BALL_HOLD.setPosition(.4);
        SECOND_RUNTIME.reset();
        while((SECOND_RUNTIME.seconds() < 1) && opModeIsActive()){idle();}
        BALL_HOLD.setPosition(1);
    }

    private void beacon(double speed, double time, GameSide side){
        drive(speed, time);
        switch(side){
            case BLUE:
                if(COLOR_SENSOR.blue() > COLOR_SENSOR.red()) press();
                else{
                    drive(0.15, 0.75);
                    press();
                }
                break;
            case RED:
                if(COLOR_SENSOR.red() > COLOR_SENSOR.blue()) press();
                else{
                    drive(-0.15, 0.75);
                    press();
                }
                break;
        }
    }

    private void press(){
        BEACON_PRESS.setPower(-.5);
        SECOND_RUNTIME.reset();
        while((SECOND_RUNTIME.milliseconds() < (2.5 * 1000)) && opModeIsActive()){idle();}
        BEACON_PRESS.setPower(0);
        SECOND_RUNTIME.reset();
        while((SECOND_RUNTIME.milliseconds() < (1000)) && opModeIsActive()){idle();}
    }

    /**
     * The steering equation. This is the result robot speed given the robot error and turning coefficient.
     *
     * @param error in terms of degrees as to how far off the robot is from its goal angle.
     * @param COEFF is the secondary speed base of the robot to turn at.
     *
     * @return the force that the robot should turn with.
     */
    private double getSteerForce(double error, double COEFF){
        return Range.clip(error * COEFF, -1, 1);
    }

    /**
     * The heading equation. First figures the angle delta (target angle - current angle),
     * then normalizes to be within 180 degrees, as in the case of base robot rotation
     * all angles are within 180 degrees of the current robot angle.
     * (EX. 360 is also 0, 181 is also -179)
     *
     * @param angle is the ideal angle for the robot to turn to.
     *
     * @return how far off the robot is from target angle.
     */
    private double getHeadError(double angle){
        double robotError = angle - GYRO_SENSOR.getIntegratedZValue();

        while(robotError >= 180) robotError-=360;
        while(robotError <= -180) robotError+=360;

        return robotError;
    }


    /**
     * Nathan is lazy and this makes telemetry(information) a little bit easier to push to the driver phone.
     *
     * @param IN or the 'title' of the data.
     * @param OUT or the 'information' of the data.
     * @param PUSH or whether or not the phone should actually receive the info.
     */
    private void inputData(String IN, String OUT, boolean PUSH){
        telemetry.addData(IN, OUT);
        if(PUSH)commitData();
    }

    /**
     * (Very unnecessary, pretty much only renaming an already
     *  existing method in order to make the code look more uniform.)
     *
     * Pushes all data to the driver phone.
     */
    private void commitData(){
        telemetry.update();
    }

    /**
     * The stop drive motors helper instruction. Stops the drive motors,
     * and is only a quicker and cleaner way to perform the setPower(0) instructions.
     */
    private void stopDriveMotors(){
        LEFT_DRIVE_MOTOR.setPower(0);
        RIGHT_DRIVE_MOTOR.setPower(0);
    }
}
