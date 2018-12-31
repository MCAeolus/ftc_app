package org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled

@Autonomous(name="ramp up")@Disabled
class RampServoAuton : AutonomousBase(true, false) {

    override fun runOpMode() {
        super.runOpMode()

        LIFT.manualHook(0.0)

        waitingForStart()

        var rampUp = true
        val increment = 0.05

        while(opModeIsActive()) {
            if (rampUp) {
                LIFT.manualHook(LIFT.hook_left.position + increment)
                if (LIFT.hook_left.position == 1.0) rampUp = false
            } else {
                LIFT.manualHook(LIFT.hook_left.position - increment)
                if (LIFT.hook_left.position == 0.0) rampUp = true
            }

            telemetry.addData("servo left", LIFT.hook_left.position)
            telemetry.addData("servo right", LIFT.hook_right.position)
            telemetry.update()

            hold(50)
        }
    }
}