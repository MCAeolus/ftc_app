package org.firstinspires.ftc.teamcode.common.drivetrain.util

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.relicrecovery.scorpion.autonomous.AutonomousBase
import org.firstinspires.ftc.teamcode.common.drivetrain.IDriveTrain
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.robot.LinearRobot

/**
 * Created by Nathan.Smith.19 on 1/10/2018.
 */
class EncoderAttachment(val DRIVE : IDriveTrain) {

    val ENCODER_TICKS = 538 // after 19.2:1 motorbox gear reduction
    val WHEEL_DIAMETER = 4 // inches

    val TICKS_PER_INCH = (ENCODER_TICKS / (WHEEL_DIAMETER * Math.PI))

    val time = ElapsedTime()

    var ROBOT : LinearRobot? = null

    fun init(robot : IRobot) {
        ROBOT = if(robot is LinearRobot)robot else null
        setRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER)
        setRunMode(DcMotor.RunMode.RUN_USING_ENCODER)
    }

    fun runByEncoder(x : Double, y : Double, r : Double, p : Double = 1.0, distance : Double, dr : AutonomousBase, time : Float) { //distance in inches

        setRunMode(DcMotor.RunMode.RUN_TO_POSITION)

        val direction = dr.IMU.XYZ().thirdAngle

        val drive_by_ticks = TICKS_PER_INCH * distance

        val pwr = Math.hypot(x, y)
        val angle = Math.atan2(y, x) - (Math.PI / 4)

        val FL = pwr * Math.cos(angle) + r
        val FR = pwr * Math.sin(angle) - r
        val BL = pwr * Math.sin(angle) + r
        val BR = pwr * Math.cos(angle) - r

        DRIVE.motorMap()["FL"]!!.targetPosition = DRIVE.motorMap()["FL"]!!.currentPosition +
                Math.floor((if(FL < 0)-1 else if(FL == 0.0)0 else 1) * drive_by_ticks).toInt()
        DRIVE.motorMap()["FR"]!!.targetPosition = DRIVE.motorMap()["FR"]!!.currentPosition +
                Math.floor((if(FR < 0)-1 else if(FR == 0.0)0 else 1) * drive_by_ticks).toInt()
        DRIVE.motorMap()["BL"]!!.targetPosition = DRIVE.motorMap()["BL"]!!.currentPosition +
                Math.floor((if(BL < 0)-1 else if(BL == 0.0)0 else 1) * drive_by_ticks).toInt()
        DRIVE.motorMap()["BR"]!!.targetPosition = DRIVE.motorMap()["BR"]!!.currentPosition +
                Math.floor((if(BR < 0)-1 else if(BR == 0.0)0 else 1) * drive_by_ticks).toInt()

        powerSet(FL, FR, BL, BR)

        this.time.reset()

        while(motorsAreBusy() && if(ROBOT == null)true else ROBOT!!.opModeIsActive() && this.time.time() < time){
            if(Math.abs(direction) > 2) {
                dr.reposition(direction)
                powerSet(FL, FR, BL, BR)
            }

        }

        DRIVE.stop()

        setRunMode(DcMotor.RunMode.RUN_USING_ENCODER)
    }

    fun setRunMode(mode : DcMotor.RunMode) {
        for(motor in DRIVE.motorList())
            motor.mode = mode
    }

    fun motorsAreBusy() : Boolean {
        for(motor in DRIVE.motorList())
            if(Math.abs(motor.targetPosition - motor.currentPosition) > 20) return true
        return false
    }

    fun powerSet(fl : Double, fr: Double, bl : Double, br : Double) {
        DRIVE.motorMap()["FL"]!!.power = fl
        DRIVE.motorMap()["FR"]!!.power = fr
        DRIVE.motorMap()["BL"]!!.power = bl
        DRIVE.motorMap()["BR"]!!.power = br
    }

}