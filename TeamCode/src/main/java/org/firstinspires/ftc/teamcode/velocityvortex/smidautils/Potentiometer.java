package org.firstinspires.ftc.teamcode.velocityvortex.smidautils;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Nathan.Smith.19 on 1/11/2017.
 */
@Deprecated
public class Potentiometer {

    private static double arm_length = 9; //inches
    private Servo servo_arm;
    private AnalogInput potentiometer;

    public Potentiometer(AnalogInput potentiometer, Servo servo_arm){
        this.potentiometer = potentiometer;
        this.servo_arm = servo_arm;
    }

    public int getRotation(){
        return (int)Math.round((((potentiometer.getVoltage())/5) * 314) + 14.1); //why
    }

    public void lock(boolean lock){
        //servo_arm.
    }

    public Servo getArmServo(){
        return servo_arm;
    }
    public double getWalLDistance() {return arm_length * Math.sin(getRotation() * (Math.PI/180)); }

    public double getVoltage(){
        return potentiometer.getVoltage();
    }

    public AnalogInput getPotentiometer(){
        return this.potentiometer;
    }

}
