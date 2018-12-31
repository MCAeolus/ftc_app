package org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.LiftSystem

@Autonomous(name = "Autonomous TF - 1 sample")@Disabled
class AutonomousTFOneSample : AutonomousBase(true, true) {

    val timer_it = ElapsedTime()

    override fun runOpMode() {
        super.runOpMode()

        waitingForStart()

        TFOD.activate()
        var sample_position = SamplePosition.N_A
        timer_it.reset()
        while(opModeIsActive() && timer_it.seconds() < 5) {
            sample_position = findSample_THREE(TFOD.updatedRecognitions)
            if (sample_position != SamplePosition.N_A && timer_it.seconds() > 1) break
        }

        telemetry.addData("sample at", sample_position)
        telemetry.update()

        TFOD.deactivate()

        LIFT.manual_run(1.0)
        hold(500)

        LIFT.setHookPosition(LiftSystem.HookPosition.UNHOOKED)
        hold(50)

        LIFT.manual_run(1.0)
        hold(200)
        LIFT.lift_motorR.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        LIFT.lift_motorL.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        LIFT.manual_run(0.0)
        hold(2000)

        LIFT.lift_motorR.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        LIFT.lift_motorL.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        turn(angle = 25.0)
        hold(500)

        LIFT.setLiftPosition(LiftSystem.LiftPosition.LIFTED)

        hold(500)

        turn(angle = 0.0)
        hold(500)

        when(sample_position) {
            SamplePosition.LEFT -> {
                turn(angle = 40.0)
                drive(distance = 22.0)
            }
            SamplePosition.N_A,
            SamplePosition.CENTER -> {
                drive(distance = 20.0)
            }
            SamplePosition.RIGHT -> {
                turn(angle = -40.0)
                drive(distance = 22.0)
            }
        }

        turn(angle = 0.0)

        drive(distance = 9.0)
        DRIVETRAIN.stop()
    }

    fun time_drive(x : Double, y : Double, r : Double, t : Long) {
        timer_it.reset()
        DRIVETRAIN.move(x, y, r)
        hold(t)
        DRIVETRAIN.stop()
    }
}