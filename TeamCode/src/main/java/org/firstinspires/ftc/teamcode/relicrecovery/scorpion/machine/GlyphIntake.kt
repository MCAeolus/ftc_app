package org.firstinspires.ftc.teamcode.relicrecovery.scorpion.machine

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.robot.LinearRobot
import org.firstinspires.ftc.teamcode.relicrecovery.scorpion.robotmodes.MecanumRobot
import org.firstinspires.ftc.teamcode.common.util.LinearOnly

/**
 * Created by Nathan.Smith.19 on 10/19/2017.
 */
@LinearOnly
class GlyphIntake(val motorNames : Array<String>, val servoNames: Array<String>) : IMachine {


    //DOOR CONSTANTS
    enum class DoorState {
        PARTIAL, CLOSED, OPEN
    }
    val DOOR_PARTIAL = 0.8
    val DOOR_CLOSED = 0.0
    val DOOR_OPEN = 1.0

    lateinit var CONVEYOR_L : DcMotor
    lateinit var CONVEYOR_R : DcMotor
    lateinit var LIFT : DcMotor
    lateinit var LIFT2 : DcMotor

    var islifting = false

    lateinit var DOOR_L : Servo
    lateinit var DOOR_R : Servo
    lateinit var DROP_PLATE : Servo

    lateinit var KICK : Servo

    lateinit var ROBOT : LinearRobot

    lateinit var TIMER : ElapsedTime

    var GLYPH_POSITION = MecanumRobot.GlyphPosition.OUT

    override fun init(robot: IRobot) {
        ROBOT = robot as LinearRobot

        CONVEYOR_L = robot.hardwareMap.get(DcMotor::class.java, motorNames[0])
        CONVEYOR_R = robot.hardwareMap.get(DcMotor::class.java, motorNames[1])

        LIFT = robot.hardwareMap.get(DcMotor::class.java, motorNames[2])
        LIFT2 = robot.hardwareMap.get(DcMotor::class.java, motorNames[3])
        LIFT2.direction = DcMotorSimple.Direction.REVERSE
        LIFT.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        LIFT2.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        DOOR_L = robot.hardwareMap.get(Servo::class.java, servoNames[0])
        DOOR_R = robot.hardwareMap.get(Servo::class.java, servoNames[1])
        DOOR_R.direction = Servo.Direction.REVERSE

        DROP_PLATE = robot.hardwareMap.get(Servo::class.java, servoNames[2])

        KICK = robot.hardwareMap.get(Servo::class.java, servoNames[3])
        KICK.position = 0.05

        TIMER = ElapsedTime()

    }


    override fun stop() {
        runConveyors(DoorState.CLOSED, false)
    }

    fun setDoorsManual(L : Double, R : Double){
        DOOR_L.position = L * DOOR_OPEN
        DOOR_R.position = R * DOOR_OPEN
    }

    fun setDoors(state : DoorState){
        when (state) {
            DoorState.OPEN -> {
                DOOR_L.position = DOOR_OPEN
                DOOR_R.position = DOOR_OPEN
            }
            DoorState.CLOSED -> {
                DOOR_L.position = DOOR_CLOSED
                DOOR_R.position = DOOR_CLOSED
            }
            DoorState.PARTIAL -> {
                DOOR_L.position = DOOR_PARTIAL
                DOOR_R.position = DOOR_PARTIAL
            }
        }
    }

    fun runConveyors(state : DoorState, running : Boolean){
        if(running && state == DoorState.OPEN){
            CONVEYOR_L.power = 0.6
            CONVEYOR_R.power = 0.6
        }else if(running && state == DoorState.CLOSED) {
            CONVEYOR_L.power = -0.6
            CONVEYOR_R.power = -0.6
        }else{
            CONVEYOR_L.power = 0.0
            CONVEYOR_R.power = 0.0
        }
    }

    fun drop(dr : Boolean){
        DROP_PLATE.position = if(dr) 0.45 else 0.0
    }

    fun kick(dr : Boolean){
        KICK.position = if(dr) 1.0 else 0.05
    }

    fun lift(dr : Boolean, br : Boolean, rn : Boolean) {
        if(br) {
            if(!rn)setDoors(DoorState.PARTIAL)

            if (dr) {
                LIFT.power = 1.0 //left
                LIFT2.power = 1.0 //right
            } else {
                LIFT.power = -1.0
                LIFT2.power = -1.0
            }
        }else{
            LIFT.power = 0.0
            LIFT2.power = 0.0
        }

    }

    fun isLifting() = islifting

}