package org.firstinspires.ftc.teamcode.relicrecovery.argus.machine

import android.webkit.DownloadListener
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.relicrecovery.scorpion.machine.IMachine

/**
 * Created by Nathan.Smith.19 on 1/19/2018.
 */
class GlyphThroughput : IMachine {

    enum class FlywheelDirection { INTAKE, OUTTAKE }
    enum class IntakePosition { UP, DOWN }
    enum class BlockPosition { UP, DOWN }
    enum class LiftPosition(val encoderPos : Int) {
        UP(-1428),
        DOWN(0)
    }
    enum class LiftRun() {
        UP,
        DOWN
    }


    //INTAKE
    
    lateinit var flyRunL : DcMotor
    lateinit var flyRunR : DcMotor
    
    lateinit var intakePull : DcMotor
    
    //OUTTAKE
    
    lateinit var blockInR : Servo
    lateinit var blockInL : Servo

    lateinit var blockStopper : Servo

    //LIFT
    
    lateinit var liftPull : DcMotor
    var liftPos = LiftPosition.DOWN

    val timer = ElapsedTime()

    override fun init(robot: IRobot) {
        
        //INTAKE
        
        flyRunL = robot.opMode().hardwareMap.get(DcMotor::class.java, "FLY_L")
        flyRunR = robot.opMode().hardwareMap.get(DcMotor::class.java, "FLY_R")
        flyRunR.direction = DcMotorSimple.Direction.REVERSE
        flyRunL.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        flyRunL.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        flyRunL.mode = DcMotor.RunMode.RUN_USING_ENCODER
        flyRunR.mode = DcMotor.RunMode.RUN_USING_ENCODER


        intakePull = robot.opMode().hardwareMap.get(DcMotor::class.java, "INT_P")
        intakePull.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        intakePull.mode = DcMotor.RunMode.RUN_USING_ENCODER
        intakePull.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT

        //OUTTAKE

        blockInL = robot.opMode().hardwareMap.get(Servo::class.java, "IN_L")
        blockInR = robot.opMode().hardwareMap.get(Servo::class.java, "IN_R")
        blockInR.direction = Servo.Direction.REVERSE

        blockStopper = robot.opMode().hardwareMap.get(Servo::class.java, "BS")

        //LIFT
        
        liftPull = robot.opMode().hardwareMap.get(DcMotor::class.java, "LIFT")
        liftPull.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        liftPull.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        liftPull.mode = DcMotor.RunMode.RUN_USING_ENCODER
        
    }

    fun runFlywheels(direction : FlywheelDirection) {
        when(direction){
            FlywheelDirection.INTAKE -> {
                flyRunL.power = 1.0
                flyRunR.power = 1.0
            }
            FlywheelDirection.OUTTAKE -> {
                flyRunL.power = -1.0
                flyRunR.power = -1.0
            }
        }
    }

    fun stopFlywheels() {
        flyRunL.power = 0.0
        flyRunR.power = 0.0
    }

    fun setIntakePosition(position : IntakePosition) {
        when(position) {
            IntakePosition.UP -> {
                intakePull.mode = DcMotor.RunMode.RUN_TO_POSITION
                intakePull.targetPosition = 0
                intakePull.power = 0.5
            }
            IntakePosition.DOWN -> {
                intakePull.mode = DcMotor.RunMode.RUN_TO_POSITION
                intakePull.targetPosition = 500 //TODO find proper position
                intakePull.power = -0.5
            }
        }
    }

    fun stopHoldingIntake() {
        if(intakePull.isBusy){
            intakePull.mode = DcMotor.RunMode.RUN_USING_ENCODER
            intakePull.power = 0.0
        }
    }

    fun setBlockPosition(position : BlockPosition) {
        when(position) {
            BlockPosition.UP -> {
                blockInL.position = 0.55
                blockInR.position = 0.55
            }
            BlockPosition.DOWN -> {
                blockInL.position = 0.0
                blockInR.position = 0.0
            }
        }
    }

    fun setStopper(position : BlockPosition) {
        when(position) {
            BlockPosition.UP -> blockStopper.position = 0.7
            BlockPosition.DOWN -> blockStopper.position = 0.3
        }
    }

    @Synchronized
    fun expand_throughtake() {
        setBlockPosition(BlockPosition.UP)
        hold(400)
        setIntakePosition(IntakePosition.DOWN)
        hold(400)
        setBlockPosition(BlockPosition.DOWN)
        hold(1000)
        intakePull.mode = DcMotor.RunMode.RUN_USING_ENCODER
        intakePull.power = 0.0
    }

    fun setLiftPosition(direction : LiftPosition) {
        if(direction != liftPos){
            liftPull.mode = DcMotor.RunMode.RUN_TO_POSITION
            liftPos = direction
            liftPull.power = 0.6
            liftPull.targetPosition = direction.encoderPos

            while(liftPull.isBusy){}

            liftPull.power = 0.0
            liftPull.mode = DcMotor.RunMode.RUN_USING_ENCODER
        }
    }

    fun setLiftPosition_UNHANDLED(direction : LiftPosition) {
        if(direction != liftPos){
            liftPull.mode = DcMotor.RunMode.RUN_TO_POSITION
            liftPos = direction
            liftPull.power = 0.6
            liftPull.targetPosition = direction.encoderPos
        }
    }

    fun runLift_BACKUP(direction : LiftRun, running : Boolean) {
        if(running) {
            if(direction == LiftRun.UP) liftPull.power = 0.6
            else liftPull.power = -0.6
        }else liftPull.power = 0.0
    }

    override fun stop() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @Synchronized
    fun hold(millis : Long) {
        timer.reset()
        while(timer.milliseconds() < millis){ }
    }
}