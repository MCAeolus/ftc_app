package org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous

import android.drm.DrmInfoEvent
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.common.util.SmidaTelemetry
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.LiftSystem


@Autonomous(name="Lift Test")
class LiftTest : AutonomousBase(true) {

    override fun runOpMode() {
        super.runOpMode()

        while(!isStarted() && opModeIsActive()) {
            telemetryUpdate(linkedMapOf(
                    "left motor" to LIFT.lift_motorL.currentPosition,
                    "right motor" to LIFT.lift_motorR.currentPosition
            ))
        }

        waitForStart()

        LIFT.manual_run(-1.0)
        hold(500)
        LIFT.setHookPosition(LiftSystem.HookPosition.UNHOOKED)
        hold(1000)
        LIFT.manual_run(0.0)
        LIFT.setLiftPosition(LiftSystem.LiftPosition.LOWERED)
        hold(1000)

        PID_rot(15.0, RotationType.THREE_SIXTY)
        hold(500)

        DRIVETRAIN.move(0.0, 1.0, 0.0)
        hold(500)
        DRIVETRAIN.stop()

        PID_rot(0.0, RotationType.ONE_EIGHTY)
        hold(500)
        DRIVETRAIN.move(0.0, 1.0, 0.0)
        LIFT.setLiftPosition(LiftSystem.LiftPosition.LIFTED)
        hold(1500)

        DRIVETRAIN.stop()
    }
}