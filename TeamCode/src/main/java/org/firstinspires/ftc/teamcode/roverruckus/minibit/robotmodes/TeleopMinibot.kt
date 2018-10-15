package org.firstinspires.ftc.teamcode.roverruckus.minibit.robotmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.common.robot.Robot
import org.firstinspires.ftc.teamcode.roverruckus.minibit.HARDWARENAMES_MINIBOT
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.LiftSystem
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.MiniTankDrive

@TeleOp(name = "Minibot-Teleop")
class TeleopMinibot : Robot(MiniTankDrive(), mapOf(Pair("Lift", LiftSystem()))) {

    lateinit var LIFT_SYSTEM : LiftSystem

    var a1_pressed = false
    var x1_pressed = false

    override fun start() {
        LIFT_SYSTEM = COMPONENTS["Lift"] as LiftSystem
    }

    override fun loop() {

        DRIVETRAIN.move(0.0, gamepad1.left_stick_y.toDouble(), gamepad1.right_stick_x.toDouble())
        telemetry.addData("left lift motor", LIFT_SYSTEM.lift_motorL.currentPosition)
        telemetry.addData("right lift motor", LIFT_SYSTEM.lift_motorR.currentPosition)

        telemetry.addData("left hook pos", LIFT_SYSTEM.hook_left.position)
        telemetry.addData("right hook pos", LIFT_SYSTEM.hook_right.position)

        if(gamepad1.a && !a1_pressed && !x1_pressed){
            a1_pressed = true
            LIFT_SYSTEM.setLiftPosition(LiftSystem.Position.LOWERED)
        }else if(a1_pressed && !LIFT_SYSTEM.isLifting()){
            a1_pressed = false
        }

        if(gamepad1.x && !x1_pressed && !a1_pressed){
            x1_pressed = true
            LIFT_SYSTEM.setLiftPosition(LiftSystem.Position.LIFTED)
        }else if(x1_pressed && !LIFT_SYSTEM.isLifting()){
            x1_pressed = false
        }

        if(gamepad1.left_trigger > 0 && !(gamepad1.right_trigger > 0)){
            LIFT_SYSTEM.manual_run(-gamepad1.left_trigger.toDouble())
        }else if(!(gamepad1.left_trigger > 0) && gamepad1.right_trigger > 0){
            LIFT_SYSTEM.manual_run(gamepad1.right_trigger.toDouble())
        }else if (!x1_pressed && !a1_pressed){
            LIFT_SYSTEM.manual_run(0.0)
        }


    }
}