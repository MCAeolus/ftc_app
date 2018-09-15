package org.firstinspires.ftc.teamcode.velocityvortex.smidautils;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by Nathan.Smith.19 on 9/14/2016. This is created for use in
 * autonomous mode for a cross-positioned omni-wheel base.
 */
@Deprecated
public class LinearMotor {

    public final String NAME;
    private DcMotor MOTOR;
    private SidePosition SIDE_POSITION;
    private boolean LARGE_DESIRED_POS = false;

    public LinearMotor(String NAME, SidePosition SIDE_POSITION, OpMode ROBOT){
        this.NAME = NAME;
        this.MOTOR = ROBOT.hardwareMap.dcMotor.get(NAME);
        this.SIDE_POSITION = SIDE_POSITION;
    }

    public double calculate(double y_power, double rot){
        switch(SIDE_POSITION){
            case LEFT:
                return y_power - rot;
            case RIGHT:
                return y_power + rot;
        }
        return 0;
    }

    public DcMotor getMotor(){
        return this.MOTOR;
    }

    public SidePosition getCrossPosition() { return this.SIDE_POSITION; }

    public boolean isLargeDesiredPos(){ return this.LARGE_DESIRED_POS; }

    public void setLargeDesiredPos(boolean largeDesiredPos) { this.LARGE_DESIRED_POS = largeDesiredPos; }
}
