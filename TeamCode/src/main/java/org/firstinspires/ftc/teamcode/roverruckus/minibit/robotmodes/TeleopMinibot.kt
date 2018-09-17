package org.firstinspires.ftc.teamcode.roverruckus.minibit.robotmodes

import org.firstinspires.ftc.teamcode.common.robot.Robot
import org.firstinspires.ftc.teamcode.roverruckus.minibit.HARDWARENAMES_MINIBOT
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.MiniTankDrive

class TeleopMinibot : Robot(MiniTankDrive(), mapOf(/*COMPONENTS*/)) {

    override fun loop() {

        DRIVETRAIN.move(gamepad1.left_stick_y.toDouble(), gamepad1.right_stick_y.toDouble(), 0.0)

    }
}