package org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous

import android.drm.DrmInfoEvent
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.tfod.Recognition
import org.firstinspires.ftc.teamcode.common.util.SmidaTelemetry
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.LiftSystem
import org.tensorflow.lite.TensorFlowLite
import java.util.concurrent.ThreadPoolExecutor


@Autonomous(name="Autonomous TF- tot & samp")@Disabled
class AutonomousBasicMiniTF : AutonomousBase(true, true) {

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

        if(sample_position != SamplePosition.LEFT)turn(angle = 0.0)
        hold(500)

        var turn_sample_angle = 0.0
        var move_sample_dist = 0.0
        var move_post = 0.0

        when(sample_position) {
            SamplePosition.LEFT -> {
                turn_sample_angle = 40.0
                move_sample_dist = 22.0
                move_post = 26.0
            }
            SamplePosition.N_A,
            SamplePosition.CENTER -> {
                move_sample_dist = 20.0
                move_post = 42.0
            }
            SamplePosition.RIGHT -> {
                turn_sample_angle = -40.0
                move_sample_dist = 22.0
                move_post = 50.0
            }
        }

        turn(angle = turn_sample_angle)
        drive(distance = move_sample_dist)

        turn(angle = 0.0)

        drive(distance = 8.0)
        hold(50)
        drive(distance = -15.0)

        turn(angle = 90.0)

        drive(distance = move_post)
        turn(angle = 320.0)

        drive(distance = -30.0)

        TOTEM.dump()
        hold(1000)
        TOTEM.reset()

        drive(distance = 60.0)

        DRIVETRAIN.stop()
    }

    fun time_drive(x : Double, y : Double, r : Double, t : Long) {
        timer_it.reset()
        DRIVETRAIN.move(x, y, r)
        hold(t)
        DRIVETRAIN.stop()
    }
}