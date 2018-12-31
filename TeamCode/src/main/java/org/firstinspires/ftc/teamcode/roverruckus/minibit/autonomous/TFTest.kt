package org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.tfod.Recognition

@Autonomous(name="tf test")@Disabled
class TFTest : AutonomousBase(false, true) {

    val TIMER_TF = ElapsedTime()

    override fun runOpMode() {
        super.runOpMode()
        waitForStart()

        TFOD.activate()

        var position = SamplePosition.N_A

        while(opModeIsActive()){
            //while(position == SamplePosition.N_A && TIMER_TF.seconds() < 15)
            position = findSample_test(TFOD.updatedRecognitions)

            telemetry.addData("position of gold", position.name)

            telemetry.update()

            hold(500)

        }
    }

    fun findSample_test(recog : List<Recognition>?) : SamplePosition {
        if(recog != null) { //whether or not our neural system has recognized any objects on the field.
            var gold_dist = -1F
            var silver1_dist = -1F
            var silver2_dist = -1F

            for(r in recog) { //iterate through each recognized item
                if (r.label == TF_SILVER_LABEL) { //check what it is
                    if (silver1_dist > -1) silver2_dist = r.left
                    else silver1_dist = r.left
                } else if (r.label == TF_GOLD_LABEL) gold_dist = r.left
            }
            telemetry.addData("gold dist", gold_dist)
            telemetry.addData("silver 1 dist", silver1_dist)
            telemetry.addData("silver 2 dist", silver2_dist)

            if(gold_dist > -1 && silver1_dist > -1 && silver2_dist > -1){ //if all three samples can be seen
                if(gold_dist < silver1_dist && gold_dist < silver2_dist) return SamplePosition.LEFT
                else if(gold_dist < silver1_dist && gold_dist > silver2_dist) return SamplePosition.CENTER
                else return SamplePosition.RIGHT
            }else { //if only two samples can be seen
                val face = getSampleFace_test()
                telemetry.addData("sample face", face.name)
                if(silver1_dist > -1 && silver2_dist > -1) when(face){
                    SampleFace.LEFT -> return SamplePosition.RIGHT
                    SampleFace.RIGHT -> return SamplePosition.LEFT
                    else -> return SamplePosition.N_A
                }
                if(silver1_dist > -1 && gold_dist > -1) {
                    var goldIsLeft = false
                    if(silver1_dist > gold_dist) goldIsLeft = true

                    when (face) {
                        SampleFace.LEFT -> return if(goldIsLeft) SamplePosition.LEFT else SamplePosition.CENTER
                        SampleFace.RIGHT -> return if(goldIsLeft) SamplePosition.CENTER else SamplePosition.RIGHT
                        else -> return SamplePosition.CENTER
                    }
                }
                else return SamplePosition.N_A
            }
        }
        else return SamplePosition.N_A
    }

    fun getSampleFace_test() : SampleFace {
        val raw = IMU.getZ360()
        telemetry.addData("raw angle", raw)
        val center_diff = 10
        if    ((raw <= center_diff || raw >= (360-center_diff))          || //CENTER
               (raw >= (90 - center_diff) && raw <= (90 + center_diff))  ||
               (raw >= (180 - center_diff) && raw <= (180 + center_diff))||
               (raw >= (270 - center_diff) && raw <= (270 + center_diff)))return SampleFace.CENTER
        else if((raw < (360 - center_diff) && raw > 315)||                  //RIGHT
                (raw < (90 - center_diff) && raw > 45)  ||
                (raw < (180 - center_diff) && raw > 135)||
                (raw < (270 - center_diff) && raw > 225))return SampleFace.RIGHT
        else if((raw > (center_diff) && raw < 45)       ||                  //LEFT
                (raw > (90 + center_diff) && raw < 135) ||
                (raw > (180 + center_diff) && raw < 225)||
                (raw > (270 + center_diff) && raw < 315))return SampleFace.LEFT
        else return SampleFace.CENTER
    }
}