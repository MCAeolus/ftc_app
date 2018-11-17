package org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous

import android.drm.DrmInfoEvent
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.tfod.Recognition
import org.firstinspires.ftc.teamcode.common.util.SmidaTelemetry
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.LiftSystem
import org.tensorflow.lite.TensorFlowLite
import java.util.concurrent.ThreadPoolExecutor


class AutonomousBasicMini : AutonomousBase(true, false) {

    val timer_it = ElapsedTime()

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

        rot_noPID(15.0, RotationType.THREE_SIXTY)

        hold(500)

        time_drive(0.0, 0.0, 1.0, 500)

        time_drive(0.0, 1.0, 0.0, 500)



        // LIFT.setLiftPosition(LiftSystem.LiftPosition.CAMERA)
        //LIFT.setLiftPosition(LiftSystem.LiftPosition.LIFTED)

        rot_noPID(0.0, RotationType.ONE_EIGHTY)
        hold(500)

        LIFT.setLiftPosition(LiftSystem.LiftPosition.LIFTED)

        /**
        TFOD.activate()
        var sample_position = SamplePosition.N_A
        timer_it.reset()
        while(opModeIsActive() && sample_position == SamplePosition.N_A && timer_it.seconds() < 5) {
            sample_position = findSample(TFOD.updatedRecognitions)
        }

        LIFT.setLiftPosition(LiftSystem.LiftPosition.LIFTED)

        telemetry.addData("sample at", sample_position)
        telemetry.update()


        TFOD.deactivate()

        hold(5000)

        **/

        hold(500)
        time_drive(0.0, 1.0, 0.0, 1500)


        /**

        if(sample_position != SamplePosition.N_A){
            telemetry.addData("sample ", sample_position.name)
            telemetry.update()
            hold(1000)
            time_drive(0.0, 1.0, 0.0, 500)

            when(sample_position) {
                SamplePosition.RIGHT -> {
                    rot_noPID(12.0, RotationType.THREE_SIXTY)
                    hold(500)
                    time_drive(0.0, 1.0, 0.0, 500)
                    hold(500)
                    rot_noPID(30.0, RotationType.THREE_SIXTY)
                    hold(500)
                    rot_noPID(0.0, RotationType.ONE_EIGHTY)
                    time_drive(0.0, 1.0, 0.0, 1000)
                }
                SamplePosition.CENTER -> {
                    time_drive(0.0, 1.0, 0.0, 400)
                    hold(500)
                    rot_noPID(30.0, RotationType.THREE_SIXTY)
                    hold(500)
                    rot_noPID(0.0, RotationType.ONE_EIGHTY)
                    hold(500)
                    time_drive(0.0, 1.0, 0.0, 1000)
                }
                SamplePosition.LEFT -> {
                    rot_noPID(-18.0, RotationType.THREE_SIXTY)
                    hold(500)
                    time_drive(0.0, 1.0, 0.0, 700)
                    hold(500)
                    rot_noPID(-35.0, RotationType.THREE_SIXTY)
                    hold(500)
                    rot_noPID(0.0, RotationType.ONE_EIGHTY)
                    time_drive(0.0, 1.0, 0.0, 1000)
                }
            }

        }else  // no sample found

        time_drive(0.0, 1.0, 0.0, 1500)

        **/

        DRIVETRAIN.stop()
    }

    fun time_drive(x : Double, y : Double, r : Double, t : Long) {
        timer_it.reset()
        DRIVETRAIN.move(x, y, r)
        hold(t)
        DRIVETRAIN.stop()
    }
}