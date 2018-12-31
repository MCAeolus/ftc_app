package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.common.robot.Robot
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems.MecanumDriveTrain
@TeleOp(name="Ruckus")
open class RuckusOpMode : Robot(MecanumDriveTrain(), mapOf()){

    override fun start() {
    }


    override fun loop() {
        DRIVETRAIN.move(-gamepad1.left_stick_x.toDouble(), gamepad1.left_stick_y.toDouble(), -gamepad1.right_stick_x.toDouble())

    }
}