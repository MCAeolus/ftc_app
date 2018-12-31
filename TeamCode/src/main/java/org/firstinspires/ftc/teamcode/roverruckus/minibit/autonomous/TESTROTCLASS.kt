package org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.teamcode.roverruckus.minibit.HARDWARENAMES_MINIBOT
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.LiftSystem
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.MiniTankDrive
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Autonomous(name = "enc test")@Disabled
class TESTROTCLASS : AutonomousBase(true, false) {



    override fun runOpMode() {
        super.runOpMode()

        waitForStart()

        drive(distance = 5.0)
        hold(500)
        turn(angle = 45.0)
        hold(500)
        drive(distance = -5.0)
        hold(500)
        turn(angle = 90.0)
        hold(500)
        drive(distance = 5.0)
        hold(500)
        turn(angle = 135.0)
        hold(500)
        drive(distance = -5.0)
    }


}