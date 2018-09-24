package org.firstinspires.ftc.teamcode.roverruckus.minibit.robotmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.common.robot.Robot
import org.firstinspires.ftc.teamcode.roverruckus.minibit.HARDWARENAMES_MINIBOT
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.LiftSystem
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.MiniTankDrive

@TeleOp(name = "Minibot-Teleop")
class TeleopMinibot : Robot(MiniTankDrive(), mapOf(Pair("Lift", LiftSystem()))) {

    lateinit var LIFT_SYSTEM : LiftSystem

    override fun start() {
        LIFT_SYSTEM = COMPONENTS["Lift"] as LiftSystem
    }

    override fun loop() {

        DRIVETRAIN.move(0.0, gamepad1.left_stick_y.toDouble(), gamepad1.left_stick_x.toDouble())
        telemetry.addData("left lift motor", LIFT_SYSTEM.lift_motorL.currentPosition)
        telemetry.addData("right lift motor", LIFT_SYSTEM.lift_motorR.currentPosition)

    }
}