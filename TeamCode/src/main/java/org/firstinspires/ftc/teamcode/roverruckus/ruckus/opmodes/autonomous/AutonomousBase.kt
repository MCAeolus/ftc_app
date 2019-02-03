package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.ClassFactory
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer
import org.firstinspires.ftc.robotcore.external.tfod.Recognition
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector
import org.firstinspires.ftc.teamcode.common.common_machines.IMU
import org.firstinspires.ftc.teamcode.common.robot.LinearRobot
import org.firstinspires.ftc.teamcode.relicrecovery.scorpion.machine.DummyMachine
import org.firstinspires.ftc.teamcode.roverruckus.minibit.HARDWARENAMES_MINIBOT
import org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous.AutonomousBase
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems.MecanumDriveTrain

open class AutonomousBase(val useTF : Boolean = false) : LinearRobot(MecanumDriveTrain(), mapOf("IMU" to IMU())) {

    val TF_GOLD_LABEL = "Gold Mineral"
    val TF_SILVER_LABEL = "Silver Mineral"

    lateinit var VUFORIA : VuforiaLocalizer
    lateinit var TFOD : TFObjectDetector

    lateinit var IMU : IMU

    val TIMER = ElapsedTime()



    override fun runOpMode() {
        super.runOpMode()

        IMU = COMPONENTS["IMU"] as IMU

        if(useTF && ClassFactory.getInstance().canCreateTFObjectDetector()) {
            useVuforia()
            useTFOD()
        }

        telemetry.addData("Status", "autonomous base completed loading.")
        telemetry.update()
    }

    fun useTFOD() {
        val tfodMonitorViewId = hardwareMap.appContext.resources.getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.packageName)
        val tfodParameters = TFObjectDetector.Parameters(tfodMonitorViewId)
        TFOD = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, VUFORIA)
        TFOD.loadModelFromAsset("RoverRuckus.tflite", TF_GOLD_LABEL, TF_SILVER_LABEL)
    }

    fun useVuforia() {
        val vu_param = VuforiaLocalizer.Parameters()

        vu_param.vuforiaLicenseKey = HARDWARENAMES_MINIBOT.VUFORIA_KEY.v
        vu_param.cameraName = hardwareMap.get(WebcamName::class.java, "Webcam 1")

        VUFORIA = ClassFactory.getInstance().createVuforia(vu_param)
    }

    fun hold(millis : Long){
        TIMER.reset()
        while(TIMER.milliseconds() < millis && opModeIsActive()){ idle() }
    }

    fun waitingForStart() {
        while(!isStarted) {
            telemetry.addData("Status", "waiting for start.")
            telemetry.addData("Current Direction", IMU.XYZ().thirdAngle)
            telemetry.update()
        }
    }

    enum class SamplePosition(val id : String) {
        LEFT("LEFT"),
        CENTER("CENTER"),
        RIGHT("RIGHT"),
        N_A("CENTER")
    }

    fun findSample_THREE(recog : List<Recognition>?) : SamplePosition {
        if(recog != null) {
            var gold_dist = -1F
            var silver1_dist = -1F
            var silver2_dist = -1F

            for(r in recog) {
                if (r.label == TF_SILVER_LABEL) {
                    if (silver1_dist > -1) silver2_dist = r.left
                    else silver1_dist = r.left
                } else if (r.label == TF_GOLD_LABEL) gold_dist = r.left
            }

            if(gold_dist > -1 && silver1_dist > -1 && silver2_dist > -1){
                if(gold_dist < silver1_dist && gold_dist < silver2_dist) return SamplePosition.LEFT
                else if((gold_dist < silver1_dist && gold_dist > silver2_dist) || (gold_dist < silver2_dist && gold_dist > silver1_dist)) return SamplePosition.CENTER
                else return SamplePosition.RIGHT
            }
            else return SamplePosition.N_A
        }

        else return SamplePosition.N_A
    }

    fun findfromLeftTwo(recog : List<Recognition>?) : SamplePosition {
        if(recog != null) {
            telemetry.addData("recogs", recog.size)
            telemetry.update()
            var gold_dist = -1F
            var silver1_dist = -1F
            var silver2_dist = -1F

            for(r in recog) {
                if (r.label == TF_SILVER_LABEL) {
                    if (silver1_dist > -1) silver2_dist = r.left
                    else silver1_dist = r.left
                } else if (r.label == TF_GOLD_LABEL) gold_dist = r.left
            }

            if(gold_dist > -1 && silver1_dist > -1 && silver2_dist > -1) {
                telemetry.addData("going three", "")
                telemetry.update()
                return findSample_THREE(recog)
            }
            else {
                if(gold_dist > -1 && silver1_dist > -1) {
                    if (gold_dist < silver1_dist) return SamplePosition.LEFT
                    else return SamplePosition.CENTER
                }
                else if(silver1_dist > -1 && silver2_dist > -1) return SamplePosition.RIGHT
                else return SamplePosition.N_A
                }
            }
        else return SamplePosition.N_A
    }
}