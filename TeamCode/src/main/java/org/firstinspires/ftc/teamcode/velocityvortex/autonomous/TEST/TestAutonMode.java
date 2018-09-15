package org.firstinspires.ftc.teamcode.velocityvortex.autonomous.TEST;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.velocityvortex.autonomous.BaseAutonomousOpMode;
import org.firstinspires.ftc.teamcode.velocityvortex.autonomous.instructions.beacon.FindLineInstruction;
import org.firstinspires.ftc.teamcode.velocityvortex.autonomous.instructions.beacon.RunServoInstruction;
import org.firstinspires.ftc.teamcode.velocityvortex.autonomous.instructions.sixwheel_drive.MoveInstruction;
import org.firstinspires.ftc.teamcode.velocityvortex.smidautils.GameSide;

import static org.firstinspires.ftc.teamcode.velocityvortex.smidautils.GameSide.BLUE;

/**
 * Created by Nathan.Smith.19 on 1/12/2017.
 */

@Autonomous(name="TESTAUTON")@Disabled@Deprecated
public class TestAutonMode extends BaseAutonomousOpMode {

    ElapsedTime run = new ElapsedTime();

    @Override
    public void runOpMode() throws InterruptedException {
        super.runOpMode();
        waitForStart();
        new FindLineInstruction(-0.3, super.opticalDistanceSensor, super.potentiometer, super.motors).execute(this);

        //new MoveInstruction(-0.2, -0.2, -1, -1, motors).execute(this);

        quickColor(BLUE);

        this.telemetry.addData("X","MOVE");
        this.telemetry.update();
        //new MoveInstruction(-0.5, -0.5, 4, 4, super.motors).execute(this);
        while(opModeIsActive()){idle();}
    }

    private void quickColor(GameSide side){
        int red = super.colorSensor.red(), blue = super.colorSensor.blue();

        RunServoInstruction runs = new RunServoInstruction(super.arm_servo, -.3, 1);

        switch(side){
            case BLUE:
                if(blue > red) {
                    telemetry.addData("y","blue1");
                    runs.execute(this);
                }else{
                    telemetry.addData("y","blue2");
                    new MoveInstruction(0.2, 0.2, .3, .3, motors).execute(this);
                    runs.execute(this);
                }
                break;
            case RED:
                if(red > blue) {
                    telemetry.addData("y","red1");
                    runs.execute(this);
                }else{
                    telemetry.addData("y","red2");
                    new MoveInstruction(0.2, 0.2, .3, .3, motors).execute(this);
                    runs.execute(this);
                }
                break;
        }
        telemetry.update();
        arm_servo.setPower(0);
    }
}
