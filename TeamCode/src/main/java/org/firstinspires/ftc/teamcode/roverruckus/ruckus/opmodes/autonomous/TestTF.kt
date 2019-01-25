package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous

@Autonomous(name="TF Tester")
class TestTF : AutonomousBase(true) {

    override fun runOpMode() {
        super.runOpMode()

        telemetry.addData("STATUS", "waiting for start button.")
        telemetry.update()
        waitingForStart()


        TFOD.activate()

        var position = SamplePosition.N_A

        while(opModeIsActive()){
            position = findSample_THREE(TFOD.updatedRecognitions)

            telemetry.addData("position of gold", position.name)

            telemetry.update()

            hold(500)

        }
    }
}