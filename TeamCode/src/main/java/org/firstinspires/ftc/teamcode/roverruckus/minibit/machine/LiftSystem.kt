package org.firstinspires.ftc.teamcode.roverruckus.minibit.machine

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine
import org.firstinspires.ftc.teamcode.roverruckus.minibit.HARDWARENAMES_MINIBOT

class LiftSystem() : IMachine {

    enum class LiftPosition(val pos : Int) {
        LIFTED(0),
        LOWERED(1700)
    }

    enum class HookPosition(val pos : Double) {
        HOOKED(0.5),
        UNHOOKED(0.0)
    }

    lateinit var lift_motorL : DcMotor
    lateinit var lift_motorR: DcMotor
    lateinit var hook_left : Servo
    lateinit var hook_right : Servo

    var speed = 1.0

    override fun init(robot: IRobot) {
        lift_motorL = robot.opMode().hardwareMap.get(DcMotor::class.java, HARDWARENAMES_MINIBOT.LIFT_MOTOR_LEFT.v)
        lift_motorR = robot.opMode().hardwareMap.get(DcMotor::class.java, HARDWARENAMES_MINIBOT.LIFT_MOTOR_RIGHT.v)
        hook_left = robot.opMode().hardwareMap.get(Servo::class.java, HARDWARENAMES_MINIBOT.HOOK_SERVO_LEFT.v)
        hook_right = robot.opMode().hardwareMap.get(Servo::class.java, HARDWARENAMES_MINIBOT.HOOK_SERVO_RIGHT.v)

        lift_motorL.direction = DcMotorSimple.Direction.REVERSE

        lift_motorL.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        lift_motorR.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        lift_motorL.mode = DcMotor.RunMode.RUN_USING_ENCODER
        lift_motorR.mode = DcMotor.RunMode.RUN_USING_ENCODER


    }

    override fun stop() {
        powerSet(0.0, 0.0)
    }

    fun setLiftPosition(position : LiftPosition) {
        lift_motorL.mode = DcMotor.RunMode.RUN_TO_POSITION
        lift_motorR.mode = DcMotor.RunMode.RUN_TO_POSITION
        when(position) {
            LiftPosition.LIFTED -> {
                //setHookPosition(HookPosition.UNHOOKED)
                lift_motorL.targetPosition = position.pos
                lift_motorR.targetPosition = position.pos
                lift_motorL.power = -speed
                lift_motorR.power = -speed
                //setHookPosition(HookPosition.HOOKED)
            }
            LiftPosition.LOWERED -> {
                //setHookPosition(HookPosition.UNHOOKED)
                lift_motorL.targetPosition = position.pos
                lift_motorR.targetPosition = position.pos
                lift_motorL.power = speed
                lift_motorR.power = speed
            }
        }

    }

    fun setHookPosition(position : HookPosition) {
        hook_left.position = position.pos
        hook_right.position = position.pos
    }

    fun manual_run(power : Double){
        lift_motorL.mode = DcMotor.RunMode.RUN_USING_ENCODER
        lift_motorR.mode = DcMotor.RunMode.RUN_USING_ENCODER
        powerSet(power, power)
    }

    fun isLifting() : Boolean {
        return lift_motorL.isBusy || lift_motorR.isBusy
    }

    fun powerSet(left : Double, right : Double){
        lift_motorL.power = left
        lift_motorR.power = right
    }
}