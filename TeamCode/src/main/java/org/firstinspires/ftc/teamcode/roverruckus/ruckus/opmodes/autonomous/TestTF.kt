package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.tfod.Recognition

//@Autonomous(name="TF Tester")
class TestTF : AutonomousBase(true) {

    override fun runOpMode() {
        super.runOpMode()

        telemetry.addData("STATUS", "waiting for start button.")
        telemetry.update()
        waitingForStart()


        TFOD.activate()

        var position = SamplePosition.N_A
        var pos2 = SamplePosition.N_A
        var pos3 = SamplePosition.N_A

        while(opModeIsActive()){
            //position = findSample_THREE(TFOD.updatedRecognitions)
            //pos2 = findfromLeftTwo(TFOD.updatedRecognitions)

            //telemetry.addData("position of gold", position.name)
            //telemetry.addData("From left", pos2.name)

            pos3 = findFromLeftByGold(TFOD.updatedRecognitions)

            telemetry.addData("pos", pos3)

            telemetry.update()

            hold(500)

        }
    }

    private fun testFind(recog : List<Recognition>?) : SamplePosition {
        if(recog != null) {
            var goldRe : Recognition? = null
            recog.forEach{
                if(it.label == TF_GOLD_LABEL)
                    goldRe = it
            }

            if(goldRe == null) return SamplePosition.RIGHT
            else {
                val ang = goldRe!!.estimateAngleToObject(AngleUnit.DEGREES)
                telemetry.addData("ang", ang)

                if(ang >= 7)return SamplePosition.LEFT
                else if(ang <= -16) return SamplePosition.RIGHT
                else return SamplePosition.CENTER
            }

        }
        return SamplePosition.N_A
    }
}