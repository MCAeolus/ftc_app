package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.robotcore.external.ClassFactory
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer
import org.firstinspires.ftc.robotcore.external.tfod.Recognition
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector
import org.firstinspires.ftc.teamcode.common.controller.Button
import org.firstinspires.ftc.teamcode.common.controller.SmidaGamepad
import org.firstinspires.ftc.teamcode.roverruckus.HNAMES_RUCKUS
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous.AutonomousBase
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.RobotInstance
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.replay_v2.Player
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.replay_v2.ReplayFile
import java.io.File
import kotlin.math.absoluteValue


@Autonomous(name="Autonomous Mode")
class Autonomous : LinearOpMode() {
    companion object {
        const val operatorName = "__OPERATION"

        fun isOperationDirectory(baseDir: File, dirPath: String): Boolean {
            val f = File(baseDir, dirPath)
            if (f.exists()) return f.list().contains(operatorName)
            return false
        }
    }

    private val defaultDir = "Autonomous"

    lateinit var robot: RobotInstance
        private set

    lateinit var vuforia : VuforiaLocalizer

    lateinit var tensorFlow : TFObjectDetector

    override fun runOpMode() {

        robot = RobotInstance(this)
        robot.start()

        initializeVuforia()
        initializeTensorFlow()

        val baseDir = hardwareMap.appContext.getExternalFilesDir(ReplayFile.EXTERNAL_DIRECTORY_HEADING)
        var currentDir = defaultDir

        var selectorLoc = 0

        var samplePosition = SampleLocation.N_A
        var lastKnownSample = SampleLocation.N_A

        var operationDir: String? = null

        val pad1 = SmidaGamepad(gamepad1, this)
        val button: (SmidaGamepad.GamePadButton) -> Button = pad1::getButton

        val buttonDelta = 0.25

        if (!baseDir.list().contains(defaultDir)) File(baseDir, defaultDir).mkdirs()

        tensorFlow.activate()
        while (!isStarted) {
            pad1.handleUpdate()
            samplePosition = findGoldPosition(tensorFlow.updatedRecognitions)
            if(samplePosition != SampleLocation.N_A) lastKnownSample = samplePosition

            val dir = File(baseDir, currentDir)

            val dirList = dir.list { _: File, s: String -> !s.endsWith(ReplayFile.REPLAY_FILE_SUFFIX) }

            if (button(SmidaGamepad.GamePadButton .PAD_UP).holdingTimeCheck(buttonDelta, time))
                if (selectorLoc > 0) selectorLoc--

            if (button(SmidaGamepad.GamePadButton.PAD_DOWN).holdingTimeCheck(buttonDelta, time))
                if (selectorLoc < dirList.lastIndex) selectorLoc++

            if (button(SmidaGamepad.GamePadButton.A).isIndividualActionButtonPress())
                if (isOperationDirectory(baseDir, currentDir + "/" + dirList[selectorLoc]))
                    operationDir = currentDir + "/" + dirList[selectorLoc]
                else {
                    currentDir += "/" + dirList[selectorLoc]
                    selectorLoc = 0
                }

            if (button(SmidaGamepad.GamePadButton.B).isIndividualActionButtonPress()) {
                if (operationDir != null) operationDir = null
                else if (currentDir != defaultDir) {
                    val thLindex = currentDir.lastIndexOf('/')
                    currentDir = currentDir.substring(0, if (thLindex < 0) defaultDir.length else thLindex)
                    selectorLoc = 0
                }
            }

            if (button(SmidaGamepad.GamePadButton.LEFT_BUMPER).isIndividualActionButtonPress()) {
                val selDir = currentDir + "/" + dirList[selectorLoc]

                if (isOperationDirectory(baseDir, selDir))
                    File(dir, dirList[selectorLoc]).listFiles().filter { it.name == operatorName }.forEach { it.delete() }
                else {
                    makeFile(operatorName, selDir)
                    makeFile("${SampleLocation.LEFT.name}${ReplayFile.REPLAY_FILE_SUFFIX}", selDir)
                    makeFile("${SampleLocation.CENTER.name}${ReplayFile.REPLAY_FILE_SUFFIX}", selDir)
                    makeFile("${SampleLocation.RIGHT.name}${ReplayFile.REPLAY_FILE_SUFFIX}", selDir)
                }
            }

            if (operationDir != null) {
                send("Current selected operation mode: ${operationDir.substringAfterLast('/')}")
                send("Ready to run.")
            } else {

                send("Please select the proper operation mode.")
                send("Use ↕ DPAD buttons to move selector, use 'A' to make the selection.")
                send("Use 'B' button to undo an operation selection or go back a directory.")
                send("Use 'left bumper' to designate or un-designate a directory as an operation directory.")
                send("")

                for (i in 0 until dirList.size)
                    send("${if (selectorLoc == i) ">>>" else "   "}${dirList[i]}", if (isOperationDirectory(baseDir, currentDir + "/" + dirList[i])) ": OPERATION MODE" else "->")
            }
            send("")
            send("Currently detecting sample: ${samplePosition.name}, last known ${lastKnownSample.name}")
            telemetry.update()
        }

        tensorFlow.deactivate()
        if (operationDir == null) requestOpModeStop()
        else {
            val usePosition = if(lastKnownSample == SampleLocation.N_A) SampleLocation.CENTER else lastKnownSample
            val USE_RECORD = ReplayFile.DataStream(operationDir + "/${usePosition.name}${ReplayFile.REPLAY_FILE_SUFFIX}", hardwareMap)
            USE_RECORD.load()
            USE_RECORD.prepare()

            Player.runReplay(USE_RECORD, this, robot)
        }
    }

