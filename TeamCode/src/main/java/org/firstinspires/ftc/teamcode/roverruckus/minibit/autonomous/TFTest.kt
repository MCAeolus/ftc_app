package org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous

@Autonomous(name="tf test")
class TFTest : AutonomousBase(true, true) {

    override fun runOpMode() {
        super.runOpMode()
        waitForStart()

        TFOD.activate()

        while(opModeIsActive()){
            val recog = TFOD.updatedRecognitions
            if(recog != null) {


                val ml = linkedMapOf<String, Any>()

                for(r in recog)
                    ml.put(r.label, r.left)

                telemetryUpdate(ml)
            }
        }
    }
}