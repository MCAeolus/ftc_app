package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.velocityvortex.smidautils.LinearMotor

/**
 * Created by Nathan.Smith.19 on 11/1/2017.
 */
//@TeleOp(name="O.W.L.", group="things that go 'flap'")
class Owl : LinearOpMode() {

    lateinit var lW : DcMotor
    lateinit var rW : DcMotor

    var firstRun = true

    override fun runOpMode() {
        val timer = ElapsedTime()
        var forwards = true
        var old_forwards = forwards
        val pulses = 500
        val power = 0.2

        //lW = hardwareMap.get(DcMotor::class.java, "LW")
        //rW = hardwareMap.get(DcMotor::class.java, "RW")

        lW.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        rW.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER

        telemetry.addData("Please give the owl time to rest.","")
        telemetry.addData("(the encoders need to reset first)","")
        telemetry.update()

        waitForStart()

        lW.mode = DcMotor.RunMode.RUN_USING_ENCODER
        rW.mode = DcMotor.RunMode.RUN_USING_ENCODER
        lW.direction = DcMotorSimple.Direction.REVERSE


        while(opModeIsActive()){
            if(timer.time() < 2){
                telemetry.addData("Time waiting: ", "${timer.time()}")
            }
            else{
                telemetry.addData("Moving: ", "forwards=$forwards")
                lW.mode = DcMotor.RunMode.RUN_TO_POSITION
                rW.mode = DcMotor.RunMode.RUN_TO_POSITION

                rW.targetPosition = if(forwards)pulses else 0
                rW.power = if(forwards)power else -power

                lW.targetPosition = if(forwards)pulses else 0
                lW.power = if(forwards)power else -power

                forwards = !forwards
                timer.reset()
            }
            telemetry.update()
        }
    }

    private fun isDone(motor : DcMotor, forwards : Boolean) =
            !((forwards && motor.targetPosition < motor.currentPosition) || (!forwards && motor.targetPosition > motor.currentPosition))

    private fun stopMotors(motors: List<LinearMotor>): Boolean {
        val motor_queue = motors.iterator()
        var isInUse = false

        while (motor_queue.hasNext()) {
            val motor = motor_queue.next()
            val targ_pos = motor.motor.targetPosition
            val current_pos = motor.motor.currentPosition
            if (motor.isLargeDesiredPos && targ_pos < current_pos || !motor.isLargeDesiredPos && targ_pos > current_pos) {
                motor.motor.mode = DcMotor.RunMode.RUN_USING_ENCODER
                motor.motor.power = 0.0
            } else
                isInUse = true
        }
        return isInUse
    }
}