    private fun initializeTensorFlow() {
        val tfodMonitorViewId = hardwareMap.appContext.resources.getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.packageName)
        val tfodParameters = TFObjectDetector.Parameters(tfodMonitorViewId)
        tfodParameters.minimumConfidence = 0.3 //TODO check this out
        tensorFlow = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia)
        tensorFlow.loadModelFromAsset("RoverRuckus.tflite", HNAMES_RUCKUS.GOLD_SAMPLE_TAG, HNAMES_RUCKUS.SILVER_SAMPLE_TAG)
    }

    private fun initializeVuforia() {
        val vu_param = VuforiaLocalizer.Parameters()

        vu_param.vuforiaLicenseKey = HNAMES_RUCKUS.VUFORIA_KEY
        val camName = hardwareMap.get(WebcamName::class.java, "Webcam 1")
        vu_param.cameraName = camName

        vuforia = ClassFactory.getInstance().createVuforia(vu_param)
    }

    enum class SampleLocation(val screenPos : Int) {
        LEFT(660),
        CENTER(320),
        RIGHT(-2),
        N_A(0)
    }

    private fun findGoldPosition(recognitions: List<Recognition>?): SampleLocation { //TODO assuming can see all locations and is upside down
        if (recognitions != null) {
            var goldRe: Recognition? = null

            recognitions.forEach {
                if (it.label == HNAMES_RUCKUS.GOLD_SAMPLE_TAG)
                    goldRe = it
            }
            if (goldRe == null) return SampleLocation.N_A
            else {
                val leftPosition = goldRe!!.left

                telemetry.log().add("location on screen $leftPosition")

                val dL = (leftPosition - SampleLocation.LEFT.screenPos).absoluteValue
                val dM = (leftPosition - SampleLocation.CENTER.screenPos).absoluteValue
                val dR = (leftPosition - SampleLocation.RIGHT.screenPos).absoluteValue
                telemetry.log().add("pos's $dL $dM $dR")

                if(dL < dM && dL < dR) return SampleLocation.LEFT
                else if(dR < dM && dR < dL) return SampleLocation.RIGHT
                else return SampleLocation.CENTER
            }

        }
        else return SampleLocation.N_A
    }

    private fun waitingForStart() {
        while(!isStarted) {
            telemetry.addData("Status", "waiting for start.")
            telemetry.update()
        }
    }

    private fun makeFile(file: String, dir: String = "") {
        val f = File(hardwareMap.appContext.getExternalFilesDir(ReplayFile.EXTERNAL_DIRECTORY_HEADING), "$dir/$file")
        if (!f.exists()) f.createNewFile()
    }

    private fun send(h: String, p: String = "", separator: String = "", update: Boolean = false, refresh: Boolean = true) {
        telemetry.captionValueSeparator = separator
        telemetry.isAutoClear = refresh
        telemetry.addData(h, p)
        if (update) telemetry.update()
    }
}