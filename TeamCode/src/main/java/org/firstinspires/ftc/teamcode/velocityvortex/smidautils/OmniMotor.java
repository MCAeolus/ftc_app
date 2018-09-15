package org.firstinspires.ftc.teamcode.velocityvortex.smidautils;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by Nathan.Smith.19 on 9/14/2016. This is created for use in
 * autonomous mode for a cross-positioned omni-wheel base.
 */
@Deprecated
public class OmniMotor {

    public final String NAME;
    private DcMotor MOTOR;
    private CrossPosition CROSS_POSITION;
    private boolean LARGE_DESIRED_POS = false;

    public OmniMotor(String NAME, CrossPosition CROSS_POSITION, OpMode ROBOT) {
        this.NAME = NAME;
        this.MOTOR = ROBOT.hardwareMap.dcMotor.get(NAME);
        this.CROSS_POSITION = CROSS_POSITION;
    }

    public DcMotor getMotor(){
        return this.MOTOR;
    }

    public CrossPosition getCrossPosition() { return this.CROSS_POSITION; }

    public boolean isLargeDesiredPos(){ return this.LARGE_DESIRED_POS; }

    public void setLargeDesiredPos(boolean largeDesiredPos) { this.LARGE_DESIRED_POS = largeDesiredPos; }
}
